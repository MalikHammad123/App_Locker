package com.example.app_locker.activities.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.app_locker.R
import com.example.app_locker.activities.login.LoginActivity
import com.example.app_locker.activities.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this, LoginActivity::class.java))

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }
}