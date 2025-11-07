package com.gearsnap.auth.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gearsnap.auth.AuthViewModel
import com.gearsnap.auth.components.*

/**
 * Écran principal d'authentification
 * Affiche les options de connexion et d'inscription
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // State pour les formulaires
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Launcher pour Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onGoogleSignInResult(result.data)
    }

    // Observer le succès de l'authentification
    LaunchedEffect(viewModel.isAuthenticated) {
        if (viewModel.isAuthenticated) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5EE)) // Fond beige très clair
            .padding(16.dp)
    ) {
        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthCard(
                onEmailLogin = { showLoginDialog = true },
                onGoogleSignIn = {
                    val intent = viewModel.onGoogleSignIn()
                    if (intent != null) {
                        googleSignInLauncher.launch(intent)
                    }
                },
                onAppleSignIn = { viewModel.onAppleSignIn() },
                onRegister = { showRegisterDialog = true },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Affichage des erreurs
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Dialog de connexion
        if (showLoginDialog) {
            LoginRegisterDialog(
                title = "Connexion",
                onDismiss = { showLoginDialog = false },
                onSubmit = { email, password, name ->
                    viewModel.loginWithEmail(email, password)
                    showLoginDialog = false
                },
                isLoading = isLoading,
                showNameField = false
            )
        }

        // Dialog d'inscription
        if (showRegisterDialog) {
            LoginRegisterDialog(
                title = "Inscription",
                onDismiss = { showRegisterDialog = false },
                onSubmit = { email, password, name ->
                    viewModel.register(email, password, name)
                    showRegisterDialog = false
                },
                isLoading = isLoading,
                showNameField = true
            )
        }
    }
}

/**
 * Carte principale d'authentification avec logo et boutons
 */
@Composable
fun AuthCard(
    onEmailLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    onRegister: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo et titre de l'app
            AppHeader()

            // Boutons d'authentification
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Continuer avec l'e-mail
                AuthButton(
                    onClick = onEmailLogin,
                    text = "Continuer avec l'e-mail",
                    icon = Icons.Default.Email,
                    isLoading = isLoading
                )

                // Continuer avec Google
                AuthButton(
                    onClick = onGoogleSignIn,
                    text = "Continuer avec Google",
                    icon = null, // TODO: Remplacer par l'icône Google réelle
                    isLoading = isLoading
                )

                // Continuer avec Apple
                AuthButton(
                    onClick = onAppleSignIn,
                    text = "Continuer avec Apple",
                    icon = null, // TODO: Remplacer par l'icône Apple réelle
                    isLoading = isLoading
                )
            }

            // Bouton principal d'inscription
            PrimaryButton(
                onClick = onRegister,
                text = "Créer mon compte",
                isLoading = isLoading
            )
        }
    }
}

/**
 * En-tête de l'application avec logo et nom
 */
@Composable
fun AppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFF2E6B4A),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Remplacer par le vrai logo GearSnap
            // Image(painter = painterResource(R.drawable.gearsnap_logo), ...)
            Text(
                text = "G",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Nom de l'app
        Text(
            text = "GearSnap",
            color = Color(0xFF2E6B4A), // Vert foncé GearSnap
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        // Sous-titre
        Text(
            text = "Connexion / Inscription",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

/**
 * Dialog pour connexion et inscription
 */
@Composable
fun LoginRegisterDialog(
    title: String,
    onDismiss: () -> Unit,
    onSubmit: (email: String, password: String, name: String?) -> Unit,
    isLoading: Boolean,
    showNameField: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Titre
                Text(
                    text = title,
                    color = Color(0xFF2E6B4A),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Champ nom (seulement pour l'inscription)
                if (showNameField) {
                    StandardTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Nom (optionnel)"
                    )
                }

                // Champ email
                EmailTextField(
                    value = email,
                    onValueChange = { email = it }
                )

                // Champ mot de passe
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                )

                // Boutons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Annuler
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Annuler")
                    }

                    // Se connecter / S'inscrire
                    PrimaryButton(
                        onClick = {
                            onSubmit(email, password, if (showNameField) name else null)
                        },
                        text = if (showNameField) "S'inscrire" else "Se connecter",
                        modifier = Modifier.weight(1f),
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}