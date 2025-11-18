package com.gearsnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
    // La nouvelle fonction `onConfirm` remonte toutes les données saisies vers le ViewModel.
    onConfirm: (name: String, category: SpotCategory, difficulty: String, description: String) -> Unit,
    onPickPhoto: () -> Unit,
    selectedPhotoLabel: String? = null,
    // Indique si un chargement est en cours pour afficher un loader.
    isAdding: Boolean
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(SpotCategory.HIKING) }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Facile") }

    // Le formulaire est valide si le nom n'est pas vide et que les coordonnées existent.
    val isFormValid = name.isNotBlank() && initialLat != null && initialLng != null

    AlertDialog(
        onDismissRequest = { if (!isAdding) onDismiss() }, // Empêche de fermer pendant le chargement
        confirmButton = {
            Button(
                onClick = {
                    // Appelle la fonction onConfirm avec toutes les données du formulaire.
                    onConfirm(name.trim(), category, difficulty, description.trim())
                },
                // Le bouton est activé uniquement si le formulaire est valide ET qu'aucun ajout n'est en cours.
                enabled = isFormValid && !isAdding
            ) {
                if (isAdding) {
                    // Affiche une roue de chargement si `isAdding` est vrai.
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ajouter")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isAdding // Désactive le bouton "Annuler" pendant l'ajout.
            ) { Text("Annuler") }
        },
        title = { Text("Ajouter un spot") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du spot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isAdding // Rend les champs non modifiables pendant l'ajout.
                )

                // Dropdown pour la catégorie
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { if (!isAdding) categoryExpanded = it }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category.display,
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        SpotCategory.values().forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.display) },
                                onClick = {
                                    category = c
                                    categoryExpanded = false
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
                    maxLines = 5,
                    readOnly = isAdding
                )

                // Dropdown pour la difficulté
                var difficultyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = difficultyExpanded, onExpandedChange = { if (!isAdding) difficultyExpanded = it }) {
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
