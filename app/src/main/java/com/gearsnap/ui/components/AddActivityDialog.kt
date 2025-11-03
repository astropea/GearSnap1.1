package com.gearsnap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gearsnap.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String, recurrence: com.gearsnap.model.Recurrence, occurrences: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf(com.gearsnap.model.Recurrence.NONE) }
    var occurrencesText by remember { mutableStateOf("1") }

    val isValid = title.isNotBlank() && time.matches(Regex("^\\d{2}:\\d{2}$")) && (occurrencesText.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(title.trim(), time.trim(), recurrence, (occurrencesText.toIntOrNull() ?: 1)) }, enabled = isValid) {
                Text(stringResource(R.string.planning_dialog_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        },
        title = { Text(stringResource(R.string.planning_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 4.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.planning_field_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        time = when (clean.length) {
                            0 -> ""
                            in 1..2 -> clean
                            in 3..4 -> clean.substring(0, 2) + ":" + clean.substring(2)
                            else -> clean.substring(0, 2) + ":" + clean.substring(2, 4)
                        }
                    },
                    label = { Text(stringResource(R.string.planning_field_time)) },
                    placeholder = { Text("08:30") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Recurrence dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = when (recurrence) {
                            com.gearsnap.model.Recurrence.NONE -> stringResource(R.string.recur_none)
                            com.gearsnap.model.Recurrence.DAILY -> stringResource(R.string.recur_daily)
                            com.gearsnap.model.Recurrence.WEEKLY -> stringResource(R.string.recur_weekly)
                            com.gearsnap.model.Recurrence.MONTHLY -> stringResource(R.string.recur_monthly)
                        },
                        onValueChange = {},
                        label = { Text(stringResource(R.string.recur_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.recur_none)) }, onClick = { recurrence = com.gearsnap.model.Recurrence.NONE; expanded = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.recur_daily)) }, onClick = { recurrence = com.gearsnap.model.Recurrence.DAILY; expanded = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.recur_weekly)) }, onClick = { recurrence = com.gearsnap.model.Recurrence.WEEKLY; expanded = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.recur_monthly)) }, onClick = { recurrence = com.gearsnap.model.Recurrence.MONTHLY; expanded = false })
                    }
                }

                // Occurrences count
                OutlinedTextField(
                    value = occurrencesText,
                    onValueChange = { input ->
                        occurrencesText = input.filter { it.isDigit() }.take(3)
                    },
                    label = { Text(stringResource(R.string.recur_occurrences)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
