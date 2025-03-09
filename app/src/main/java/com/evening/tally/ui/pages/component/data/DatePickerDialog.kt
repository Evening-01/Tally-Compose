package com.evening.tally.ui.pages.component.data

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    viewModel: AccountingViewModel,
    onDismiss: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.selectedDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    dateState.selectedDateMillis?.let {
                        viewModel.onDateSelected(it)
                    }
                    onDismiss()
                }
            ) { Text("确认") }
        }
    ) {
        DatePicker(
            state = dateState,
            showModeToggle = true
        )
    }
}
