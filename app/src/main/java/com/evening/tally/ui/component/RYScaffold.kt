package com.evening.tally.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evening.tally.ui.theme.palette.onDark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RYScaffold(
    title: String = "",
    containerColor: Color = MaterialTheme.colorScheme.surface,
    topBarTonalElevation: Dp = 0.dp,
    containerTonalElevation: Dp = 0.dp,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.surfaceColorAtElevation(topBarTonalElevation)
        ),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(containerTonalElevation) onDark MaterialTheme.colorScheme.surface,
        topBar = {
            if (navigationIcon != null || actions != null) {
                TopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = { navigationIcon?.invoke() },
                    actions = { actions?.invoke(this) },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent,
                    )
                )
            }
        },
        content = {
            Column {
                Spacer(modifier = Modifier.height(it.calculateTopPadding()))
                content()
            }
        },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = { floatingActionButton?.invoke() },
    )
}

@Composable
infix fun Color.onDark(darkColor: Color): Color = this