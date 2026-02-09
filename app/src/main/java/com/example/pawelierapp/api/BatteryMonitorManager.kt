package com.example.pawelierapp.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * BatteryMonitorManager
 *
 * Purpose:
 * - Read current battery status (level, charging, health, status) from the system sticky
 *   broadcast Intent.ACTION_BATTERY_CHANGED.
 * - Provide a Flow to observe ongoing battery changes.
 *
 * Notes:
 * - We keep behavior unchanged. This file only adds comments and small readability tweaks.
 */
class BatteryMonitorManager(private val context: Context) {

    companion object {
        const val BATTERY_LEVEL_20_PERCENT = 20
        const val BATTERY_LEVEL_10_PERCENT = 10
    }

    /**
     * Returns the current battery level percentage in [0, 100].
     * Uses the sticky battery broadcast to avoid registering a receiver.
     */
    fun getCurrentBatteryLevel(): Int {
        // Sticky broadcast: returns the last Intent without a live receiver
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level != -1 && scale != -1) {
            ((level * 100) / scale.toFloat()).toInt()
        } else {
            // Fallback to 100% if we cannot read (keeps existing behavior)
            100
        }
    }

    /**
     * Returns whether the device is currently charging (or full and on power).
     */
    fun isCharging(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    /**
     * Aggregates battery info in a single data object.
     */
    fun getBatteryInfo(): BatteryInfo {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

        val batteryLevel = if (level != -1 && scale != -1) {
            ((level * 100) / scale.toFloat()).toInt()
        } else {
            100
        }

        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return BatteryInfo(
            level = batteryLevel,
            isCharging = charging,
            health = health,
            status = status
        )
    }

    /**
     * Observes battery changes as a Flow. Emits:
     * - Initial value immediately
     * - Updates on ACTION_BATTERY_CHANGED and related power events
     */
    fun observeBatteryLevel(): Flow<BatteryInfo> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Emit current snapshot each time we receive an event
                trySend(getBatteryInfo())
            }
        }

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        // Register the receiver and emit the first value immediately
        context.registerReceiver(receiver, intentFilter)
        trySend(getBatteryInfo())

        awaitClose {
            // Always unregister to avoid leaks
            context.unregisterReceiver(receiver)
        }
    }
}

/**
 * Represents a snapshot of battery information.
 */
data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean,
    val health: Int,
    val status: Int
) {
    /** True if we should show a low battery alert (<= 20% and not charging). */
    fun shouldShowLowBatteryAlert(): Boolean = !isCharging && (level <= 20)

    /** True if we should show a critical battery alert (<= 10% and not charging). */
    fun shouldShowCriticalBatteryAlert(): Boolean = !isCharging && (level <= 10)

    /** A human-friendly status string matching existing behavior. */
    fun getStatusText(): String = when {
        isCharging && level == 100 -> "Fully Charged"
        isCharging -> "Charging"
        level <= 10 -> "Critical"
        level <= 20 -> "Low"
        else -> "Normal"
    }
}

/**
 * Composable helper that wires a monitoring Flow into UI state.
 *
 * Parameters:
 * - isEnabled: when true, starts collecting battery updates.
 * - onBatteryAlert: called once when crossing <=20% (low) or <=10% (critical). The function
 *   is invoked with (level, isCritical=true|false).
 *
 * Behavior:
 * - Alert flags are reset when charging starts or when level rises above 20%.
 * - We preserve existing behavior and thresholds.
 */
@Composable
fun rememberBatteryMonitor(
    isEnabled: Boolean,
    onBatteryAlert: (level: Int, isCritical: Boolean) -> Unit
): BatteryMonitorState {
    val context = LocalContext.current
    val batteryManager = remember { BatteryMonitorManager(context) }

    var currentBatteryInfo by remember { mutableStateOf(batteryManager.getBatteryInfo()) }
    var hasShown20Alert by remember { mutableStateOf(false) }
    var hasShown10Alert by remember { mutableStateOf(false) }

    // Start/stop observing based on isEnabled
    LaunchedEffect(isEnabled) {
        if (isEnabled) {
            batteryManager.observeBatteryLevel().collect { info ->
                // Update current info from stream
                currentBatteryInfo = info

                // Reset when charging or battery rises above 20%
                if (info.isCharging || info.level > 20) {
                    hasShown20Alert = false
                    hasShown10Alert = false
                }

                // Fire alerts only when not charging
                if (!info.isCharging) {
                    // Critical (<=10%)
                    if (info.level <= 10 && !hasShown10Alert) {
                        hasShown10Alert = true
                        onBatteryAlert(info.level, true)
                    } else if (info.level <= 20 && info.level > 10 && !hasShown20Alert) {
                        // Low (<=20% and >10%)
                        hasShown20Alert = true
                        onBatteryAlert(info.level, false)
                    }
                }
            }
        }
    }

    return BatteryMonitorState(
        currentBatteryInfo = currentBatteryInfo,
        isEnabled = isEnabled
    )
}

/** Holds state for battery monitor UI consumers. */
data class BatteryMonitorState(
    val currentBatteryInfo: BatteryInfo,
    val isEnabled: Boolean
)
