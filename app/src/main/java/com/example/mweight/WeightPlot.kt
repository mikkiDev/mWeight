package com.example.mweight

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeightChart(
    entries: List<WeightEntryData>,
    modifier: Modifier = Modifier
) {
    if (entries.size < 2) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (entries.isEmpty()) "Add weight entries to see the chart"
                        else "Add more entries to see the chart",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val sortedEntries = entries.sortedBy { parseDate(it.date) }
    val minWeight = sortedEntries.minOf { it.weight } - 2f
    val maxWeight = sortedEntries.maxOf { it.weight } + 2f
    val weightRange = maxWeight - minWeight
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val canvasWidth = size.width - 40f // Padding for labels
            val canvasHeight = size.height - 40f
            val paddingLeft = 40f
            val paddingBottom = 30f

            // Draw grid lines and labels
            drawChartGrid(
                minWeight = minWeight,
                maxWeight = maxWeight,
                weightRange = weightRange,
                entries = sortedEntries,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                paddingLeft = paddingLeft,
                paddingBottom = paddingBottom,
                textColor = secondaryColor
            )

            // Draw data points and lines
            drawWeightData(
                entries = sortedEntries,
                minWeight = minWeight,
                weightRange = weightRange,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                paddingLeft = paddingLeft,
                paddingBottom = paddingBottom,
                primaryColor = primaryColor
            )
        }
    }

}

private fun DrawScope.drawChartGrid(
    minWeight: Float,
    maxWeight: Float,
    weightRange: Float,
    entries: List<WeightEntryData>,
    canvasWidth: Float,
    canvasHeight: Float,
    paddingLeft: Float,
    paddingBottom: Float,
    textColor: Color
) {
    // Draw Y-axis (weight) labels and grid lines
    val weightSteps = 5
    for (i in 0..weightSteps) {
        val weightValue = minWeight + (weightRange * i / weightSteps)
        val yPos = canvasHeight - (canvasHeight * i / weightSteps) + paddingBottom

        // Draw grid line
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = Offset(paddingLeft, yPos),
            end = Offset(canvasWidth + paddingLeft, yPos),
            strokeWidth = 1f
        )

        // Draw weight label
        drawContext.canvas.nativeCanvas.drawText(
            "%.1f".format(weightValue),
            paddingLeft - 35f,
            yPos + 5f,
            Paint().apply {
                color = textColor.toArgb()
                textSize = 12.sp.toPx()
                textAlign = Paint.Align.RIGHT
            }
        )
    }

    // Draw X-axis (date) labels
    val dateStep = maxOf(1, entries.size / 5)
    for (i in entries.indices step dateStep) {
        val entry = entries[i]
        val xPos = paddingLeft + (canvasWidth * i / (entries.size - 1))

        // Draw date label
        drawContext.canvas.nativeCanvas.drawText(
            formatDateForChart(entry.date),
            xPos,
            canvasHeight + paddingBottom + 40f,
            Paint().apply {
                color = textColor.toArgb()
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.CENTER
            }
        )
    }

    // Draw axes
    drawLine(
        color = Color.Black,
        start = Offset(paddingLeft, paddingBottom),
        end = Offset(paddingLeft, canvasHeight + paddingBottom),
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Black,
        start = Offset(paddingLeft, canvasHeight + paddingBottom),
        end = Offset(canvasWidth + paddingLeft, canvasHeight + paddingBottom),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawWeightData(
    entries: List<WeightEntryData>,
    minWeight: Float,
    weightRange: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    paddingLeft: Float,
    paddingBottom: Float,
    primaryColor: Color
) {
    val points = entries.mapIndexed { index, entry ->
        val x = paddingLeft + (canvasWidth * index / (entries.size - 1))
        val y = canvasHeight + paddingBottom - (canvasHeight * (entry.weight - minWeight) / weightRange)
        Offset(x, y)
    }

    // Draw connecting lines
    for (i in 0 until points.size - 1) {
        drawLine(
            color = primaryColor,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 3f
        )
    }

    // Draw data points
    points.forEach { point ->
        drawCircle(
            color = primaryColor,
            radius = 6f,
            center = point
        )
        drawCircle(
            color = Color.White,
            radius = 3f,
            center = point
        )
    }
}

private fun parseDate(dateString: String): Long {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.parse(dateString)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun formatDateForChart(dateString: String): String {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(dateString)
        val chartFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        chartFormatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}