package com.eliauk.momentary.data

import android.content.Context
import androidx.core.content.edit
import com.eliauk.momentary.model.AiPersona
import com.eliauk.momentary.model.ChatMessage
import com.eliauk.momentary.model.EntrySource
import com.eliauk.momentary.model.Journal
import com.eliauk.momentary.model.MomentEntry
import org.json.JSONArray
import org.json.JSONObject

class MomentaryStore(context: Context) {
    private val preferences = context.getSharedPreferences("momentary_local", Context.MODE_PRIVATE)

    fun loadEntries(): List<MomentEntry> {
        val raw = preferences.getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                repeat(array.length()) { index ->
                    val item = array.getJSONObject(index)
                    add(
                        MomentEntry(
                            id = item.getLong("id"),
                            time = item.getString("time"),
                            text = item.getString("text"),
                            title = item.optString("title").takeIf(String::isNotBlank),
                            isFavorite = item.optBoolean("favorite"),
                            source = EntrySource.valueOf(item.optString("source", "NOTE")),
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveEntries(entries: List<MomentEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("time", entry.time)
                    .put("text", entry.text)
                    .put("title", entry.title.orEmpty())
                    .put("favorite", entry.isFavorite)
                    .put("source", entry.source.name)
            )
        }
        preferences.edit { putString(KEY_ENTRIES, array.toString()) }
    }

    fun loadChatMessages(): List<ChatMessage> = readArray(KEY_CHAT_MESSAGES) { item ->
        ChatMessage(
            id = item.getLong("id"),
            text = item.getString("text"),
            isUser = item.getBoolean("user"),
            personaId = item.optString("personaId", "momentary"),
            isFavorite = item.optBoolean("favorite"),
            isIncludedToday = item.optBoolean("includedToday"),
        )
    }

    fun saveChatMessages(messages: List<ChatMessage>) = writeArray(
        KEY_CHAT_MESSAGES,
        messages.map { message ->
            JSONObject()
                .put("id", message.id)
                .put("text", message.text)
                .put("user", message.isUser)
                .put("personaId", message.personaId)
                .put("favorite", message.isFavorite)
                .put("includedToday", message.isIncludedToday)
        },
    )

    fun loadJournals(): List<Journal> = readArray(KEY_JOURNALS) { item ->
        Journal(
            id = item.getLong("id"),
            date = item.getString("date"),
            title = item.getString("title"),
            body = item.getString("body"),
            isFavorite = item.optBoolean("favorite"),
        )
    }

    fun saveJournals(journals: List<Journal>) = writeArray(
        KEY_JOURNALS,
        journals.map { journal ->
            JSONObject()
                .put("id", journal.id)
                .put("date", journal.date)
                .put("title", journal.title)
                .put("body", journal.body)
                .put("favorite", journal.isFavorite)
        },
    )

    fun loadPersonas(): List<AiPersona> = readArray(KEY_PERSONAS) { item ->
        AiPersona(
            id = item.getString("id"),
            name = item.getString("name"),
            description = item.getString("description"),
            responseStyle = item.getString("responseStyle"),
            isBuiltIn = item.optBoolean("builtIn"),
        )
    }

    fun savePersonas(personas: List<AiPersona>) = writeArray(
        KEY_PERSONAS,
        personas.map { persona ->
            JSONObject()
                .put("id", persona.id)
                .put("name", persona.name)
                .put("description", persona.description)
                .put("responseStyle", persona.responseStyle)
                .put("builtIn", persona.isBuiltIn)
        },
    )

    fun loadSelectedMood(): String = preferences.getString(KEY_SELECTED_MOOD, "平静") ?: "平静"

    fun saveSelectedMood(mood: String) = preferences.edit { putString(KEY_SELECTED_MOOD, mood) }

    fun loadActivePersonaId(): String = preferences.getString(KEY_ACTIVE_PERSONA, "momentary") ?: "momentary"

    fun saveActivePersonaId(personaId: String) = preferences.edit { putString(KEY_ACTIVE_PERSONA, personaId) }

    private fun <T> readArray(key: String, transform: (JSONObject) -> T): List<T> {
        val raw = preferences.getString(key, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                repeat(array.length()) { index -> add(transform(array.getJSONObject(index))) }
            }
        }.getOrDefault(emptyList())
    }

    private fun writeArray(key: String, objects: List<JSONObject>) {
        val array = JSONArray()
        objects.forEach(array::put)
        preferences.edit { putString(key, array.toString()) }
    }

    private companion object {
        const val KEY_ENTRIES = "entries"
        const val KEY_CHAT_MESSAGES = "chat_messages"
        const val KEY_JOURNALS = "journals"
        const val KEY_PERSONAS = "personas"
        const val KEY_SELECTED_MOOD = "selected_mood"
        const val KEY_ACTIVE_PERSONA = "active_persona"
    }
}
