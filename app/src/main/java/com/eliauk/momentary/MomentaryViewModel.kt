package com.eliauk.momentary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.eliauk.momentary.data.MomentaryStore
import com.eliauk.momentary.data.PrototypeAiService
import com.eliauk.momentary.model.ChatMessage
import com.eliauk.momentary.model.EntrySource
import com.eliauk.momentary.model.Journal
import com.eliauk.momentary.model.MomentEntry
import com.eliauk.momentary.model.MomentaryUiState
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MomentaryViewModel(application: Application) : AndroidViewModel(application) {
    private val store = MomentaryStore(application)
    private val aiService = PrototypeAiService()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val initialEntries = listOf(
        MomentEntry(1, "08:30", "做了十分钟拉伸，身体舒服了很多。"),
        MomentEntry(2, "12:45", "和朋友吃了咖啡，聊了很多，有些话憋在心里，心里轻松了。"),
        MomentEntry(
            3,
            "16:20",
            "高考完的那个暑假，我把大学生活想成一万种颜色。",
            title = "实验室小记",
            isFavorite = true,
        ),
        MomentEntry(4, "21:10", "今天读到一句话，很触动：\n“慢慢来，比较快。”"),
    )

    private val loadedEntries = store.loadEntries().ifEmpty { initialEntries }
    private val _uiState = MutableStateFlow(
        MomentaryUiState(
            entries = loadedEntries,
            chatMessages = listOf(
                ChatMessage(1, "最近总觉得有点累，提不起劲。", isUser = true),
                ChatMessage(
                    2,
                    "听起来你最近在消耗很多精力。你愿意和我聊聊，最近让你累的是什么吗？",
                    isUser = false,
                ),
            ),
            journals = listOf(
                Journal(
                    id = 1,
                    date = "5月9日 · 星期五",
                    title = "实验室小记",
                    body = "高考完的那个暑假，我把大学生活想成一万种颜色。\n\n它应该是凌晨三点的烧烤摊，是社团招新时震天响的音箱，是躺在草坪上数星星数到睡着的夜晚，是遇见一个人，然后整个青春都亮起来。\n\n后来我在大学待了两年。两年足够让所有颜色褪成灰色。我终于搞明白一件事，大学不是迪士尼，大学是高中的加长续集。\n\n今天其实憋了一肚子话。想了想，说到底就是累。",
                )
            ),
        )
    )
    val uiState: StateFlow<MomentaryUiState> = _uiState.asStateFlow()

    fun selectMood(mood: String) = _uiState.update { it.copy(selectedMood = mood) }

    fun addMoment(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val newEntry = MomentEntry(
            id = System.currentTimeMillis(),
            time = LocalTime.now().format(timeFormatter),
            text = clean,
        )
        _uiState.update { it.copy(entries = it.entries + newEntry) }
        store.saveEntries(_uiState.value.entries)
    }

    fun toggleEntryFavorite(id: Long) {
        _uiState.update { state ->
            state.copy(entries = state.entries.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it })
        }
        store.saveEntries(_uiState.value.entries)
    }

    fun sendChat(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val now = System.currentTimeMillis()
        val userMessage = ChatMessage(now, clean, isUser = true)
        val aiMessage = ChatMessage(now + 1, aiService.replyTo(clean), isUser = false)
        _uiState.update { it.copy(chatMessages = it.chatMessages + userMessage + aiMessage) }
    }

    fun toggleChatFavorite(id: Long) = _uiState.update { state ->
        state.copy(chatMessages = state.chatMessages.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it })
    }

    fun includeChatToday(id: Long) {
        val message = _uiState.value.chatMessages.firstOrNull { it.id == id } ?: return
        if (!message.isIncludedToday) {
            val entry = MomentEntry(
                id = System.currentTimeMillis(),
                time = LocalTime.now().format(timeFormatter),
                text = message.text,
                source = EntrySource.CHAT,
            )
            _uiState.update { state ->
                state.copy(
                    entries = state.entries + entry,
                    chatMessages = state.chatMessages.map {
                        if (it.id == id) it.copy(isIncludedToday = true) else it
                    },
                )
            }
            store.saveEntries(_uiState.value.entries)
        }
    }

    fun saveTodayAsJournal() {
        if (_uiState.value.journals.any { it.date.startsWith("今天") }) return
        val body = _uiState.value.entries.joinToString("\n\n") { it.text }
        val journal = Journal(
            id = System.currentTimeMillis(),
            date = "今天 · 7月14日",
            title = "把片刻装订成今天",
            body = body,
        )
        _uiState.update { it.copy(journals = listOf(journal) + it.journals) }
    }
}
