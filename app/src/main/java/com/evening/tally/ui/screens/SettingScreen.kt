package com.evening.tally.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.evening.tally.R
import com.evening.tally.ext.showToast
import com.evening.tally.ext.string
import com.evening.tally.ui.common.Route
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.pages.component.setting.SelectableSettingGroupItem

@Composable
fun SettingScreen(
    navController: NavHostController
) {
    RYScaffold(
        title = stringResource(id = R.string.nav_setting),
        actions = {
            IconButton(
                onClick = {
                    showToast("todo")
                }) {
                Icon(Icons.Filled.Search, contentDescription = R.string.search.string)
            }
        },
        content = {
            SettingsPreferenceScreen(navController)
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPreferenceScreen(navController: NavHostController) {

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.color_and_style),
                    desc = stringResource(R.string.color_and_style_desc),
                    icon = Icons.Outlined.Palette,
                ) {
                    navController.navigate(Route.COLOR_AND_STYLE) {
                        launchSingleTop = true
                    }
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.local_data_manager),
                    desc = stringResource(R.string.local_data_manager_desc),
                    icon = Icons.Outlined.DataUsage,
                ) {
                    navController.navigate(Route.DATA_LOCAL_MANAGER) {
                        launchSingleTop = true
                    }
                }
            }

//            item {
//                SelectableSettingGroupItem(
//                    title = stringResource(R.string.cloud_data_manager),
//                    desc = stringResource(R.string.cloud_data_manager_desc),
//                    icon = Icons.Outlined.Cloud,
//                ) {
//                    navController.navigate(RouteName.DATA_CLOUD_MANAGER) {
//                        launchSingleTop = true
//                    }
//                }
//            }
//
//            item {
//                SelectableSettingGroupItem(
//                    title = stringResource(R.string.fix_tag),
//                    desc = stringResource(R.string.fix_tag_desc),
//                    icon = Icons.Outlined.Label,
//                ) {
////                    navController.navigate(RouteName.COLOR_AND_STYLE) {
////                        launchSingleTop = true
////                    }
//                    showToast("todo")
//                }
//            }
//
//            item {
//                SelectableSettingGroupItem(
//                    title = stringResource(R.string.about),
//                    desc = stringResource(R.string.tips_and_support_desc),
//                    icon = Icons.Outlined.TipsAndUpdates,
//                ) {
//                    navController.navigate(RouteName.TIPS_AND_SUPPORT) {
//                        launchSingleTop = true
//                    }
//                }
//            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }

        }
    }
}