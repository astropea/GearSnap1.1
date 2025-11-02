package com.gearsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gearsnap.theme.*

@Composable
fun GSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GSButtonVariant = GSButtonVariant.PRIMARY,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val backgroundColor = when (variant) {
        GSButtonVariant.PRIMARY -> GS_ForestGreen
        GSButtonVariant.SECONDARY -> GS_WarmOrange
        GSButtonVariant.OUTLINE -> Color.Transparent
    }

    val contentColor = when (variant) {
        GSButtonVariant.PRIMARY -> Color.White
        GSButtonVariant.SECONDARY -> Color.White
        GSButtonVariant.OUTLINE -> GS_ForestGreen
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (variant != GSButtonVariant.OUTLINE) 4.dp else 0.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = if (variant == GSButtonVariant.OUTLINE) {
            BorderStroke(
                width = 2.dp,
                color = GS_ForestGreen
            )
        } else null,
        shape = RoundedCornerShape(20.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

enum class GSButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE
}

@Composable
fun GSPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ImageVector? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) GS_ForestGreen else GS_SearchBg,
        contentColor = if (selected) Color.White else GS_BodyGray
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}