package com.eliauk.momentary.model

import androidx.compose.runtime.Immutable

@Immutable
data class MomentEntry(
    val id: Long,
    val time: String,
    val text: String,
    val title: String? = null,
    val isFavorite: Boolean = false,
    val source: EntrySource = EntrySource.NOTE,
)

enum class EntrySource { NOTE, CHAT }

@Immutable
data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val isFavorite: Boolean = false,
    val isIncludedToday: Boolean = false,
)

@Immutable
data class Journal(
    val id: Long,
    val date: String,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false,
)

@Immutable
data class MomentaryUiState(
    val userName: String = "Eliauk",
    val selectedMood: String = "平静",
    val entries: List<MomentEntry> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val journals: List<Journal> = emptyList(),
    val isGenerating: Boolean = false,
)
