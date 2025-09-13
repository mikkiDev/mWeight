package com.example.mweight

import android.graphics.Color
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

@Composable
fun AddWeightDialog(onDismissRequest: () -> Unit, onConfirmation: (Float, String) -> Unit) {

    var numberText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val todayDate = remember {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }
    var selectedDate by remember { mutableStateOf(todayDate) }


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Weight entry",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(top = 30.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.padding(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                WeightInput(
                    value = numberText,
                    onValueChange = {
                        numberText = it
                        isError = it.isNotEmpty() && !isValidFloat(it)
                    },
                    label = "Weight (kg):",
                    isError = isError,
                    errorMessage = "Enter a valid value.",
                    modifier = Modifier
                        .padding(10.dp)
                )

                DatePickerDocked(
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date }
                )

            }



            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Dismiss")
                }

                TextButton(
                    onClick = {
                        val weight = numberText.toFloatOrNull()
                        if (weight != null) {
                            onConfirmation(weight, selectedDate)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Add entry")
                }
            }

        }
    }

}


@Composable
fun WeightInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Number",
    isError: Boolean = false,
    errorMessage: String = "Invalid number format",
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || isValidFloat(newValue)) {
                    onValueChange(newValue)
                }
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = isError,
            singleLine = true,
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


private fun isValidFloat(input: String): Boolean {
    if (input == ".") return false // Single dot not allowed
    if (input.count { it == '.' } > 1) return false // Multiple dots not allowed

    val pattern = Regex("^\\d*\\.?\\d*$")
    return pattern.matches(input)
}

// Helper function to convert to actual float
fun String.toFloatOrNull(): Float? {
    return if (isNotEmpty() && isValidFloat(this)) {
        try {
            toFloat()
        } catch (e: NumberFormatException) {
            null
        }
    } else {
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            val newDate = convertMillisToDate(it)
            onDateSelected(newDate)

            if (datePickerState.selectedDateMillis != null) {
                showDatePicker = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Date of record") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable(onClick = {showDatePicker = true}),

            )

            if (showDatePicker) {
                Popup(
                    onDismissRequest = { showDatePicker = false },
                    alignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            //.offset(y = 64.dp)
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
    }
}


private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}
