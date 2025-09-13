package com.example.mweight

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.math.RoundingMode

enum class Screen(@StringRes title: Int) {
    Home(title = R.string.app_name),
    Weight(title = R.string.weight_screen_name)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(weightViewModel: WeightViewModel = viewModel()) {

    val weightEntries by weightViewModel.allEntries.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val lazyColumnScrollableState = rememberLazyListState()
    val extended by remember {
        derivedStateOf {
            lazyColumnScrollableState.firstVisibleItemIndex == 0 &&
                    lazyColumnScrollableState.firstVisibleItemScrollOffset == 0
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "mWeight") },
                    scrollBehavior = topBarScrollBehavior
                )
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.height(1.dp).width(50.dp)
                )
            }
        },
        floatingActionButton = {
            AddFab(
                onClick = { showDialog = true },
                extended = extended
            )
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

                    LazyColumn(
                        state = lazyColumnScrollableState,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            //.weight(1f)
                    ) {

                        item {
                            WeightChart(entries = weightEntries)
                        }

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
                                var roundedWeight = weight.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                                if (roundedWeight > 200f) roundedWeight = 200f
                                weightViewModel.addEntry(value = roundedWeight, date = date)
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
            Text(
                text = "${entry.weight}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
            )
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
private fun AddFab(
    onClick: () -> Unit,
    extended : Boolean = true
    ) {

    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            AnimatedVisibility(extended) {
                Text (
                    text = "Add",
                    modifier = Modifier.padding(8.dp, 3.dp)
                )
            }
        }
    }
}