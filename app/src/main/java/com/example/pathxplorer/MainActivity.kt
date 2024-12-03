package com.example.pathxplorer

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pathxplorer.databinding.ActivityMainBinding
import com.example.pathxplorer.ui.auth.LoginActivity
import com.example.pathxplorer.ui.main.MainViewModel
import com.example.pathxplorer.ui.main.SplashActivity
import com.example.pathxplorer.ui.utils.UserViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
//        enableEdgeToEdge()

//        val colorActionBar = if (isDarkModeEnabled()) "#121212" else "#FFFFFF"
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
//        supportActionBar?.elevation = 0f

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    private fun isDarkModeEnabled(): Boolean {
//        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
//    }
}