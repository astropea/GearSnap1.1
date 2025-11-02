package com.gearsnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SocialScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Fil social & messages", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Text(text = "Placeholder — connectez Firebase/Map/AI dans le repository.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}