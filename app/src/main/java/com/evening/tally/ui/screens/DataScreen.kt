package com.evening.tally.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.R
import com.evening.tally.ext.showToast
import com.evening.tally.ext.string
import com.evening.tally.ui.common.LocalRootNavController
import com.evening.tally.ui.common.Route
import com.evening.tally.ui.component.RYScaffold
import com.evening.tally.ui.pages.component.data.AccountingTable
import com.evening.tally.ui.pages.component.data.AddEditDialog
import com.evening.tally.ui.pages.component.data.FilterDropdown
import com.evening.tally.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }
    val rootNavController = LocalRootNavController.current
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(
                        onClick = { rootNavController.navigate(Route.SEARCH) }
                    ) {
                        Icon(Icons.Default.Search, "搜索")
                    }

                    // 原有筛选按钮
                    Box {
                        var showSortMenu by remember { mutableStateOf(false)}

                        IconButton(
                            onClick = { showSortMenu = true }
                        ) {
                            Icon(Icons.Default.FilterList, "筛选")
                        }

                        FilterDropdown(
                            expanded = showSortMenu,
                            onDismiss = { showSortMenu = false },
                            currentSortType = state.selectedSortType,
                            onSortSelected = { viewModel.applySort(it) }
                        )
                    }
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
                                showToast("删除成功")
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


        Crossfade(targetState = uiState.items, label = "动画") { targetItems ->
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                targetItems.isEmpty() -> EmptyState()
                else -> AccountingTable(
                    items = targetItems,
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