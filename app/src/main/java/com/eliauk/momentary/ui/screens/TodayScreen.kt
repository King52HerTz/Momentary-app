package com.eliauk.momentary.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eliauk.momentary.model.MomentEntry
import com.eliauk.momentary.model.MomentaryUiState
import com.eliauk.momentary.ui.components.PaperCard
import com.eliauk.momentary.ui.components.RoundIconButton

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
            top = contentPadding.calculateTopPadding() + 20.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            TodayHeader(state.userName)
        }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("此刻心情", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                    Text("轻轻点一下就好", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                MoodRow(selectedMood = state.selectedMood, onSelectMood = onSelectMood)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("今日片刻", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                OutlinedButton(onClick = onChat) {
                    Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null)
                    Text("和片刻聊聊", Modifier.padding(start = 8.dp))
                }
            }
        }
        items(state.entries, key = { it.id }, contentType = { "moment" }) { entry ->
            TimelineEntry(entry = entry, onToggleFavorite = { onToggleFavorite(entry.id) })
        }
        item {
            Button(
                onClick = onSummary,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text("整理今天", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TodayHeader(userName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Text("早上好，$userName", style = MaterialTheme.typography.headlineLarge)
        Text("7月14日 · 星期二", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CaptureCard(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    PaperCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(8.dp)) {
        Column {
            TextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("此刻想留下一点什么？") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(
                    imageVector = if (draft.isBlank()) Icons.Filled.KeyboardVoice else Icons.Filled.Send,
                    contentDescription = if (draft.isBlank()) "语音记录，尚未开放" else "保存片刻",
                    onClick = { if (draft.isNotBlank()) onSend() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodRow(selectedMood: String, onSelectMood: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(moods, key = { it.label }) { mood ->
            ElevatedFilterChip(
                selected = selectedMood == mood.label,
                onClick = { onSelectMood(mood.label) },
                label = { Text(mood.label) },
                leadingIcon = { Icon(mood.icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@Composable
private fun TimelineEntry(entry: MomentEntry, onToggleFavorite: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Text(
            entry.time,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
        PaperCard(modifier = Modifier.weight(1f)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (entry.title != null) {
                    Text(entry.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(entry.text, style = MaterialTheme.typography.bodyLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (entry.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (entry.isFavorite) "取消收藏" else "收藏片刻",
                            tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
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
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            placeholder = { Text("不用组织语言，想到什么就写什么。") },
        )
        Button(onClick = { onSave(text) }, enabled = text.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text("记下来")
        }
        OutlinedButton(onClick = { onChat(text) }, modifier = Modifier.fillMaxWidth()) {
            Text("带着它去聊聊")
        }
        Spacer(Modifier.height(16.dp))
    }
}
