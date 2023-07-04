package com.example.app_locker.directory.utils

import android.content.Context

object GetCheckedApps {
    fun getSelectedApps(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val selectedApps = sharedPref.getStringSet("selectedApps", mutableSetOf())
        return selectedApps?.toList() ?: listOf()
    }
}