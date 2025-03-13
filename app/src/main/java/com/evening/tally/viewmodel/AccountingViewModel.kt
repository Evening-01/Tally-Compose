package com.evening.tally.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evening.tally.data.entity.AccountingItem
import com.evening.tally.repository.AccountingRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val repository: AccountingRepository
) : ViewModel() {

    // UI 状态统一管理
    data class UiState(
        val items: List<UiModel> = emptyList(),
        val isLoading: Boolean = true,
        val selectedIds: Set<Long> = emptySet(),
        val filter: FilterState = FilterState(),
        val errorMessage: String? = null,
        val showAddDialog: Boolean = false,
        val showDatePicker: Boolean = false,
        val showFilterSheet: Boolean = false,
        val selectedDate: Long = System.currentTimeMillis()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // 业务模型定义
    data class UiModel(
        val id: Long,
        val date: String,
        val orderNumber: String,
        val diameter: String,
        val thickness: String,
        val length: String,
        val piecesPerBundle: String,
        val bundleWeight: String,
        val totalBundles: String,
        val totalPieces: String,
        val totalWeight: String,
        val unitPrice: String,
        val totalAmount: String
    )

    data class FilterState(
        val startDate: Date = Date(System.currentTimeMillis() - 7 * 24 * 3600 * 1000L),
        val endDate: Date = Date(),
        val sortType: SortType = SortType.DATE_DESC, // 默认按时间倒序
        val orderNumberQuery: String = ""
    )

    enum class SortType { DATE_ASC, DATE_DESC, AMOUNT_ASC, AMOUNT_DESC }

    init {
        loadItems()
    }

    /* 状态操作方法示例 */
    // 加载数据
    private fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllItems()
                .map { items -> items.map { it.toUiModel() } }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            errorMessage = "加载失败: ${e.localizedMessage}",
                            isLoading = false
                        )
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(
                            items = applySortToItems(items),
                            isLoading = false
                        )
                    }
                }
        }
    }

    // 切换选中状态
    fun toggleSelection(itemId: Long) {
        _uiState.update { state ->
            val newSelection = if (state.selectedIds.contains(itemId)) {
                state.selectedIds - itemId
            } else {
                state.selectedIds + itemId
            }
            state.copy(selectedIds = newSelection)
        }
    }

    fun toggleDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showFilterSheet = show)
    }

    fun setErrorMessage(message: String?) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun saveItem(item: AccountingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addItem(item)
                _uiState.update {
                    it.copy(
                        showAddDialog = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "保存失败: ${e.localizedMessage}"
                    )
                }
            }
        }
    }


    // 删除选中项
    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteByIds(_uiState.value.selectedIds.toList())
            _uiState.update { it.copy(selectedIds = emptySet()) }
            loadItems()
        }
    }

    fun applySort(sortType: SortType) {
        // 异步执行排序操作
        viewModelScope.launch(Dispatchers.Default) {
            val sortedItems = when (sortType) {
                SortType.DATE_ASC -> _uiState.value.items.sortedBy { it.date }
                SortType.DATE_DESC -> _uiState.value.items.sortedByDescending { it.date }
                SortType.AMOUNT_ASC -> _uiState.value.items.sortedBy { it.totalAmount }
                SortType.AMOUNT_DESC -> _uiState.value.items.sortedByDescending { it.totalAmount }
            }
            // 切换到主线程更新 UI
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(items = sortedItems)
            }
        }
    }

    private fun applySortToItems(items: List<UiModel>): List<UiModel> {
        return when (_uiState.value.filter.sortType) {
            SortType.DATE_ASC -> items.sortedBy { parseDate(it.date) }
            SortType.DATE_DESC -> items.sortedByDescending { parseDate(it.date) }
            SortType.AMOUNT_ASC -> items.sortedBy {
                // 处理金额字符串，去掉 "¥" 符号，并转为 Double
                it.totalAmount.replace("¥", "").toDoubleOrNull() ?: 0.0
            }
            SortType.AMOUNT_DESC -> items.sortedByDescending {
                // 处理金额字符串，去掉 "¥" 符号，并转为 Double
                it.totalAmount.replace("¥", "").toDoubleOrNull() ?: 0.0
            }
        }
    }

    // 显示/隐藏对话框
    fun toggleAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    // 日期选择
    fun onDateSelected(millis: Long) {
        _uiState.update {
            it.copy(
                selectedDate = millis,
                showDatePicker = false
            )
        }
    }

    /* 辅助方法 */
    private fun processFilter(items: List<UiModel>): List<UiModel> {
        return items
            .filter { item ->
                val date = parseDate(item.date)
                date.time >= _uiState.value.filter.startDate.time &&
                        date.time <= _uiState.value.filter.endDate.time &&
                        item.orderNumber.contains(_uiState.value.filter.orderNumberQuery, true)
            }
            .sortedWith(
                when (_uiState.value.filter.sortType) {
                    SortType.DATE_ASC -> compareBy { parseDate(it.date) }
                    SortType.DATE_DESC -> compareByDescending { parseDate(it.date) }
                    SortType.AMOUNT_ASC -> compareBy { it.totalAmount.toDouble() }
                    SortType.AMOUNT_DESC -> compareByDescending { it.totalAmount.toDouble() }
                }
            )
    }

    private fun parseDate(dateStr: String): Date {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Date()
    }

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                repository.exportData(context, uri)
                showToast(context, "导出成功")
            } catch (e: Exception) {
                showToast(context, "导出失败: ${e.message}")
            }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                repository.importData(context, uri)
                showToast(context, "导入成功")
            } catch (e: Exception) {
                showToast(context, "导入失败: ${e.message}")
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    // 数据库实体转换
    private fun AccountingItem.toUiModel(): UiModel {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val piecesPerBundle = calculatePiecesPerBundle(diameter)

        return UiModel(
            id = this.id,
            date = dateFormat.format(date),
            orderNumber = orderNumber,
            diameter = "Ø ${diameter}mm",
            thickness = "${thickness}mm",
            length = "${length}mm",
            piecesPerBundle = "$piecesPerBundle 支/捆",
            bundleWeight = "%.1fkg".format(bundleWeight),
            totalBundles = "${totalBundles}捆",
            totalPieces = "${totalBundles * piecesPerBundle}支",
            totalWeight = "%.1fkg".format(bundleWeight * totalBundles),
            unitPrice = "¥%.2f".format(unitPrice),
            totalAmount = "¥%.2f".format(bundleWeight * totalBundles * unitPrice)
        )
    }

    private fun calculatePiecesPerBundle(diameter: Double) = when {
        diameter <= 50 -> 10
        diameter <= 80 -> 5
        else -> 2
    }
}