package com.example.dermai.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.dermai.R
import com.example.dermai.ui.home.HomeActivity
import com.example.dermai.ui.landing.LandingActivity
import com.example.dermai.utils.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        finish()

        if(PreferenceManager.getInstance(this).isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }
    }
}