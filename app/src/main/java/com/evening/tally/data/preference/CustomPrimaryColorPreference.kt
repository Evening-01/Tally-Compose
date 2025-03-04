package com.evening.tally.data.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.evening.tally.ext.DataStoreKeys
import com.evening.tally.ext.dataStore
import com.evening.tally.ext.put

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object CustomPrimaryColorPreference {

    const val default = ""

    fun put(context: Context, scope: CoroutineScope, value: String) {
        scope.launch {
            context.dataStore.put(DataStoreKeys.CustomPrimaryColor, value)
        }
    }

    fun fromPreferences(preferences: Preferences) =
        preferences[DataStoreKeys.CustomPrimaryColor.key] ?: default
}
