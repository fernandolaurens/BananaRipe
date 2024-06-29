package com.laurens.bananaripe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_beranda

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_detection -> {
                    startActivity(Intent(this@MainActivity, DeteksiActivity::class.java))
                    true
                }
                R.id.bottom_information -> {
                    startActivity(Intent(this@MainActivity, InformasiActivity::class.java))
                    true
                }
                R.id.bottom_profile -> {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                    true
                }

                else -> false
            }

        }
    }

}