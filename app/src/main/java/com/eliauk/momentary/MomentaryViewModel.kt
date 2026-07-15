package com.eliauk.momentary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.eliauk.momentary.data.MomentaryStore
import com.eliauk.momentary.data.PrototypeAiService
import com.eliauk.momentary.domain.DiarySessionStateMachine
import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.ChatMessage
import com.eliauk.momentary.model.DiarySessionState
import com.eliauk.momentary.model.EntrySource
import com.eliauk.momentary.model.Journal
import com.eliauk.momentary.model.MomentEntry
import com.eliauk.momentary.model.MomentaryUiState
import com.eliauk.momentary.model.PersonaDefaults
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MomentaryViewModel(application: Application) : AndroidViewModel(application) {
    private val store = MomentaryStore(application)
    private val aiService = PrototypeAiService()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val today = LocalDate.now()
    private val todayIso = today.toString()
    private val todayLabel = buildTodayLabel(today)

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
    private val defaultChatMessages = listOf(
        ChatMessage(1, "最近总觉得有点累，提不起劲。", isUser = true),
        ChatMessage(
            2,
            "听起来你最近在消耗很多精力。你愿意和我聊聊，最近让你累的是什么吗？",
            isUser = false,
        ),
    )
    private val defaultJournals = listOf(
        Journal(
            id = 1,
            date = "5月9日 · 星期五",
            title = "实验室小记",
            body = "高考完的那个暑假，我把大学生活想成一万种颜色。\n\n它应该是凌晨三点的烧烤摊，是社团招新时震天响的音箱，是躺在草坪上数星星数到睡着的夜晚，是遇见一个人，然后整个青春都亮起来。\n\n后来我在大学待了两年。两年足够让所有颜色褪成灰色。我终于搞明白一件事，大学不是迪士尼，大学是高中的加长续集。\n\n今天其实憋了一肚子话。想了想，说到底就是累。",
        )
    )
    private val loadedPersonas = store.loadPersonas().ifEmpty { PersonaDefaults.all() }
    private val loadedActivePersonaId = store.loadActivePersonaId()
        .takeIf { id -> loadedPersonas.any { it.id == id } }
        ?: PersonaDefaults.MOMENTARY_ID
    private val loadedDiarySession = DiarySessionStateMachine.forDate(store.loadDiarySession(), todayIso)
    private val _uiState = MutableStateFlow(
        MomentaryUiState(
            todayLabel = todayLabel,
            selectedMood = store.loadSelectedMood(),
            entries = loadedEntries,
            chatMessages = store.loadChatMessages().ifEmpty { defaultChatMessages },
            journals = store.loadJournals().ifEmpty { defaultJournals },
            personas = loadedPersonas,
            activePersonaId = loadedActivePersonaId,
            diarySession = loadedDiarySession,
        )
    )
    val uiState: StateFlow<MomentaryUiState> = _uiState.asStateFlow()

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
        store.saveSelectedMood(mood)
    }

    fun selectPersona(personaId: String) {
        if (_uiState.value.personas.none { it.id == personaId }) return
        _uiState.update { it.copy(activePersonaId = personaId) }
        store.saveActivePersonaId(personaId)
    }

    fun createPersona(name: String, description: String, responseStyle: String) {
        val cleanName = name.trim()
        val cleanDescription = description.trim()
        val cleanStyle = responseStyle.trim()
        if (cleanName.isEmpty() || cleanDescription.isEmpty() || cleanStyle.isEmpty()) return
        val persona = AiPersona(
            id = "custom_${System.currentTimeMillis()}",
            name = cleanName,
            description = cleanDescription,
            responseStyle = cleanStyle,
        )
        _uiState.update { it.copy(personas = it.personas + persona, activePersonaId = persona.id) }
        store.savePersonas(_uiState.value.personas)
        store.saveActivePersonaId(persona.id)
    }

    fun addMoment(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val newEntry = MomentEntry(
            id = System.currentTimeMillis(),
            time = LocalTime.now().format(timeFormatter),
            text = clean,
        )
        _uiState.update { it.copy(entries = it.entries + newEntry) }
        collectDiaryEntryIfNeeded(newEntry.id)
        store.saveEntries(_uiState.value.entries)
    }

    fun startDiarySession() {
        val state = _uiState.value
        if (state.diarySession.state != DiarySessionState.IDLE) return
        val session = DiarySessionStateMachine.start(state.diarySession, System.currentTimeMillis())
        val opening = ChatMessage(
            id = System.currentTimeMillis() + 1,
            text = "今天也不必写得完整，想到什么，就慢慢说给我听。",
            isUser = false,
            personaId = state.activePersonaId,
        )
        _uiState.update { it.copy(diarySession = session, chatMessages = it.chatMessages + opening) }
        store.saveDiarySession(session)
        store.saveChatMessages(_uiState.value.chatMessages)
    }

    fun endDiarySession() {
        val session = _uiState.value.diarySession
        if (session.state != DiarySessionState.COLLECTING) return
        val generating = DiarySessionStateMachine.beginGeneration(session)
        _uiState.update { it.copy(diarySession = generating, isGenerating = true) }
        store.saveDiarySession(generating)

        val reviewing = DiarySessionStateMachine.markReviewing(generating)
        _uiState.update { it.copy(diarySession = reviewing, isGenerating = false) }
        store.saveDiarySession(reviewing)
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
        val persona = _uiState.value.personas.firstOrNull { it.id == _uiState.value.activePersonaId }
            ?: PersonaDefaults.all().first()
        val userMessage = ChatMessage(now, clean, isUser = true, personaId = persona.id)
        if (_uiState.value.diarySession.state == DiarySessionState.COLLECTING) {
            val entry = MomentEntry(
                id = now + 1,
                time = LocalTime.now().format(timeFormatter),
                text = clean,
                source = EntrySource.CHAT,
            )
            val receipt = ChatMessage(now + 2, "收到", isUser = false, personaId = persona.id)
            val session = DiarySessionStateMachine.collect(_uiState.value.diarySession, entry.id)
            _uiState.update {
                it.copy(
                    entries = it.entries + entry,
                    chatMessages = it.chatMessages + userMessage + receipt,
                    diarySession = session,
                )
            }
            store.saveEntries(_uiState.value.entries)
            store.saveChatMessages(_uiState.value.chatMessages)
            store.saveDiarySession(session)
            return
        }
        val aiMessage = ChatMessage(now + 1, aiService.replyTo(clean, persona), isUser = false, personaId = persona.id)
        _uiState.update { it.copy(chatMessages = it.chatMessages + userMessage + aiMessage) }
        store.saveChatMessages(_uiState.value.chatMessages)
    }

    fun toggleChatFavorite(id: Long) {
        _uiState.update { state ->
            state.copy(chatMessages = state.chatMessages.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it })
        }
        store.saveChatMessages(_uiState.value.chatMessages)
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
            store.saveChatMessages(_uiState.value.chatMessages)
        }
    }

    fun saveTodayAsJournal() {
        if (_uiState.value.journals.none { it.date == todayLabel }) {
            val body = _uiState.value.entries.joinToString("\n\n") { it.text }
            val journal = Journal(
                id = System.currentTimeMillis(),
                date = todayLabel,
                title = "把片刻装订成今天",
                body = body,
            )
            _uiState.update { it.copy(journals = listOf(journal) + it.journals) }
            store.saveJournals(_uiState.value.journals)
        }
        val session = _uiState.value.diarySession
        if (session.state == DiarySessionState.REVIEWING) {
            val completed = DiarySessionStateMachine.complete(session, System.currentTimeMillis())
            _uiState.update { it.copy(diarySession = completed) }
            store.saveDiarySession(completed)
        }
    }

    private fun collectDiaryEntryIfNeeded(entryId: Long) {
        val session = _uiState.value.diarySession
        if (session.state != DiarySessionState.COLLECTING) return
        val updated = DiarySessionStateMachine.collect(session, entryId)
        _uiState.update { it.copy(diarySession = updated) }
        store.saveDiarySession(updated)
    }

    private fun buildTodayLabel(date: LocalDate): String {
        val datePart = date.format(DateTimeFormatterBuilder().appendPattern("M月d日").toFormatter(Locale.CHINA))
        val weekday = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
        return "$datePart · $weekday"
    }
}
