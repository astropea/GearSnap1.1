package com.gearsnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gearsnap.R
import com.gearsnap.ui.activities.LanguageManager
import com.gearsnap.ui.activities.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Language state
    val currentLanguage = remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }
    val languageOptions = mapOf(
        "fr" to stringResource(R.string.language_french),
        "en" to stringResource(R.string.language_english),
        "de" to stringResource(R.string.language_german)
    )

    // Theme state
    val systemDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val savedTheme = remember { mutableStateOf(ThemeManager.getSavedTheme(context)) }
    val isDarkTheme = savedTheme.value ?: systemDarkTheme

    // Notifications state
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Dialog states
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.nav_profile),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        // Profile Section
        ProfileSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Section
        StatsSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Badges Section
        BadgesSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Section
        SettingsSection(
            currentLanguage = currentLanguage.value,
            languageOptions = languageOptions,
            isDarkTheme = isDarkTheme,
            notificationsEnabled = notificationsEnabled,
            onLanguageClick = { showLanguageDialog = true },
            onThemeClick = { showThemeDialog = true },
            onNotificationToggle = { notificationsEnabled = it },
            onVersionClick = { showVersionDialog = true },
            onPrivacyClick = { showPrivacyDialog = true },
            onTermsClick = { showTermsDialog = true }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Language Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage.value,
            languageOptions = languageOptions,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { languageCode ->
                LanguageManager.setLanguage(context, languageCode)
                currentLanguage.value = languageCode
                showLanguageDialog = false
                LanguageManager.restartActivity(context)
            }
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = savedTheme.value,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { isDark ->
                ThemeManager.setTheme(context, isDark)
                savedTheme.value = isDark
                showThemeDialog = false
                ThemeManager.applyThemeAndRestart(context)
            }
        )
    }

    // Version Dialog
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = {
                Text(stringResource(R.string.settings_version))
            },
            text = {
                Text("GearSnap v1.1.0\n\nMade with ❤️ in France")
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(stringResource(R.string.settings_privacy))
            },
            text = {
                Text("Nous respectons votre vie privée et ne partageons vos données qu'avec votre consentement explicite.")
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(stringResource(R.string.settings_terms))
            },
            text = {
                Text("En utilisant GearSnap, vous acceptez nos conditions d'utilisation disponibles sur notre site web.")
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }
}

@Composable
fun ProfileSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = stringResource(R.string.profile_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        Text(
            text = stringResource(R.string.profile_email),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Member Since
        Text(
            text = stringResource(R.string.profile_member_since),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_stats),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.DirectionsRun,
                    value = "127",
                    label = stringResource(R.string.profile_outings)
                )
                StatItem(
                    icon = Icons.Default.Timer,
                    value = "234h",
                    label = stringResource(R.string.profile_duration)
                )
                StatItem(
                    icon = Icons.Default.Terrain,
                    value = "892km",
                    label = stringResource(R.string.profile_distance)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadgesSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_badges),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BadgeItem(
                    icon = Icons.Default.Star,
                    label = stringResource(R.string.profile_beginner),
                    unlocked = true
                )
                BadgeItem(
                    icon = Icons.Default.EmojiEvents,
                    label = stringResource(R.string.profile_champion),
                    unlocked = true
                )
                BadgeItem(
                    icon = Icons.Default.LocalFireDepartment,
                    label = stringResource(R.string.profile_streak),
                    unlocked = false
                )
                BadgeItem(
                    icon = Icons.Default.MilitaryTech,
                    label = stringResource(R.string.profile_expert),
                    unlocked = false
                )
            }
        }
    }
}

@Composable
fun BadgeItem(
    icon: ImageVector,
    label: String,
    unlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (unlocked) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (unlocked) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSection(
    currentLanguage: String,
    languageOptions: Map<String, String>,
    isDarkTheme: Boolean,
    notificationsEnabled: Boolean,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onVersionClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_general),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_language),
                subtitle = languageOptions[currentLanguage],
                onClick = onLanguageClick
            )

            SettingsItem(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.settings_theme),
                subtitle = if (isDarkTheme) stringResource(R.string.theme_dark) else stringResource(R.string.theme_light),
                onClick = onThemeClick
            )

            SettingsItemWithSwitch(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.settings_notifications),
                isChecked = notificationsEnabled,
                onCheckedChange = onNotificationToggle
            )

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = stringResource(R.string.settings_about),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_version),
                subtitle = "1.1.0",
                onClick = onVersionClick
            )

            SettingsItem(
                icon = Icons.Default.Security,
                title = stringResource(R.string.settings_privacy),
                onClick = onPrivacyClick
            )

            SettingsItem(
                icon = Icons.Default.Description,
                title = stringResource(R.string.settings_terms),
                onClick = onTermsClick
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }
}


@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    languageOptions: Map<String, String>,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.dialog_select_language))
        },
        text = {
            Column {
                languageOptions.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(code)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == code,
                            onClick = { onLanguageSelected(code) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_ok))
            }
        }
    )
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: Boolean?,
    onDismiss: () -> Unit,
    onThemeSelected: (Boolean?) -> Unit
) {
    val options = listOf(
        null to stringResource(R.string.theme_system),
        false to stringResource(R.string.theme_light),
        true to stringResource(R.string.theme_dark)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.dialog_select_theme))
        },
        text = {
            Column {
                options.forEach { (isDark, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(isDark)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == isDark,
                            onClick = { onThemeSelected(isDark) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_ok))
            }
        }
    )
}
