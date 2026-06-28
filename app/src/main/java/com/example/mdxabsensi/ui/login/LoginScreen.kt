package com.example.mdxabsensi.ui.login

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdxabsensi.ui.components.AndriCorpLogo
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import com.example.mdxabsensi.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    themeMode: String,
    onThemeToggle: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onNavigateRegister: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val context = LocalContext.current

    // Penyesuaian warna sesuai tema
    val contentColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subContentColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
    val inputContainerColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color(0xFFF5F6F8)

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF090814)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF8F0FA), Color(0xFFFDFDFD)))
    }

    var nik by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // LAPISAN 1: Efek Pendaran Cahaya (Paling Belakang)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(500.dp)
                .offset(y = (-100).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElegantPurple.copy(alpha = if (isDark) 0.15f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // LAPISAN 2: Bottom polygon graphics / Grafik Gunung (Background)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height

            val path1 = Path().apply {
                moveTo(0f, height)
                lineTo(width * 0.35f, height * 0.25f)
                lineTo(width * 0.7f, height)
                close()
            }
            drawPath(path1, ElegantPurple.copy(alpha = 0.15f))

            val path2 = Path().apply {
                moveTo(width * 0.2f, height)
                lineTo(width * 0.65f, height * 0.15f)
                lineTo(width, height)
                close()
            }
            drawPath(path2, DeepIndigo.copy(alpha = 0.2f))
        }

        // LAPISAN 3: Konten Utama Form Login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Logo
            AndriCorpLogo(
                logoSize = 150,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Teks Sambutan
            Text(
                text = "Selamat Datang!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Silakan masuk ke akun Anda untuk melanjutkan",
                fontSize = 14.sp,
                color = subContentColor,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isDark) 16.dp else 8.dp, RoundedCornerShape(32.dp), spotColor = ElegantPurple.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nik,
                        onValueChange = { nik = it },
                        label = { Text("NIK") },
                        placeholder = { Text("Masukkan NIK Anda") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = ElegantPurple) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantPurple, unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = ElegantPurple, unfocusedLabelColor = subContentColor,
                            focusedTextColor = contentColor, unfocusedTextColor = contentColor,
                            focusedContainerColor = inputContainerColor, unfocusedContainerColor = inputContainerColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = ElegantPurple) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null, tint = subContentColor
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantPurple, unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = ElegantPurple, unfocusedLabelColor = subContentColor,
                            focusedTextColor = contentColor, unfocusedTextColor = contentColor,
                            focusedContainerColor = inputContainerColor, unfocusedContainerColor = inputContainerColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Lupa Password?",
                            color = ElegantPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    try {
                                        val url = "https://wa.me/6283153610195?text=${Uri.encode("Halo, saya lupa password akun absensi saya dan membutuhkan bantuan untuk meresetnya.")}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal membuka WhatsApp. Pastikan aplikasi terinstal.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLoginClick(nik, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ElegantPurple.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple)
                    ) {
                        Text(
                            text = "MASUK",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Belum punya akun? ",
                    fontSize = 14.sp,
                    color = subContentColor,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Daftar di sini",
                    color = ElegantPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onNavigateRegister() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // LAPISAN 4: Tombol Tema (Paling Depan agar tidak tertutup dan bisa diklik)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 24.dp)
                .size(48.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(surfaceColor)
                .border(
                    BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)),
                    CircleShape
                )
                .clickable { onThemeToggle() },
            contentAlignment = Alignment.Center
        ) {
            val themeIcon = when (themeMode) {
                "light" -> Icons.Default.LightMode
                "dark" -> Icons.Default.DarkMode
                else -> Icons.Default.Brightness6
            }
            Icon(
                imageVector = themeIcon,
                contentDescription = "Ganti Tema",
                tint = ElegantPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    themeMode: String,
    onThemeToggle: () -> Unit,
    onNavigateDashboard: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateDashboard()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            errorMessage = it
            showErrorDialog = true
            viewModel.resetError()
        }
    }

    LoginScreen(
        themeMode = themeMode,
        onThemeToggle = onThemeToggle,
        onLoginClick = { nik, password ->
            if (nik.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(context, "NIK dan Password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(nik, password)
            }
        },
        onNavigateRegister = onNavigateRegister
    )

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Gagal Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDark) Color.White else Color.Black
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = ElegantPurple)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDark) Color(0xFF1E1E2E) else Color.White
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White)
            ) {
                Box(modifier = Modifier.padding(32.dp)) {
                    CircularProgressIndicator(color = ElegantPurple)
                }
            }
        }
    }
}