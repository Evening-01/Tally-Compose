package com.evening.tally.ui.pages.fragment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evening.tally.R
import com.evening.tally.ext.string
import com.evening.tally.ui.common.LocalRootNavController
import com.evening.tally.ui.pages.component.data.AccountingTable
import com.evening.tally.viewmodel.AccountingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(viewModel: AccountingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var isWholeWordSearch by remember { mutableStateOf(false) }
    val rootNavController = LocalRootNavController.current
    val focusRequester = remember { FocusRequester() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    // 搜索防抖处理
    LaunchedEffect(searchText) {
        delay(500)
        viewModel.searchItems(searchText.text, isWholeWordSearch)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            value = searchText,
                            onValueChange = { searchText = it },
                            singleLine = true,
                            placeholder = { Text(R.string.search.string) },
                            shape = ShapeDefaults.ExtraLarge,
                            trailingIcon = {
                                TextButton(
                                    shape = RoundedCornerShape(28.0.dp),
                                    onClick = {
                                        isWholeWordSearch = !isWholeWordSearch
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = if (isWholeWordSearch) Color.Green else Color(
                                            ContextCompat.getColor(context, R.color.fontColor)
                                        )
                                    ),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Text(
                                        text = R.string.whole_word_search.string,
                                        fontSize = 18.sp
                                    )
                                }
                            },
                            leadingIcon = {
                                IconButton(onClick = { rootNavController.popBackStack() }) {
                                    Icon(
                                        Icons.Outlined.ArrowBack,
                                        contentDescription = R.string.back.string
                                    )
                                }
                            }
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Crossfade(targetState = uiState.searchResults) { results ->
                    when {
                        uiState.isSearchLoading ->
                            CircularProgressIndicator(Modifier.align(Alignment.Center))

                        results.isEmpty() ->
                            EmptySearchState(searchText.text)

                        else ->
                            AccountingTable(
                                items = results,
                                selectedIds = emptySet(),
                                onItemClick = { /* ... */ },
                                onLongClick = { /* ... */ }
                            )
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}

@Composable
private fun EmptySearchState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (searchQuery.isNotEmpty()) {
                "没有找到'$searchQuery'的相关结果"
            } else {
                R.string.empty_state_message.string
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}