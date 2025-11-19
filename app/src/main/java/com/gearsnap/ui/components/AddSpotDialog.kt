package com.gearsnap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gearsnap.ui.screens.SpotCategory

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddSpotDialog(
    initialLat: Double?,
    initialLng: Double?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: SpotCategory, difficulty: String, description: String) -> Unit,
    onPickPhoto: () -> Unit,
    selectedPhotoCount: Int = 0,
    isAdding: Boolean
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(SpotCategory.HIKING) }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Facile") }

    var categoryDropdown by remember { mutableStateOf(false) }
    var difficultyDropdown by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && initialLat != null && initialLng != null

    AlertDialog(
        onDismissRequest = { if (!isAdding) onDismiss() },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim(), category, difficulty, description.trim()) },
                enabled = isFormValid && !isAdding
            ) {
                if (isAdding) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ajouter")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isAdding) { Text("Annuler") }
        },
        title = { Text("Ajouter un spot") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du spot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isAdding
                )

                // Catégorie (simple dropdown manual)
                OutlinedTextField(
                    value = category.display,
                    onValueChange = {},
                    label = { Text("Categorie") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { if (!isAdding) categoryDropdown = !categoryDropdown }) {
                            Icon(
                                imageVector = if (categoryDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                )
                if (categoryDropdown) {
                    Column {
                        SpotCategory.values().forEach { c ->
                            TextButton(
                                onClick = {
                                    category = c
                                    categoryDropdown = false
                                },
                                enabled = !isAdding,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(c.display) }
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    readOnly = isAdding
                )

                OutlinedTextField(
                    value = difficulty,
                    onValueChange = {},
                    label = { Text("Difficulte") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { if (!isAdding) difficultyDropdown = !difficultyDropdown }) {
                            Icon(
                                imageVector = if (difficultyDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                )
                if (difficultyDropdown) {
                    Column {
                        listOf("Facile", "Moyen", "Difficile", "Expert").forEach { diff ->
                            TextButton(
                                onClick = {
                                    difficulty = diff
                                    difficultyDropdown = false
                                },
                                enabled = !isAdding,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(diff) }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { if (!isAdding) onPickPhoto() }, enabled = !isAdding) {
                    val label = if (selectedPhotoCount > 0) "$selectedPhotoCount photos selectionnees" else "Ajouter des photos"
                    Text(label)
                }

                if (initialLat != null && initialLng != null) {
                    Text(
                        text = "Position: ${"%.4f".format(initialLat)}, ${"%.4f".format(initialLng)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
