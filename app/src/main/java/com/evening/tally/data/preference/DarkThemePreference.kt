package com.evening.tally.data.preference

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.datastore.preferences.core.Preferences
import com.evening.tally.R
import com.evening.tally.ext.DataStoreKeys
import com.evening.tally.ext.dataStore
import com.evening.tally.ext.put

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class DarkThemePreference(val value: Int) : Preference() {
    object UseDeviceTheme : DarkThemePreference(0)
    object ON : DarkThemePreference(1)
    object OFF : DarkThemePreference(2)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.DarkTheme,
                value
            )
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            UseDeviceTheme -> context.getString(R.string.use_device_theme)
            ON -> context.getString(R.string.on)
            OFF -> context.getString(R.string.off)
        }

    @Composable
    @ReadOnlyComposable
    fun isDarkTheme(): Boolean = when (this) {
        UseDeviceTheme -> isSystemInDarkTheme()
        ON -> true
        OFF -> false
    }

    companion object {

        val default = UseDeviceTheme
        val values = listOf(UseDeviceTheme, ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.DarkTheme.key]) {
                0 -> UseDeviceTheme
                1 -> ON
                2 -> OFF
                else -> default
            }
    }
}

@Composable
operator fun DarkThemePreference.not(): DarkThemePreference =
    when (this) {
        DarkThemePreference.UseDeviceTheme -> if (isSystemInDarkTheme()) {
            DarkThemePreference.OFF
        } else {
            DarkThemePreference.ON
        }

        DarkThemePreference.ON -> DarkThemePreference.OFF
        DarkThemePreference.OFF -> DarkThemePreference.ON
    }
