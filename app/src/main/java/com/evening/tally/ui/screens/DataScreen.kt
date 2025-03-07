package com.evening.tally.ui.screens

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.evening.tally.R
import com.evening.tally.ext.string
import com.evening.tally.ui.component.RYScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen() {
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
                    Toast.makeText(
                        context,
                        "Add new memo",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = R.string.add_new_one.string
                )
            }
        },
        content = {
            Text(text = "DataScreen")
        })
}