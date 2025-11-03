package com.gearsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gearsnap.R
import com.gearsnap.ui.components.AddActivityDialog
import com.gearsnap.ui.components.MonthlyCalendar
import java.time.format.DateTimeFormatter
import android.os.Build

@Composable
fun PlanningScreen(vm: PlanningViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MonthlyCalendar(
            month = vm.currentMonth,
            selectedDate = vm.selectedDate,
            onPrevMonth = vm::prevMonth,
            onNextMonth = vm::nextMonth,
            onSelectDate = vm::onDateSelected
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = vm.selectedDate.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            FilledTonalButton(onClick = { showDialog = true }, shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.planning_add_activity))
            }
        }

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            val items = vm.activitiesForSelected()
            if (items.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.planning_empty_day), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(items, key = { it.id }) { it ->
                    ListItem(
                        headlineContent = { Text(it.title) },
                        supportingContent = { Text(it.time) },
                        leadingContent = { AssistChip(onClick = {}, label = { Text(it.time) }) }
                    )
                    Divider()
                }
            }
        }
    }

    if (showDialog) {
        AddActivityDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, time, recurrence, occurrences ->
                if (recurrence == com.gearsnap.model.Recurrence.NONE) {
                    vm.addActivity(title, time)
                } else {
                    vm.addRecurringActivity(title, time, vm.selectedDate, recurrence, occurrences)
                }
                showDialog = false
            }
        )
    }
}
