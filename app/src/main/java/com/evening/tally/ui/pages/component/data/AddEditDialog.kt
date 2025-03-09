package com.evening.tally.ui.pages.component.data


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.data.entity.AccountingItem
import com.evening.tally.viewmodel.AccountingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditDialog(
    viewModel: AccountingViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var diameter by remember { mutableStateOf("") }
    var thickness by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var orderNumber by remember { mutableStateOf("") }
    var bundleWeight by remember { mutableStateOf("") }
    var totalBundles by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // 从ViewModel状态获取日期
    val dateText = remember(uiState.selectedDate) {
        dateFormat.format(Date(uiState.selectedDate))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加新记录") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 显示错误信息
                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // 日期选择按钮
                OutlinedButton(
                    onClick = { viewModel.toggleDatePicker(true) },
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "选择日期",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "日期: $dateText",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp) // 微调文字垂直对齐
                        )
                    }
                }



                OutlinedTextField(
                    value = orderNumber,
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = { orderNumber = it },
                    label = { Text("订单号*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = diameter,
                        shape = MaterialTheme.shapes.medium,
                        onValueChange = { diameter = it.filter { it.isDigit() || it == '.' } },
                        label = { Text("直径(mm)*") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    OutlinedTextField(
                        value = thickness,
                        shape = MaterialTheme.shapes.medium,
                        onValueChange = { thickness = it.filter { it.isDigit() || it == '.' } },
                        label = { Text("壁厚(mm)*") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }

                OutlinedTextField(
                    value = length,
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = { length = it.filter(Char::isDigit) },
                    label = { Text("长度(mm)*") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = bundleWeight,
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = { bundleWeight = it.filter { it.isDigit() || it == '.' } },
                    label = { Text("单捆重量(kg)*") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = totalBundles,
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = { totalBundles = it.filter(Char::isDigit) },
                    label = { Text("总捆数*") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = unitPrice,
                    shape = MaterialTheme.shapes.medium,
                    onValueChange = { unitPrice = it.filter { it.isDigit() || it == '.' } },
                    label = { Text("开票价(元/kg)*") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val newItem = AccountingItem(
                            date = Date(uiState.selectedDate), // 使用状态中的日期
                            orderNumber = orderNumber,
                            diameter = diameter.toDouble(),
                            thickness = thickness.toDouble(),
                            length = length.toInt(),
                            bundleWeight = bundleWeight.toDouble(),
                            totalBundles = totalBundles.toInt(),
                            unitPrice = unitPrice.toDouble()
                        )
                        viewModel.saveItem(newItem)
                        onDismiss()
                    } catch (e: NumberFormatException) {
                        viewModel.setErrorMessage("请输入有效的数值")
                    }
                },
                enabled = orderNumber.isNotEmpty() && diameter.isNotEmpty()
                        && thickness.isNotEmpty() && length.isNotEmpty()
                        && bundleWeight.isNotEmpty() && totalBundles.isNotEmpty()
                        && unitPrice.isNotEmpty()
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )

    // 日期选择器对话框

    if (uiState.showDatePicker) {
        CustomDatePicker(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleDatePicker(false) }
        )
    }
}
