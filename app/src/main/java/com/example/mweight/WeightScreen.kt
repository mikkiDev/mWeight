package com.example.mweight

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen(@StringRes title: Int) {
    Home(title = R.string.app_name),
    Weight(title = R.string.weight_screen_name)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(weightViewModel: WeightViewModel = viewModel()) {

    val weightEntries by weightViewModel.allEntries.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "mWeight") }
            )
        },
        floatingActionButton = {
            AddFab { showDialog = true }
                               },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WeightChart(
                        entries = weightEntries,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .weight(1f)
                    ) {
                        if (weightEntries.isNotEmpty()) {
                            items(weightEntries, key = { it.id }) { entry ->
                                SwipeToDismissWeightEntry(
                                    entry = entry,
                                    onDismiss = {
                                        weightViewModel.deleteEntry(entry.id)
                                    }
                                )
                            }
                        }
                    }

                    if (showDialog) {
                        AddWeightDialog(
                            onDismissRequest = { showDialog = false },
                            onConfirmation = { weight, date ->
                                weightViewModel.addEntry(value = weight, date = date)
                            }
                        )
                    }
                }
            }
        }
    )


}


@Composable
private fun WeightEntryItem(entry: WeightEntryData) {

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${entry.weight}", modifier = Modifier.weight(1f))
            Text(entry.date)
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissWeightEntry(
    entry: WeightEntryData,
    onDismiss: () -> Unit
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        WeightEntryItem(entry = entry)
    }

}



@Composable
private fun AddFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Add, "Extended Add weight FAB") },
        text = { Text(text = "Add") },
    )
}