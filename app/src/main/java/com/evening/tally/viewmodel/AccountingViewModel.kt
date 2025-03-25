package com.evening.tally.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evening.tally.data.entity.AccountingItem
import com.evening.tally.ext.showToast
import com.evening.tally.repository.AccountingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val repository: AccountingRepository,
    @ApplicationContext private val context: Context // 注入 Context
) : ViewModel() {

    // UI 状态统一管理
    data class UiState(
        val items: List<UiModel> = emptyList(),
        val isLoading: Boolean = true,
        val selectedIds: Set<Long> = emptySet(),
        val errorMessage: String? = null,
        val showAddDialog: Boolean = false,
        val showDatePicker: Boolean = false,
        val showFilterSheet: Boolean = false,
        val selectedDate: Long = System.currentTimeMillis(),
        val selectedSortType: SortType = SortType.DATE_DESC,
        val searchResults: List<UiModel> = emptyList(),
        val isSearchLoading: Boolean = false,
        val searchError: String? = null
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

    enum class SortType { DATE_ASC, DATE_DESC, AMOUNT_ASC, AMOUNT_DESC }

    init {
        val initialSortType = getPersistedSortType(context)
        _uiState.update { it.copy(selectedSortType = initialSortType) }
        loadItems()
    }

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

    // 更新排序方式
    fun applySort(sortType: SortType) {
        persistSortType(sortType) // 持久化保存
        _uiState.update { it.copy(selectedSortType = sortType) } // 更新 UI 状态

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

    // 搜索方法
    fun searchItems(query: String, isWholeWord: Boolean) {
        _uiState.update { it.copy(isSearchLoading = true, searchError = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repository.searchItems(query, isWholeWord).map { it.toUiModel() }
                _uiState.update {
                    it.copy(
                        searchResults = items,
                        isSearchLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearchLoading = false,
                        searchError = "搜索失败: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // 根据 FilterState 中的 sortType 进行排序
    private fun applySortToItems(items: List<UiModel>): List<UiModel> {
        return when (_uiState.value.selectedSortType) {
            SortType.DATE_ASC -> items.sortedBy { parseDate(it.date) }
            SortType.DATE_DESC -> items.sortedByDescending { parseDate(it.date) }
            SortType.AMOUNT_ASC -> items.sortedBy {
                it.totalAmount.replace("¥", "").toDoubleOrNull() ?: 0.0
            }
            SortType.AMOUNT_DESC -> items.sortedByDescending {
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


    private fun parseDate(dateStr: String): Date {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Date()
    }

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                repository.exportData(context, uri)
                showToast("导出成功")
            } catch (e: Exception) {
                showToast("导出失败: ${e.message}")
            }
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                repository.importData(context, uri)
                showToast("导入成功")
            } catch (e: Exception) {
                showToast("导入失败: ${e.message}")
            }
        }
    }

    private fun getPersistedSortType(context: Context): SortType {
        val prefs = context.getSharedPreferences("sort_prefs", Context.MODE_PRIVATE)
        return try {
            SortType.valueOf(prefs.getString("sort_type", SortType.DATE_DESC.name)!!)
        } catch (e: Exception) {
            SortType.DATE_DESC
        }
    }

    private fun persistSortType(sortType: SortType) {
        context.getSharedPreferences("sort_prefs", Context.MODE_PRIVATE).edit {
            putString("sort_type", sortType.name)
        }
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