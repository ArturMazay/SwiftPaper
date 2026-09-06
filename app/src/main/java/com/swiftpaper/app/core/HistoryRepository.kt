package com.swiftpaper.app.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.historyStore by preferencesDataStore(name = "swiftpaper_history")

data class HistoryItem(
    val id: String,
    val name: String,
    val path: String,
    val mimeType: String,
    val createdAt: Long
)

class HistoryRepository(private val context: Context) {
    private val itemsKey = stringPreferencesKey("items_json")

    val items: Flow<List<HistoryItem>> = context.historyStore.data.map { prefs ->
        parse(prefs[itemsKey].orEmpty())
    }

    suspend fun add(item: HistoryItem) {
        context.historyStore.edit { prefs ->
            val current = parse(prefs[itemsKey].orEmpty()).toMutableList()
            current.removeAll { it.id == item.id }
            current.add(0, item)
            prefs[itemsKey] = serialize(current.take(30))
        }
    }

    private fun parse(raw: String): List<HistoryItem> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        HistoryItem(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            path = obj.getString("path"),
                            mimeType = obj.getString("mimeType"),
                            createdAt = obj.getLong("createdAt")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun serialize(items: List<HistoryItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("name", item.name)
                    .put("path", item.path)
                    .put("mimeType", item.mimeType)
                    .put("createdAt", item.createdAt)
            )
        }
        return array.toString()
    }
}
