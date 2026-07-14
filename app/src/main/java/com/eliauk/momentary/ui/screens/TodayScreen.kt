package com.eliauk.momentary.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eliauk.momentary.model.MomentEntry
import com.eliauk.momentary.model.MomentaryUiState
import com.eliauk.momentary.ui.components.PaperCard
import com.eliauk.momentary.ui.components.RoundIconButton
import com.eliauk.momentary.ui.components.SectionTitle
import java.time.LocalTime

private data class MoodOption(val label: String, val icon: ImageVector)

private val moods = listOf(
    MoodOption("开心", Icons.Filled.SentimentSatisfied),
    MoodOption("平静", Icons.Filled.SelfImprovement),
    MoodOption("难过", Icons.Filled.SentimentDissatisfied),
    MoodOption("焦虑", Icons.Filled.PsychologyAlt),
    MoodOption("疲惫", Icons.Filled.Bedtime),
)

@Composable
fun TodayScreen(
    state: MomentaryUiState,
    contentPadding: PaddingValues,
    onAddMoment: (String) -> Unit,
    onSelectMood: (String) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onChat: () -> Unit,
    onSummary: () -> Unit,
) {
    var draft by remember { mutableStateOf("") }
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item { TodayHeader(state.userName, state.todayLabel) }
        item {
            CaptureCard(
                draft = draft,
                onDraftChange = { draft = it },
                onSend = {
                    onAddMoment(draft)
                    draft = ""
                },
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle(
                    text = "此刻心情",
                    modifier = Modifier.fillMaxWidth(),
                    action = {
                        Text("不必解释", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                )
                MoodRow(selectedMood = state.selectedMood, onSelectMood = onSelectMood)
            }
        }
        item {
            SectionTitle(
                text = "今日片刻",
                modifier = Modifier.fillMaxWidth(),
                action = {
                    TextButton(onClick = onChat) {
                        Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("聊聊", Modifier.padding(start = 6.dp))
                    }
                },
            )
        }
        items(state.entries, key = { it.id }, contentType = { "moment" }) { entry ->
            TimelineEntry(entry = entry, onToggleFavorite = { onToggleFavorite(entry.id) })
        }
        item {
            Button(
                onClick = onSummary,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                Text("整理今天", Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TodayHeader(userName: String, todayLabel: String) {
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..10 -> "早上好"
            in 11..13 -> "中午好"
            in 14..18 -> "下午好"
            else -> "晚上好"
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("片刻", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = {}) {
                Icon(Icons.Filled.NotificationsNone, contentDescription = "提醒设置")
            }
        }
        Text("$greeting，$userName", style = MaterialTheme.typography.headlineLarge)
        Text(todayLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CaptureCard(draft: String, onDraftChange: (String) -> Unit, onSend: () -> Unit) {
    PaperCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(6.dp)) {
        Column {
            TextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp),
                placeholder = { Text("此刻想留下一点什么？") },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                RoundIconButton(
                    imageVector = if (draft.isBlank()) Icons.Filled.KeyboardVoice else Icons.AutoMirrored.Filled.Send,
                    contentDescription = if (draft.isBlank()) "语音记录，尚未开放" else "保存片刻",
                    onClick = { if (draft.isNotBlank()) onSend() },
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}

@Composable
private fun MoodRow(selectedMood: String, onSelectMood: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        moods.forEach { mood ->
            val selected = selectedMood == mood.label
            Column(
                modifier = Modifier
                    .widthIn(min = 54.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onSelectMood(mood.label) }
                    .semantics { role = Role.RadioButton }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(if (selected) MaterialTheme.shapes.medium else CircleShape)
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        mood.icon,
                        contentDescription = null,
                        modifier = Modifier.size(23.dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    mood.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TimelineEntry(entry: MomentEntry, onToggleFavorite: () -> Unit) {
    val lineColor = MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            entry.time,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(44.dp).padding(top = 14.dp),
        )
        Canvas(Modifier.width(8.dp).fillMaxHeight()) {
            val centerX = size.width / 2
            drawLine(lineColor, start = androidx.compose.ui.geometry.Offset(centerX, 0f), end = androidx.compose.ui.geometry.Offset(centerX, size.height), strokeWidth = 1.dp.toPx())
            drawCircle(color = lineColor, radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(centerX, 18.dp.toPx()))
        }
        PaperCard(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            containerColor = if (entry.title != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 2.dp, topEnd = 6.dp, bottomEnd = 6.dp, bottomStart = 2.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (entry.title != null) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(entry.text, style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (entry.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (entry.isFavorite) "取消收藏" else "收藏片刻",
                        tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun QuickCaptureSheet(onSave: (String) -> Unit, onChat: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("接住这个片刻", style = MaterialTheme.typography.headlineMedium)
        Text("不用组织语言，想到什么就写什么。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().height(132.dp),
            placeholder = { Text("写下此刻……") },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
        Button(
            onClick = { onSave(text) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) { Text("记下来") }
        OutlinedButton(onClick = { onChat(text) }, enabled = text.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null)
            Text("带着它去聊聊", Modifier.padding(start = 8.dp))
        }
        Spacer(Modifier.height(16.dp))
    }
}
