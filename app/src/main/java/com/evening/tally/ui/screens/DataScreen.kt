package com.evening.tally.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.R
import com.evening.tally.ext.string
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.pages.component.data.AccountingTable
import com.evening.tally.ui.pages.component.data.AddEditDialog
import com.evening.tally.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val title = when {
        state.selectedIds.isNotEmpty() ->
            "已选择 ${state.selectedIds.size} 项"

        else -> stringResource(id = R.string.nav_data)
    }

    RYScaffold(
        title = title,

        actions = {

                if (state.selectedIds.isNotEmpty()) {
                    IconButton(
                        onClick = { showConfirmDialog = true }
                    ) {
                        Icon(Icons.Default.Delete, "删除")
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.toggleFilterSheet(true) }
                    ) {
                        Icon(Icons.Default.ArrowDropDown, "筛选")
                    }
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

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("确认删除") },
                    text = { Text("确定要删除选中的 ${state.selectedIds.size} 项记录吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteSelected()
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                showConfirmDialog = false
                            }
                        ) {
                            Text("确认", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showConfirmDialog = false }
                        ) {
                            Text("取消", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    textContentColor = MaterialTheme.colorScheme.onSurface
                )
            }
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
            AddEditDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.toggleAddDialog(false) }
            )
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