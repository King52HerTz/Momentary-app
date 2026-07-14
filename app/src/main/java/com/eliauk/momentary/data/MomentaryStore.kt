package com.eliauk.momentary.data

import android.content.Context
import androidx.core.content.edit
import com.eliauk.momentary.model.EntrySource
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

    private companion object {
        const val KEY_ENTRIES = "entries"
    }
}
