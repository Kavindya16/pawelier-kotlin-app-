package com.example.pawelierapp.api

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * AmbientLightSensorManager
 *
 * Purpose:
 * - Detect ambient light levels using the device's light sensor.
 * - Provide a Flow to observe light level changes.
 * - Classify light conditions (Very Dark, Dark, Dim, Normal, Bright, Very Bright).
 *
 * Notes:
 * - Light is measured in lux (lx).
 * - Values typically range from 0 (complete darkness) to 100,000+ (direct sunlight).
 */
class AmbientLightSensorManager(private val context: Context) {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val lightSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    companion object {
        // Light level thresholds in lux
        const val VERY_DARK = 0f
        const val DARK = 10f
        const val DIM = 50f
        const val NORMAL = 200f
        const val BRIGHT = 1000f
        const val VERY_BRIGHT = 5000f
    }

    /**
     * Checks if the device has an ambient light sensor.
     */
    fun hasLightSensor(): Boolean = lightSensor != null

    /**
     * Observes ambient light level changes as a Flow.
     * Emits LightInfo whenever the sensor detects a change.
     */
    fun observeLightLevel(): Flow<LightInfo> = callbackFlow {
        if (lightSensor == null) {
            // If no sensor available, emit a default value and close
            trySend(LightInfo(0f, "No Sensor Available"))
            close()
            return@callbackFlow
        }

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // event.values[0] contains the ambient light level in lux
                val luxValue = event.values[0]
                val condition = classifyLightLevel(luxValue)
                trySend(LightInfo(luxValue, condition))
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for light sensor, but required by interface
            }
        }

        // Register sensor listener with normal sampling rate
        val registered = sensorManager.registerListener(
            sensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        if (!registered) {
            // Failed to register
            trySend(LightInfo(0f, "Sensor Registration Failed"))
            close()
            return@callbackFlow
        }

        // Emit initial state
        trySend(LightInfo(0f, "Initializing..."))

        awaitClose {
            // Always unregister to prevent battery drain
            sensorManager.unregisterListener(sensorListener)
        }
    }

    /**
     * Classifies the light level into a human-friendly condition.
     */
    private fun classifyLightLevel(lux: Float): String = when {
        lux < DARK -> "Very Dark"
        lux < DIM -> "Dark"
        lux < NORMAL -> "Dim"
        lux < BRIGHT -> "Normal"
        lux < VERY_BRIGHT -> "Bright"
        else -> "Very Bright"
    }
}

/**
 * Represents ambient light information.
 */
data class LightInfo(
    val lux: Float,
    val condition: String
) {
    /**
     * Returns a formatted string for displaying lux value.
     */
    fun getFormattedLux(): String = String.format("%.1f lx", lux)

    /**
     * Returns a descriptive message about current light conditions.
     */
    fun getDetailedDescription(): String = when {
        lux < AmbientLightSensorManager.DARK -> "Ambient Light: $condition (${getFormattedLux()})"
        lux < AmbientLightSensorManager.DIM -> "Ambient Light: $condition (${getFormattedLux()})"
        lux < AmbientLightSensorManager.NORMAL -> "Ambient Light: $condition (${getFormattedLux()})"
        lux < AmbientLightSensorManager.BRIGHT -> "Ambient Light: $condition (${getFormattedLux()})"
        lux < AmbientLightSensorManager.VERY_BRIGHT -> "Ambient Light: $condition (${getFormattedLux()})"
        else -> "Ambient Light: $condition (${getFormattedLux()})"
    }
}

/**
 * Composable helper that wires the ambient light sensor Flow into UI state.
 *
 * Parameters:
 * - isEnabled: when true, starts collecting light sensor updates.
 * - onLightLevelChange: called when light level changes significantly.
 *
 * Behavior:
 * - Monitors light level and provides real-time updates.
 * - Calls onLightLevelChange callback with the latest LightInfo.
 */
@Composable
fun rememberAmbientLightSensor(
    isEnabled: Boolean,
    onLightLevelChange: (LightInfo) -> Unit
): AmbientLightSensorState {
    val context = LocalContext.current
    val lightSensorManager = remember { AmbientLightSensorManager(context) }

    var currentLightInfo by remember {
        mutableStateOf(LightInfo(0f, "Sensor Not Active"))
    }

    var previousCondition by remember { mutableStateOf<String?>(null) }

    // Start/stop observing based on isEnabled
    LaunchedEffect(isEnabled) {
        if (isEnabled && lightSensorManager.hasLightSensor()) {
            lightSensorManager.observeLightLevel().collect { info ->
                currentLightInfo = info

                // Only trigger callback when condition changes
                // This prevents too many toast messages
                if (info.condition != previousCondition && info.condition != "Initializing...") {
                    previousCondition = info.condition
                    onLightLevelChange(info)
                }
            }
        } else if (isEnabled && !lightSensorManager.hasLightSensor()) {
            // Device doesn't have a light sensor
            currentLightInfo = LightInfo(0f, "No Sensor Available")
        } else {
            // Reset when disabled
            currentLightInfo = LightInfo(0f, "Sensor Not Active")
            previousCondition = null
        }
    }

    return AmbientLightSensorState(
        currentLightInfo = currentLightInfo,
        isEnabled = isEnabled,
        hasSensor = lightSensorManager.hasLightSensor()
    )
}

/**
 * Holds state for ambient light sensor UI consumers.
 */
data class AmbientLightSensorState(
    val currentLightInfo: LightInfo,
    val isEnabled: Boolean,
    val hasSensor: Boolean
)