package com.example.githubuser.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.githubuser.R
import com.example.githubuser.databinding.ActivityBottomNavigationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationActivity : AppCompatActivity() {
    private var binding: ActivityBottomNavigationBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navView: BottomNavigationView? = binding?.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView?.setupWithNavController(navController)
    }
}