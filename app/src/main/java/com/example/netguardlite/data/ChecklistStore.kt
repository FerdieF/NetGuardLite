package com.example.netguardlite.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Harus top-level, dan hanya sekali per nama file
private val Context.dataStore by preferencesDataStore(name ="netguard_prefs")

class ChecklistStore(context: Context) {
    private val dataStore = context.applicationContext.dataStore
    private val key = stringSetPreferencesKey("checked_items")

    val checked: Flow<Set<String>> = dataStore.data.map { it[key] ?: emptySet() }

    suspend fun toggle(id: String) {
        dataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = if (id in current) current - id else current + id
        }
    }
}