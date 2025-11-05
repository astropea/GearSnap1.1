package com.gearsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.gearsnap.ui.screens.SpotCategory

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddSpotDialog(
    initialName: String = "",
    initialCategory: SpotCategory = SpotCategory.HIKING,
    onDismiss: () -> Unit,
    onAdd: (name: String, category: SpotCategory) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onAdd(name.text.trim(), category) },
                enabled = name.text.isNotBlank()
            ) { Text("Ajouter") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
        title = { Text("Ajouter un spot") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du spot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Simple category dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category.display,
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        SpotCategory.values().forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.display) },
                                onClick = {
                                    category = c
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
