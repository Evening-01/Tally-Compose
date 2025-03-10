package com.evening.tally.ui.pages.fragment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.evening.tally.R
import com.evening.tally.ui.component.DisplayText
import com.evening.tally.ui.component.FeedbackIconButton
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.component.Subtitle
import com.evening.tally.ui.pages.component.setting.SettingItem
import com.evening.tally.ui.theme.palette.onLight
import com.evening.tally.viewmodel.AccountingViewModel

@Composable
fun DataLocalManagerPage(
    navController: NavHostController,
    viewModel: AccountingViewModel
) {
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportData(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importData(context, it) }
    }


    RYScaffold(containerColor = MaterialTheme.colorScheme.surface onLight MaterialTheme.colorScheme.inverseOnSurface, navigationIcon = {
        FeedbackIconButton(
            imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onSurface
        ) {
            navController.popBackStack()
        }
    }, content = {
        LazyColumn {
            item {
                DisplayText(text = stringResource(R.string.local_data_manager), desc = "")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.local_data_manager_subtitle),
                )

                SettingItem(
                    title = stringResource(R.string.data_restore),
                    desc = stringResource(R.string.data_restore_desc),
                    onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    },
                ) {}
                SettingItem(
                    title = stringResource(R.string.json_export),
                    desc = stringResource(R.string.json_export_summary),
                    onClick = {
                        exportLauncher.launch("counter_backup.json")
                    },
                ) {}
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    })
}
