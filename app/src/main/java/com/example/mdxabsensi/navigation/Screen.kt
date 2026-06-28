package com.example.mdxabsensi.navigation


sealed class Screen(
    val route: String
) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Dashboard : Screen("dashboard")

    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object Camera : Screen("camera/{type}/{nik}") {
        fun createRoute(type: String, nik: String) = "camera/$type/$nik"
    }
    data object Riwayat : Screen("riwayat")
    data object Kalender : Screen("kalender")

    data object Notification : Screen("notification")
    data object ChangePassword : Screen("change_password")

}