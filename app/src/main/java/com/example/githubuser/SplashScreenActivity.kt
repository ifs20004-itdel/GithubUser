package com.example.githubuser

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.githubuser.ui.BottomNavigationActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler().postDelayed(
            {
                val intent = Intent(this@SplashScreenActivity, BottomNavigationActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000
        )
    }
}