package com.evening.tally.ui.pages.other

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.evening.tally.R
import com.evening.tally.ext.DataStoreKeys
import com.evening.tally.ext.dataStore
import com.evening.tally.ext.put
import com.evening.tally.ext.string
import com.evening.tally.ui.component.DisplayText
import com.evening.tally.ui.component.DynamicSVGImage
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.pages.common.Route
import com.evening.tally.ui.svg.SVGString
import com.evening.tally.ui.svg.WELCOME
import com.ireward.htmlcompose.HtmlText
import kotlinx.coroutines.launch

@Composable
fun StartPage(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tosVisible by remember { mutableStateOf(false) }

    RYScaffold(
        content = {
            LazyColumn(
                modifier = Modifier.navigationBarsPadding(),
            ) {
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                    DisplayText(text = stringResource(R.string.welcome), desc = "")
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    DynamicSVGImage(
                        modifier = Modifier.padding(horizontal = 60.dp),
                        svgImageString = SVGString.WELCOME,
                        contentDescription = stringResource(R.string.color_and_style),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = R.string.tos_tips.string,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    TextButton(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = { Toast.makeText(context, "测试", Toast.LENGTH_SHORT).show() }
                    ) {
                        HtmlText(
                            text = stringResource(R.string.browse_tos_tips),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        },
        bottomBar = null,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = {
                    navController.navigate(Route.DATA) {
                        launchSingleTop = true
                    }
                    scope.launch {
                        context.dataStore.put(DataStoreKeys.IsFirstLaunch, false)
                    }
                },
                icon = {
                    Icon(
                        Icons.Rounded.CheckCircleOutline,
                        stringResource(R.string.agree)
                    )
                },
                text = { Text(text = stringResource(R.string.agree)) },
            )
        }
    )
}