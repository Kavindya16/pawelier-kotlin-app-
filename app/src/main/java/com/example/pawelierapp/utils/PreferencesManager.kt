package com.example.pawelierapp.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager class to handle app preferences using SharedPreferences
 * Persists user settings across app sessions
 */
class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "PawelierAppPreferences"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_BATTERY_ALERT = "battery_alert_enabled"
        private const val KEY_DARK_MODE_SET = "dark_mode_manually_set"
        private const val KEY_AMBIENT_LIGHT = "ambient_light_enabled"
    }

    /**
     * Save dark mode preference
     */
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .putBoolean(KEY_DARK_MODE_SET, true)
            .apply()
    }

    /**
     * Get dark mode preference
     * Returns the saved value, or null if user hasn't manually set it
     */
    fun getDarkMode(): Boolean? {
        return if (sharedPreferences.getBoolean(KEY_DARK_MODE_SET, false)) {
            sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        } else {
            null // Use system theme
        }
    }

    /**
     * Check if dark mode has been manually set by user
     */
    fun isDarkModeSet(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE_SET, false)
    }

    /**
     * Clear dark mode preference (revert to system theme)
     */
    fun clearDarkMode() {
        sharedPreferences.edit()
            .remove(KEY_DARK_MODE)
            .remove(KEY_DARK_MODE_SET)
            .apply()
    }

    /**
     * Save battery alert preference
     */
    fun setBatteryAlert(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT, enabled).apply()
    }

    /**
     * Get battery alert preference
     */
    fun getBatteryAlert(): Boolean {
        return sharedPreferences.getBoolean(KEY_BATTERY_ALERT, false)
    }

    /**
     * Save ambient light sensor preference
     */
    fun setAmbientLight(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AMBIENT_LIGHT, enabled).apply()
    }

    /**
     * Get ambient light sensor preference
     */
    fun getAmbientLight(): Boolean {
        return sharedPreferences.getBoolean(KEY_AMBIENT_LIGHT, false)
    }
}