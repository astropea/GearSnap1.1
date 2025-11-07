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
    initialLat: Double? = null,
    initialLng: Double? = null,
    onDismiss: () -> Unit,
    onAdd: (name: String, category: SpotCategory, lat: Double, lng: Double) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var difficulty by remember { mutableStateOf("Facile") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val lat = initialLat ?: 48.8566
                    val lng = initialLng ?: 2.3522
                    onAdd(name.text.trim(), category, lat, lng)
                },
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

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Difficulty dropdown
                var difficultyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = difficultyExpanded, onExpandedChange = { difficultyExpanded = it }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = difficulty,
                        onValueChange = {},
                        label = { Text("Difficulté") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = difficultyExpanded, onDismissRequest = { difficultyExpanded = false }) {
                        listOf("Facile", "Moyen", "Difficile", "Expert").forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff) },
                                onClick = {
                                    difficulty = diff
                                    difficultyExpanded = false
                                }
                            )
                        }
                    }
                }

                // Afficher les coordonnées si disponibles
                if (initialLat != null && initialLng != null) {
                    Text(
                        text = "📍 Position: ${String.format("%.4f", initialLat)}, ${String.format("%.4f", initialLng)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
