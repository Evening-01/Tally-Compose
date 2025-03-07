package com.evening.tally.ui.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.evening.tally.R
import com.evening.tally.ui.pages.common.Route

sealed class Screen(val route: String, @StringRes val stringId: Int, val imageId: ImageVector) {
    object Data : Screen(Route.DATA, R.string.nav_data, Icons.Filled.List)
    object Setting : Screen(Route.SETTING, R.string.nav_setting, Icons.Filled.Settings)
}

val bottomNavScreenList = listOf(
    Screen.Data,
    Screen.Setting,
)