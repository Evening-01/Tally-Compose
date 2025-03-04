package com.evening.tally.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RYSwitch(
    modifier: Modifier = Modifier,
    activated: Boolean,
    enable: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Switch(
        modifier = modifier.clickable { onClick?.invoke() },
        checked = activated,
        enabled = enable,
        onCheckedChange = { onClick?.invoke() },
    )
}
