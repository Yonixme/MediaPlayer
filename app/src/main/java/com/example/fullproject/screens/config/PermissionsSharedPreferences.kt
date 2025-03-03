package com.example.fullproject.screens.config

import android.content.Context

object PermissionsSharedPreferences {
    private const val PREFS_NAME = "permission_prefs"
    private const val KEY_PERMISSION_ASKED = "permission_asked"

    fun setPermissionAsked(context: Context, asked: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_PERMISSION_ASKED, asked).apply()
    }

    fun wasPermissionAsked(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PERMISSION_ASKED, false)
    }
}