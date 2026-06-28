package com.example.mdxabsensi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.*
import com.example.mdxabsensi.ui.dashboard.DashboardScreen
import com.example.mdxabsensi.ui.splash.SplashScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext

import com.example.mdxabsensi.repository.AuthRepository
import com.example.mdxabsensi.datastore.UserPreferences

import com.example.mdxabsensi.viewmodel.LoginViewModel
import com.example.mdxabsensi.viewmodel.LoginViewModelFactory
import com.example.mdxabsensi.ui.login.LoginRoute

import com.example.mdxabsensi.viewmodel.RegisterViewModel
import com.example.mdxabsensi.viewmodel.RegisterViewModelFactory
import com.example.mdxabsensi.ui.register.RegisterRoute

import com.example.mdxabsensi.viewmodel.SplashViewModel
import com.example.mdxabsensi.viewmodel.SplashViewModelFactory

import com.example.mdxabsensi.viewmodel.DashboardViewModel
import com.example.mdxabsensi.viewmodel.DashboardViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.mdxabsensi.viewmodel.ProfileViewModel
import com.example.mdxabsensi.viewmodel.ProfileViewModelFactory
import com.example.mdxabsensi.ui.profile.ProfileScreen
import com.example.mdxabsensi.ui.profile.EditProfileRoute
import com.example.mdxabsensi.ui.profile.ChangePasswordScreen
import com.example.mdxabsensi.repository.UserRepository
import com.example.mdxabsensi.ui.camera.CameraRoute
import com.example.mdxabsensi.repository.AbsensiRepository
import com.example.mdxabsensi.utils.NotificationHelper
import com.example.mdxabsensi.viewmodel.AbsensiViewModel
import com.example.mdxabsensi.viewmodel.AbsensiViewModelFactory
import com.example.mdxabsensi.ui.riwayat.RiwayatRoute
import com.example.mdxabsensi.ui.notification.NotificationScreen
import com.example.mdxabsensi.viewmodel.RiwayatViewModel
import com.example.mdxabsensi.viewmodel.RiwayatViewModelFactory
import com.example.mdxabsensi.viewmodel.KalenderViewModel
import com.example.mdxabsensi.viewmodel.KalenderViewModelFactory
import com.example.mdxabsensi.ui.kalender.KalenderScreen
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val userPreferences = remember { UserPreferences(context) }
    val themeMode by userPreferences.themeMode.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()

    val sharedProfileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            UserRepository(),
            userPreferences
        )
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {

            val context =
                LocalContext.current

            val splashViewModel: SplashViewModel =
                viewModel(

                    factory =
                        SplashViewModelFactory(

                            UserPreferences(
                                context
                            )

                        )

                )

            SplashScreen(

                viewModel = splashViewModel,

                onNavigateLogin = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(
                            Screen.Splash.route
                        ) {
                            inclusive = true
                        }

                    }

                },

                onNavigateDashboard = {

                    navController.navigate(
                        Screen.Dashboard.route
                    ) {

                        popUpTo(
                            Screen.Splash.route
                        ) {
                            inclusive = true
                        }

                    }

                }

            )

        }

        composable(
            Screen.Login.route
        ) {

            val context =
                LocalContext.current

            val viewModel: LoginViewModel =
                viewModel(

                    factory =
                        LoginViewModelFactory(

                            AuthRepository(),

                            UserPreferences(
                                context
                            )

                        )
                )

            LoginRoute(
                viewModel = viewModel,
                themeMode = themeMode,
                onThemeToggle = {
                    scope.launch {
                        userPreferences.toggleTheme()
                    }
                },

                onNavigateDashboard = {

                    navController.navigate(
                        Screen.Dashboard.route
                    )

                },

                onNavigateRegister = {

                    navController.navigate(
                        Screen.Register.route
                    )

                }
            )
        }

        composable(
            Screen.Register.route
        ) {

            val viewModel:
                    RegisterViewModel =
                viewModel(

                    factory =
                        RegisterViewModelFactory(
                            AuthRepository()
                        )

                )

            RegisterRoute(

                viewModel = viewModel,

                onRegisterSuccess = {

                    navController.popBackStack()

                }

            )

        }

        composable(Screen.Dashboard.route) {
            val context =
                LocalContext.current

            val dashboardViewModel:
                    DashboardViewModel =
                viewModel(

                    factory =
                        DashboardViewModelFactory(
                            AbsensiRepository(),
                            UserPreferences(
                                context
                            )
                        )

                )
            val nama by
            sharedProfileViewModel.nama.collectAsState()

            val nik by
            sharedProfileViewModel.nik.collectAsState()

            val foto by
            sharedProfileViewModel.foto.collectAsState()

            val fotoLastUpdated by
            sharedProfileViewModel.fotoLastUpdated.collectAsState()

            val monthlyMasukCount by
            dashboardViewModel.monthlyMasukCount.collectAsState()

            val monthlyKeluarCount by
            dashboardViewModel.monthlyKeluarCount.collectAsState()

            val monthlyTotalHours by
            dashboardViewModel.monthlyTotalHours.collectAsState()

            val checkInTime by
            dashboardViewModel.checkInTime.collectAsState()

            val checkOutTime by
            dashboardViewModel.checkOutTime.collectAsState()

            val todayWorkHours by
            dashboardViewModel.todayWorkHours.collectAsState()

            val absensiViewModel: AbsensiViewModel = viewModel(
                factory = AbsensiViewModelFactory(AbsensiRepository(), notificationHelper)
            )
            val absensiLoading by absensiViewModel.isLoading.collectAsState()
            val absensiSuccess by absensiViewModel.isSuccess.collectAsState()
            val absensiError by absensiViewModel.error.collectAsState()

            DashboardScreen(

                nama = nama,

                nik = nik,

                foto = foto,

                fotoLastUpdated = fotoLastUpdated,

                monthlyMasukCount = monthlyMasukCount,

                monthlyKeluarCount = monthlyKeluarCount,

                monthlyTotalHours = monthlyTotalHours,

                absensiLoading = absensiLoading,

                absensiSuccess = absensiSuccess,

                absensiError = absensiError,

                checkInTime = checkInTime,

                checkOutTime = checkOutTime,

                todayWorkHours = todayWorkHours,

                onLogout = {

                    dashboardViewModel.logout()

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(
                            Screen.Dashboard.route
                        ) {
                            inclusive = true
                        }

                    }

                },

                onProfileClick = {

                    navController.navigate(
                        Screen.Profile.route
                    )

                },

                onAbsenCameraClick = { type ->
                    navController.navigate(
                        Screen.Camera.createRoute(type, nik)
                    )
                },

                onAbsenGallerySubmit = { type, fotoBase64, lat, lon ->
                    absensiViewModel.absensi(nik, type, fotoBase64, lat, lon)
                },

                onResetAbsensiStatus = {
                    absensiViewModel.resetStatus()
                },

                onRefreshStatus = {
                    dashboardViewModel.fetchTodayAbsensiStatus()
                    sharedProfileViewModel.loadProfileFromPreferences()
                    sharedProfileViewModel.fetchUserProfile()
                },

                onRiwayatClick = {
                    navController.navigate(
                        Screen.Riwayat.route
                    )
                },

                onKalenderClick = {
                    navController.navigate(
                        Screen.Kalender.route
                    )
                },

                onNotificationClick = {
                    navController.navigate(
                        Screen.Notification.route
                    )
                }

            )
        }

        composable(
            Screen.Notification.route
        ) {
            val riwayatViewModel: RiwayatViewModel = viewModel(
                factory = RiwayatViewModelFactory(AbsensiRepository())
            )
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(
                    AbsensiRepository(),
                    UserPreferences(LocalContext.current)
                )
            )
            val nik by dashboardViewModel.nik.collectAsState()
            val notifications by riwayatViewModel.riwayatList.collectAsState()

            androidx.compose.runtime.LaunchedEffect(nik) {
                if (nik.isNotEmpty()) {
                    riwayatViewModel.fetchRiwayat(nik)
                }
            }

            NotificationScreen(notifications = notifications)
        }

        composable(
            Screen.Camera.route
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "masuk"
            val nik = backStackEntry.arguments?.getString("nik") ?: ""

            val absensiViewModel: AbsensiViewModel = viewModel(
                factory = AbsensiViewModelFactory(AbsensiRepository(), notificationHelper)
            )

            CameraRoute(
                type = type,
                nik = nik,
                viewModel = absensiViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.Profile.route
        ) {

            val viewModel = sharedProfileViewModel

            val nama by
            viewModel.nama.collectAsState()

            val nik by
            viewModel.nik.collectAsState()

            val email by
            viewModel.email.collectAsState()

            val foto by
            viewModel.foto.collectAsState()

            val fotoLastUpdated by
            viewModel.fotoLastUpdated.collectAsState()

            val scope = rememberCoroutineScope()

            ProfileScreen(

                nama = nama,
                nik = nik,
                email = email,
                foto = foto,
                fotoLastUpdated = fotoLastUpdated,
                themeMode = themeMode,
                onThemeSelected = { mode ->
                    scope.launch {
                        userPreferences.setThemeMode(mode)
                    }
                },

                onEditProfile = {
                    navController.navigate(
                        Screen.EditProfile.route
                    )
                },

                onChangePassword = {
                    navController.navigate(
                        Screen.ChangePassword.route
                    )
                },

                onLogout = {
                    scope.launch {
                        userPreferences.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }

            )

        }

        composable(Screen.ChangePassword.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(
                    AbsensiRepository(),
                    UserPreferences(LocalContext.current)
                )
            )
            val nik by dashboardViewModel.nik.collectAsState()

            ChangePasswordScreen(
                nik = nik,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.EditProfile.route
        ) {

            val viewModel = sharedProfileViewModel

            EditProfileRoute(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )

        }

        composable(
            Screen.Riwayat.route
        ) {
            val context = LocalContext.current
            val viewModel: RiwayatViewModel = viewModel(
                factory = RiwayatViewModelFactory(AbsensiRepository())
            )

            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(
                    AbsensiRepository(),
                    UserPreferences(context)
                )
            )
            val nik by dashboardViewModel.nik.collectAsState()

            RiwayatRoute(
                nik = nik,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.Kalender.route
        ) {
            val context = LocalContext.current
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(
                    AbsensiRepository(),
                    UserPreferences(context)
                )
            )
            val nik by dashboardViewModel.nik.collectAsState()

            val kalenderViewModel: KalenderViewModel = viewModel(
                factory = KalenderViewModelFactory(
                    AbsensiRepository(),
                    UserPreferences(context)
                )
            )

            KalenderScreen(
                nik = nik,
                viewModel = kalenderViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

