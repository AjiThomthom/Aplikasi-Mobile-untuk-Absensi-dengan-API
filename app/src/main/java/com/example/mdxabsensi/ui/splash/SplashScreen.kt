package com.example.mdxabsensi.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdxabsensi.ui.components.AppBackground
import com.example.mdxabsensi.ui.components.AndriCorpLogo
import com.example.mdxabsensi.ui.theme.SkyBlue
import com.example.mdxabsensi.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateLogin: () -> Unit,
    onNavigateDashboard: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        delay(2000) // 2.5 seconds delay for a premium feel
        if (isLoggedIn) {
            onNavigateDashboard()
        } else {
            onNavigateLogin()
        }
    }

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Centered Logo and Brand Name
            AndriCorpLogo(
                logoSize = 200,
                modifier = Modifier.align(Alignment.Center)
            )

            // Slogan and progress bar at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Smart Solutions",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "For Better Future",
                        color = SkyBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                }

                LinearProgressIndicator(
                    color = SkyBlue,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                )
            }
        }
    }
}