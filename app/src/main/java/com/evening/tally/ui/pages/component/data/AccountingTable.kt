package com.evening.tally.ui.pages.component.data

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evening.tally.R
import com.evening.tally.viewmodel.AccountingViewModel


@Composable
fun AccountingTable(
    items: List<AccountingViewModel.UiModel>,
    modifier: Modifier = Modifier,
    selectedIds: Set<Long>,
    onItemClick: (AccountingViewModel.UiModel) -> Unit,
    onLongClick: (AccountingViewModel.UiModel) -> Unit
) {
    val horizontalScroll = rememberScrollState()
    val showCheckboxes = selectedIds.isNotEmpty()

    Column(modifier = modifier.fillMaxSize()) {
        // 表头根据 Checkbox 显示状态添加占位
        HeaderRow(
            scrollState = horizontalScroll,
            showCheckboxes = showCheckboxes
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScroll)
        ) {
            items(items) { item ->
                DataRow(
                    item = item,
                    selected = selectedIds.contains(item.id),
                    showCheckbox = showCheckboxes, // 传递显示状态
                    onItemClick = { onItemClick(item) },
                    onLongPress = { onLongClick(item) }
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.8.dp
                )
            }
        }
    }
}
@Composable
private fun HeaderRow(
    scrollState: ScrollState,
    showCheckboxes: Boolean // 新增显示状态参数
) {
    val headers = listOf(
        R.string.header_date,
        R.string.header_order,
        R.string.header_1,
        R.string.header_2,
        R.string.header_3,
        R.string.header_4,
        R.string.header_5,
        R.string.header_6,
        R.string.header_7,
        R.string.header_8,
        R.string.header_9,
        R.string.header_10
    )

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showCheckboxes) {
            HeaderCell(
                text = "",
                modifier = Modifier.width(40.dp) // 与 DataRow 中 Checkbox 宽度一致
            )
        }
        headers.forEach { resId ->
            HeaderCell(text = stringResource(id = resId))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DataRow(
    item: AccountingViewModel.UiModel,
    selected: Boolean,
    showCheckbox: Boolean, // 新增显示状态参数
    onItemClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onItemClick,
                onLongClick = onLongPress
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选中状态指示器

        if (showCheckbox) {
            Checkbox(
                checked = selected,
                onCheckedChange = null,
                modifier = Modifier
                    .width(40.dp) // 固定宽度确保与表头对齐
                    .size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }


        // 数据单元格
        listOf(
            item.date,
            item.orderNumber,
            item.diameter,
            item.thickness,
            item.length,
            item.piecesPerBundle,
            item.bundleWeight,
            item.totalBundles,
            item.totalPieces,
            item.totalWeight,
            item.unitPrice,
            item.totalAmount
        ).forEach { value ->
            DataCell(value = value)
        }
    }
}

@Composable
private fun HeaderCell(
    text: String,
    modifier: Modifier = Modifier // 允许自定义宽度
) {
    Text(
        text = text,
        modifier = modifier.width(120.dp)
            .padding(horizontal = 8.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun DataCell(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .width(120.dp)
            .padding(horizontal = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}