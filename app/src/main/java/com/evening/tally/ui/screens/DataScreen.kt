package com.evening.tally.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.R
import com.evening.tally.ext.showToast
import com.evening.tally.ext.string
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.pages.component.data.AccountingTable
import com.evening.tally.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(viewModel: AccountingViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    RYScaffold(
        title = stringResource(id = R.string.nav_data),
        actions = {
            IconButton(
                onClick = {
                    Toast.makeText(
                        context,
                        "Add new memo",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                Icon(Icons.Filled.Search, contentDescription = R.string.search.string)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                        viewModel.toggleAddDialog(true)

                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = R.string.add_new_one.string
                )
            }
        },
        content = {
            DataLazyColumn(viewModel)
        })
}

@Composable
fun DataLazyColumn(
    viewModel: AccountingViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


        when {
            uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            uiState.items.isEmpty() -> EmptyState()
            else -> AccountingTable(
                items = uiState.items,
                selectedIds = uiState.selectedIds,
                onItemClick = { item ->
                    if (uiState.selectedIds.isEmpty()) {
                        // 进入编辑逻辑
                    } else {
                        viewModel.toggleSelection(item.id)
                    }
                },
                onLongClick = { item ->
                    viewModel.toggleSelection(item.id)
                }
            )
        }


        if (uiState.showAddDialog) {
//            AddEditDialog(
//                viewModel = viewModel,
//                onDismiss = { viewModel.toggleAddDialog(false) }
//            )
            showToast("dsakldsa")
        }
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = stringResource(id = R.string.empty_state_message),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}