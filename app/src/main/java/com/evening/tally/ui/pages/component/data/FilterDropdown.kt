package com.evening.tally.ui.pages.component.data

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evening.tally.viewmodel.AccountingViewModel

@Composable
fun FilterDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    currentSortType: AccountingViewModel.SortType,
    onSortSelected: (AccountingViewModel.SortType) -> Unit
) {
    val sortOptions = listOf(
        "按时间升序" to AccountingViewModel.SortType.DATE_ASC,
        "按时间降序" to AccountingViewModel.SortType.DATE_DESC,
        "按金额升序" to AccountingViewModel.SortType.AMOUNT_ASC,
        "按金额降序" to AccountingViewModel.SortType.AMOUNT_DESC
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        sortOptions.forEach { (label, sortType) ->



            androidx.compose.material3.DropdownMenuItem(
                text = { Text(label) },
                trailingIcon = {
                    if (currentSortType == sortType) { // 添加判断条件
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "已选中",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                onClick = {
                    onSortSelected(sortType)
                    onDismiss()
                }
            )
        }
    }
}