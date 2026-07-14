package com.eliauk.momentary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.eliauk.momentary.ui.components.SectionTitle
import com.eliauk.momentary.ui.theme.JournalBodyStyle
import com.eliauk.momentary.ui.theme.Sun

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "片刻日记本",
                    modifier = Modifier.size(148.dp).clip(RoundedCornerShape(42.dp)),
                    contentScale = ContentScale.Fit,
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("2026 年", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("${journals.size} 篇", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "从真实片刻里，\n慢慢装订出的生活。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            SectionTitle("按月浏览", modifier = Modifier.fillMaxWidth())
        }
        item {
            MonthGrid(journals.size)
        }
        item {
            SectionTitle("最近装订", modifier = Modifier.fillMaxWidth())
        }
        items(journals, key = { it.id }) { journal ->
            JournalIndexRow(journal = journal, onClick = { onJournalClick(journal) })
        }
    }
}

@Composable
private fun MonthGrid(currentCount: Int) {
    val months = listOf("7月" to "${currentCount}篇", "6月" to "0篇", "5月" to "12篇", "4月" to "18篇", "3月" to "15篇", "2月" to "10篇")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth().height(230.dp),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(months, key = { it.first }) { (month, count) ->
            PaperCard(
                modifier = Modifier.aspectRatio(1f),
                contentPadding = PaddingValues(12.dp),
                containerColor = if (month == "7月") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(month, style = MaterialTheme.typography.titleMedium)
                    Text(count, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun JournalIndexRow(journal: Journal, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.width(72.dp)) {
                Text(
                    journal.date.substringAfter("年").substringBefore(" ·"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text("已装订", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(journal.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    journal.body.replace("\n", " ").take(46) + if (journal.body.length > 46) "……" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(journal.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Text(journal.date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(journal.body, style = JournalBodyStyle, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = {}) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = null)
                    Text("收藏", Modifier.padding(start = 6.dp))
                }
                TextButton(onClick = {}) {
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
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("我的回响", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    "那些值得多读一次的话。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            FavoriteFilterTabs(selected = filter, onSelect = { filter = it })
        }
        if (filter != "AI回响") {
            items(favoriteEntries, key = { "entry_${it.id}" }) { entry ->
                FavoritePaper(
                    text = entry.text,
                    source = "我的笔记 · ${entry.time}",
                    onUnfavorite = { onToggleEntry(entry.id) },
                )
            }
        }
        if (filter != "我的笔记") {
            items(favoriteChats, key = { "chat_${it.id}" }) { message ->
                val personaName = state.personas.firstOrNull { it.id == message.personaId }?.name ?: "AI 回响"
                FavoritePaper(
                    text = message.text,
                    source = personaName,
                    onUnfavorite = { onToggleChat(message.id) },
                )
            }
        }
        if (favoriteEntries.isEmpty() && favoriteChats.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(36.dp))
                    Text("还没有收藏", style = MaterialTheme.typography.titleMedium)
                    Text("遇到想带走的话时，轻轻点一下星标。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun FavoriteFilterTabs(selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("全部", "AI回响", "我的笔记").forEach { label ->
            val isSelected = selected == label
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(label) }
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(
                    thickness = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                )
            }
        }
    }
}

@Composable
private fun FavoritePaper(text: String, source: String, onUnfavorite: () -> Unit) {
    PaperCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = onUnfavorite, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Star, "取消收藏", tint = Sun)
                }
            }
            Text(source, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("我的片刻", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Text("把陪伴方式调成你舒服的样子。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Eliauk", style = MaterialTheme.typography.titleLarge)
                        Text("片刻只整理生活，不替你定义生活。", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("本地", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
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
            Text("偏好", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        item {
            Column {
                SettingsRow(
                    icon = Icons.Filled.AutoAwesome,
                    title = "AI 陪伴风格",
                    description = "${activePersona.name} · ${activePersona.responseStyle}",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(
                    icon = Icons.Filled.Palette,
                    title = "导出样式",
                    description = "纸张长图 · 摘要卡片",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(
                    icon = Icons.Filled.DarkMode,
                    title = "深色模式",
                    description = "跟随系统，正在准备中",
                    trailing = { Switch(checked = darkMode, onCheckedChange = { darkMode = it }, enabled = false) },
                )
            }
        }
        item {
            Text("隐私与数据", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        item {
            Column {
                SettingsRow(
                    icon = Icons.Filled.Storage,
                    title = "本地存储",
                    description = "新增片刻保存在这台设备上",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(
                    icon = Icons.Filled.Lock,
                    title = "应用锁",
                    description = "将在接入正式数据层时开放",
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(
                    icon = Icons.Filled.DeleteOutline,
                    title = "清除本地数据",
                    description = "删除前会再次向你确认",
                )
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
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    description: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing?.invoke()
    }
}

@Composable
private fun PersonaListItem(persona: AiPersona, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(9.dp),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(persona.name, style = MaterialTheme.typography.titleMedium)
            Text(persona.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(persona.responseStyle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        if (selected) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "当前角色", tint = MaterialTheme.colorScheme.primary)
        }
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
