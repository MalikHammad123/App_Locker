package com.example.app_locker.directory.utils

import android.content.Context
import android.util.Log

object SwitchHandler {

    private const val SHARED_PREFS_NAME = "fingerprint"
     const val SWITCH_STATE_KEY = "SwitchState"

    fun saveSwitchState(context: Context, state: Boolean) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(SWITCH_STATE_KEY, state)
            apply()
        }
        Log.d("SwitchHandler", "Saved switch state: $state")
    }

    fun getSwitchState(context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val state = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
        Log.d("SwitchHandler" , "retrived switch state: $state")
        return state
    }

}