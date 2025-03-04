package com.evening.tally.data.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.evening.tally.ext.DataStoreKeys
import com.evening.tally.ext.dataStore
import com.evening.tally.ext.put

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class AmoledDarkThemePreference(val value: Boolean) : Preference() {
    object ON : AmoledDarkThemePreference(true)
    object OFF : AmoledDarkThemePreference(false)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.AmoledDarkTheme,
                value
            )
        }
    }

    companion object {

        val default = OFF
        val values = listOf(ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.AmoledDarkTheme.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }
}

operator fun AmoledDarkThemePreference.not(): AmoledDarkThemePreference =
    when (value) {
        true -> AmoledDarkThemePreference.OFF
        false -> AmoledDarkThemePreference.ON
    }
