package com.example.mdxabsensi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.navigation.AppNavigation
import com.example.mdxabsensi.ui.theme.AbsenAppTheme
import com.example.mdxabsensi.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi Notification Channel
        NotificationHelper(this).createNotificationChannel()


        val userPreferences = UserPreferences(this)



        setContent {
            val themeMode by userPreferences.themeMode.collectAsState(initial = "system")
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            AbsenAppTheme(darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}