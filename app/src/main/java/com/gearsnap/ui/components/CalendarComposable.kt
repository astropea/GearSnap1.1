@file:Suppress("NewApi")

package com.gearsnap.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun MonthlyCalendar(
    month: YearMonth,
    selectedDate: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevMonth) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } + " " + month.year,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onNextMonth) { Icon(Icons.Default.ArrowForward, contentDescription = null) }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday labels
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.values().forEach { dow ->
                Text(
                    text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dates grid 6x7
        val firstOfMonth = month.atDay(1)
        val firstDayIndex = (firstOfMonth.dayOfWeek.value - 1).coerceIn(0, 6) // 0..6 -> Monday..Sunday
        val daysInMonth = month.lengthOfMonth()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            var dayCounter = 1
            repeat(6) { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(7) { col ->
                        val cellNumber = row * 7 + col
                        val inMonth = cellNumber >= firstDayIndex && dayCounter <= daysInMonth
                        val date = if (inMonth) month.atDay(dayCounter) else null
                        if (inMonth) dayCounter++

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        date == selectedDate -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .clickable(enabled = inMonth && date != null) { onSelectDate(date!!) }
                                .padding(6.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (inMonth && date != null) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (date == selectedDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
