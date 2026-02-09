package com.example.pawelierapp.api

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * TopBatteryAlert
 *
 * Purpose:
 * - Show a transient toast-like battery alert from the top of the screen.
 * - Animates in/out and auto-dismisses after 4 seconds; calls onDismiss when done.
 *
 * Notes:
 * - Behavior and visuals are preserved. Only comments and tiny readability tweaks added.
 */
@Composable
fun TopBatteryAlert(
    level: Int,
    isCritical: Boolean,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    // Local visibility drives the AnimatedVisibility; we keep it separate from the external flag
    var isVisible by remember { mutableStateOf(false) }

    // When external visibility flips to true, show the alert for a fixed duration
    LaunchedEffect(visible) {
        if (visible) {
            isVisible = true
            delay(4000) // Show for 4 seconds (unchanged)
            isVisible = false
            delay(300) // Wait for exit animation to complete (unchanged)
            onDismiss()
        }
    }

    // Entrance: slide in from top + fade in; Exit: slide out to top + fade out
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCritical) {
                        Color(0xFFD32F2F) // Red for critical
                    } else {
                        Color(0xFFFF9800) // Orange for low
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Leading icon: Warning for critical; BatteryAlert for low
                    Icon(
                        imageVector = if (isCritical) Icons.Filled.Warning else Icons.Filled.BatteryAlert,
                        contentDescription = "Battery Alert",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )

                    // Text content (title + message)
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isCritical) "Critical Battery" else "Low Battery",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Battery level is at $level%. ${if (isCritical) "Please charge immediately!" else "Please charge your device soon."}",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 13.sp
                        )
                    }

                    // Battery percentage chip on the right
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "$level%",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * BatteryAlertState
 *
 * Purpose:
 * - Simple state holder for the top alert: visible flag, level, and critical-ness.
 * - Provides show/dismiss helpers for use by UI.
 */
class BatteryAlertState {
    private val _showAlert = mutableStateOf(false)
    private val _batteryLevel = mutableStateOf(100)
    private val _isCritical = mutableStateOf(false)

    val showAlert: Boolean get() = _showAlert.value
    val batteryLevel: Int get() = _batteryLevel.value
    val isCritical: Boolean get() = _isCritical.value

    /** Update values and show the alert. */
    fun showBatteryAlert(level: Int, critical: Boolean) {
        _batteryLevel.value = level
        _isCritical.value = critical
        _showAlert.value = true
    }

    /** Hide the alert. */
    fun dismissAlert() {
        _showAlert.value = false
    }
}

/**
 * Remember helper to create and retain BatteryAlertState across recompositions.
 */
@Composable
fun rememberBatteryAlertState(): BatteryAlertState = remember { BatteryAlertState() }
