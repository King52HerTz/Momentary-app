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
    val personaId: String = PersonaDefaults.MOMENTARY_ID,
    val isFavorite: Boolean = false,
    val isIncludedToday: Boolean = false,
)

@Immutable
data class AiPersona(
    val id: String,
    val name: String,
    val description: String,
    val responseStyle: String,
    val isBuiltIn: Boolean = false,
)

object PersonaDefaults {
    const val MOMENTARY_ID = "momentary"
    const val STORY_ID = "story"
    const val CLARITY_ID = "clarity"

    fun all(): List<AiPersona> = listOf(
        AiPersona(
            id = MOMENTARY_ID,
            name = "片刻",
            description = "先听懂你的感受，再陪你慢慢往下说。",
            responseStyle = "温柔、克制、少建议、会追问",
            isBuiltIn = true,
        ),
        AiPersona(
            id = STORY_ID,
            name = "故事陪伴者",
            description = "用有画面感的语言接住经历和情绪。",
            responseStyle = "温暖、有叙事感、保留生活细节",
            isBuiltIn = true,
        ),
        AiPersona(
            id = CLARITY_ID,
            name = "清晰复盘者",
            description = "帮你分清事实、感受和下一步。",
            responseStyle = "清楚、简洁、具体、不评判",
            isBuiltIn = true,
        ),
    )
}

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
    val todayLabel: String = "",
    val selectedMood: String = "平静",
    val entries: List<MomentEntry> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val journals: List<Journal> = emptyList(),
    val personas: List<AiPersona> = PersonaDefaults.all(),
    val activePersonaId: String = PersonaDefaults.MOMENTARY_ID,
    val isGenerating: Boolean = false,
)
