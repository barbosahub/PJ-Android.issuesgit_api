package com.android.githubapi.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.android.githubapi.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        timerRedirect()
    }
    private fun timerRedirect() {
        Handler(Looper.getMainLooper()).postDelayed({ redirectActivity() }, getSplashTimer())
    }

    private fun getSplashTimer(): Long {
        val sharedPreferences =
            getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val firstLogin = sharedPreferences.getBoolean("FIRST_LOGIN_SPLASH", true)
        if (firstLogin) {
            editor.putBoolean("FIRST_LOGIN_SPLASH", false)
            editor.commit()
            return 4000
        }
        return 2000
    }

    private fun redirectActivity() {
        val redirect = Intent(this, MainActivity::class.java)
        startActivity(redirect)
        finish()
    }
}