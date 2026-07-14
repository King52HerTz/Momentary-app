package com.eliauk.momentary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.ChatMessage
import com.eliauk.momentary.model.MomentaryUiState
import com.eliauk.momentary.ui.components.PaperCard
import com.eliauk.momentary.ui.components.RoundIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    personas: List<AiPersona>,
    activePersonaId: String,
    onBack: () -> Unit,
    onSelectPersona: (String) -> Unit,
    onSend: (String) -> Unit,
    onFavorite: (Long) -> Unit,
    onIncludeToday: (Long) -> Unit,
) {
    var draft by remember { mutableStateOf("") }
    val activePersona = personas.firstOrNull { it.id == activePersonaId } ?: personas.first()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("和${activePersona.name}聊聊") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = draft,
                        onValueChange = { draft = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("随时说说……") },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                    )
                    RoundIconButton(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "发送",
                        onClick = {
                            if (draft.isNotBlank()) {
                                onSend(draft)
                                draft = ""
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 12.dp,
                end = 16.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "persona_switcher", contentType = "persona_switcher") {
                PersonaStrip(personas, activePersonaId, onSelectPersona)
                Text(
                    activePersona.description,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (messages.isEmpty()) {
                item(key = "empty_message", contentType = "empty") {
                    PaperCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "我是${activePersona.name}。${activePersona.description}\n\n你想从哪一个片刻说起？",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            items(messages, key = { it.id }, contentType = { if (it.isUser) "user" else "ai" }) { message ->
                ChatBubble(message, activePersona.name, onFavorite, onIncludeToday)
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    personaName: String,
    onFavorite: (Long) -> Unit,
    onIncludeToday: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        PaperCard(
            modifier = Modifier.fillMaxWidth(if (message.isUser) 0.82f else 0.9f),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 13.dp),
            containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            shape = if (message.isUser) {
                RoundedCornerShape(topStart = 8.dp, topEnd = 2.dp, bottomEnd = 8.dp, bottomStart = 8.dp)
            } else {
                RoundedCornerShape(topStart = 2.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 8.dp)
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                if (!message.isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(personaName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(message.text, style = MaterialTheme.typography.bodyLarge)
                if (!message.isUser) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { onFavorite(message.id) }) {
                            Icon(
                                if (message.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp),
                            )
                            Text(if (message.isFavorite) "已收藏" else "收藏", Modifier.padding(start = 6.dp))
                        }
                        TextButton(
                            onClick = { onIncludeToday(message.id) },
                            enabled = !message.isIncludedToday,
                        ) {
                            Icon(if (message.isIncludedToday) Icons.Filled.Check else Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(19.dp))
                            Text(if (message.isIncludedToday) "已收录" else "收录到今天", Modifier.padding(start = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonaStrip(
    personas: List<AiPersona>,
    activePersonaId: String,
    onSelectPersona: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        personas.forEach { persona ->
            val selected = persona.id == activePersonaId
            Column(
                modifier = Modifier
                    .widthIn(min = 68.dp)
                    .clickable { onSelectPersona(persona.id) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    persona.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                )
            }
        }
    }
}

private data class SummaryBlock(val title: String, val body: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(state: MomentaryUiState, onBack: () -> Unit, onSave: () -> Unit) {
    val blocks = listOf(
        SummaryBlock("今天发生了什么", state.entries.take(3).joinToString("\n") { "• ${it.text.substringBefore('\n')}" }),
        SummaryBlock("心情和状态", "整体心情以${state.selectedMood}为主。一天里有疲惫，也有和朋友相处后的松弛。"),
        SummaryBlock("灵感与想法", "把生活想成许多种颜色，每一种都值得被认真经历。"),
        SummaryBlock("感谢", "感谢有人愿意听自己说话，也感谢今天留下这些诚实的片刻。"),
        SummaryBlock("未完成待办", "□ 整理实验数据\n□ 准备下周汇报\n□ 给自己留一个不赶路的晚上"),
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("今日总结")
                    Text(state.todayLabel, style = MaterialTheme.typography.labelMedium)
                } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Column {
                        blocks.forEachIndexed { index, block ->
                            SummarySection(
                                number = (index + 1).toString().padStart(2, '0'),
                                block = block,
                            )
                            if (index < blocks.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                "片刻的回应",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.semantics { heading() },
                            )
                        }
                        Text(
                            "你并不是没有做成什么。你今天照顾了身体、认真听了朋友，也把那些容易消失的感受留下来了。生活不总靠巨大的进展向前，有时只是把注意力放回真正重要的地方。",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("装订成今天", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SummarySection(number: String, block: SummaryBlock) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            number,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(min = 24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                block.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                block.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
