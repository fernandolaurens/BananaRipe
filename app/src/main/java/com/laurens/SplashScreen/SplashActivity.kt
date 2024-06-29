package com.laurens.SplashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.laurens.bananaripe.MainActivity
import com.laurens.bananaripe.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT: Long = 3000
    }
}