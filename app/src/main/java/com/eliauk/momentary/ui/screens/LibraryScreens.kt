package com.eliauk.momentary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eliauk.momentary.R
import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.Journal
import com.eliauk.momentary.model.MomentaryUiState
import com.eliauk.momentary.ui.components.PaperCard

@Composable
fun JournalsScreen(
    journals: List<Journal>,
    contentPadding: PaddingValues,
    onJournalClick: (Journal) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 24.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("我的本子", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = {}) { Icon(Icons.Filled.Search, contentDescription = "搜索日记") }
            }
        }
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "片刻日记本",
                    modifier = Modifier.size(190.dp).clip(RoundedCornerShape(48.dp)),
                    contentScale = ContentScale.Fit,
                )
                Text("已经留下 ${journals.size} 篇日记", style = MaterialTheme.typography.titleMedium)
                Text("每一篇都从真实的片刻开始", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Text("最近装订", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        items(journals, key = { it.id }) { journal ->
            PaperCard(modifier = Modifier.fillMaxWidth(), onClick = { onJournalClick(journal) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(journal.date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(journal.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(
                        journal.body.replace("\n", " ").take(92) + if (journal.body.length > 92) "……" else "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        item {
            Text("按月浏览", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        item {
            MonthGrid()
        }
    }
}

@Composable
private fun MonthGrid() {
    val months = listOf("7月" to "${1}篇", "5月" to "12篇", "4月" to "18篇", "3月" to "15篇", "2月" to "10篇", "1月" to "8篇")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth().height(230.dp),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(months, key = { it.first }) { (month, count) ->
            PaperCard(modifier = Modifier.aspectRatio(1f), contentPadding = PaddingValues(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(month, style = MaterialTheme.typography.titleMedium)
                    Text(count, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDetailScreen(journal: Journal, onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("日记") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Filled.Share, "导出或分享") }
                    IconButton(onClick = {}) { Icon(Icons.Filled.MoreHoriz, "更多") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(journal.title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text(journal.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Text(journal.body, style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = null)
                    Text("收藏", Modifier.padding(start = 6.dp))
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Text("生成长图", Modifier.padding(start = 6.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FavoritesScreen(
    state: MomentaryUiState,
    contentPadding: PaddingValues,
    onToggleEntry: (Long) -> Unit,
    onToggleChat: (Long) -> Unit,
) {
    var filter by remember { mutableStateOf("全部") }
    val favoriteEntries = state.entries.filter { it.isFavorite }
    val favoriteChats = state.chatMessages.filter { it.isFavorite }
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 24.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("我的回响", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("全部", "AI回响", "我的笔记").forEach { label ->
                    FilterChip(selected = filter == label, onClick = { filter = label }, label = { Text(label) })
                }
            }
        }
        if (filter != "AI回响") {
            items(favoriteEntries, key = { "entry_${it.id}" }) { entry ->
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(entry.text, style = MaterialTheme.typography.bodyLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("我的笔记 · ${entry.time}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            IconButton(onClick = { onToggleEntry(entry.id) }) { Icon(Icons.Filled.Favorite, "取消收藏") }
                        }
                    }
                }
            }
        }
        if (filter != "我的笔记") {
            items(favoriteChats, key = { "chat_${it.id}" }) { message ->
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(message.text, style = MaterialTheme.typography.bodyLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            val personaName = state.personas.firstOrNull { it.id == message.personaId }?.name ?: "AI 回响"
                            Text(personaName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { onToggleChat(message.id) }) { Icon(Icons.Filled.Favorite, "取消收藏") }
                        }
                    }
                }
            }
        }
        if (favoriteEntries.isEmpty() && favoriteChats.isEmpty()) {
            item {
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Text("还没有收藏。遇到想带走的话时，轻轻点一下爱心。", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    state: MomentaryUiState,
    contentPadding: PaddingValues,
    onSelectPersona: (String) -> Unit,
    onCreatePersona: (String, String, String) -> Unit,
) {
    var darkMode by remember { mutableStateOf(false) }
    var showCreatePersona by remember { mutableStateOf(false) }
    val activePersona = state.personas.firstOrNull { it.id == state.activePersonaId } ?: state.personas.first()
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 24.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Text("我的片刻", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) }
        item {
            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Eliauk", style = MaterialTheme.typography.titleLarge)
                    Text("片刻只帮助你整理，不替你定义生活。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("我的陪伴角色", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                IconButton(onClick = { showCreatePersona = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "创建陪伴角色")
                }
            }
        }
        items(state.personas, key = { "persona_${it.id}" }, contentType = { "persona" }) { persona ->
            PersonaListItem(
                persona = persona,
                selected = persona.id == activePersona.id,
                onClick = { onSelectPersona(persona.id) },
            )
        }
        item {
            PaperCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("AI 陪伴风格") },
                        supportingContent = { Text("当前：${activePersona.name} · ${activePersona.responseStyle}") },
                        leadingContent = { Icon(Icons.Filled.AutoAwesome, null) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("导出样式") },
                        supportingContent = { Text("纸张长图 · 摘要卡片") },
                        leadingContent = { Icon(Icons.Filled.Palette, null) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("深色模式") },
                        supportingContent = { Text("跟随系统的主题正在准备中") },
                        leadingContent = { Icon(Icons.Filled.DarkMode, null) },
                        trailingContent = { Switch(checked = darkMode, onCheckedChange = { darkMode = it }, enabled = false) },
                    )
                }
            }
        }
        item {
            Text("隐私与数据", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        item {
            PaperCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("本地存储") },
                        supportingContent = { Text("新增片刻保存在这台设备上") },
                        leadingContent = { Icon(Icons.Filled.Storage, null) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("应用锁") },
                        supportingContent = { Text("将在接入正式数据层时开放") },
                        leadingContent = { Icon(Icons.Filled.Lock, null) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("清除本地数据") },
                        leadingContent = { Icon(Icons.Filled.DeleteOutline, null) },
                    )
                }
            }
        }
    }

    if (showCreatePersona) {
        CreatePersonaDialog(
            onDismiss = { showCreatePersona = false },
            onCreate = { name, description, style ->
                onCreatePersona(name, description, style)
                showCreatePersona = false
            },
        )
    }
}

@Composable
private fun PersonaListItem(persona: AiPersona, selected: Boolean, onClick: () -> Unit) {
    PaperCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(0.dp),
    ) {
        ListItem(
            headlineContent = { Text(persona.name) },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(persona.description)
                    Text(persona.responseStyle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            },
            leadingContent = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
            trailingContent = {
                if (selected) Icon(Icons.Filled.CheckCircle, contentDescription = "当前角色", tint = MaterialTheme.colorScheme.primary)
            },
        )
    }
}

@Composable
private fun CreatePersonaDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }
    val canCreate = name.isNotBlank() && description.isNotBlank() && style.isNotBlank()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("创建陪伴角色") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("描述你需要的陪伴方式，不必模仿某一位真实作家。", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("角色名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("他如何陪伴你") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = style,
                    onValueChange = { style = it },
                    label = { Text("回应偏好") },
                    placeholder = { Text("例如：温暖、有画面感、少说教") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, description, style) }, enabled = canCreate) {
                Text("创建并使用")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}
