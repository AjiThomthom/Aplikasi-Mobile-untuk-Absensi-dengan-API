package com.example.mdxabsensi.ui.register

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.mdxabsensi.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String) -> Unit,
    onBackLogin: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current

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
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Efek Pendaran Cahaya (Ambient Glow) di tengah atas layar
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

        // Tombol Kembali Melayang di Kiri Atas
        IconButton(
            onClick = onBackLogin,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali ke Login",
                tint = contentColor
            )
        }

        // Konten Utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            // Logo
            AndriCorpLogo(
                logoSize = 140, // Sedikit diperkecil agar pas dengan form yang panjang
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Teks Sambutan (Rata Tengah)
            Text(
                text = "Buat Akun Anda",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Lengkapi data diri di bawah ini untuk mendaftar",
                fontSize = 14.sp,
                color = subContentColor,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // KOMPONEN: Form Input dalam Kartu Kaca (Glassmorphism Card)
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
                    // NIK TextField
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

                    // Nama Lengkap TextField
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama Lengkap") },
                        placeholder = { Text("Masukkan nama lengkap") },
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

                    // Email TextField
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("Masukkan email Anda") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = ElegantPurple) },
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

                    // Password TextField
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Buat password") },
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

                    // Konfirmasi Password TextField
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password") },
                        placeholder = { Text("Ulangi password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = ElegantPurple) },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol DAFTAR
                    Button(
                        onClick = {
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Password dan Konfirmasi Password tidak cocok", Toast.LENGTH_SHORT).show()
                            } else {
                                onRegisterClick(nik, nama, email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ElegantPurple.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple)
                    ) {
                        Text(
                            text = "DAFTAR SEKARANG",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigasi ke Login
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    fontSize = 14.sp,
                    color = subContentColor,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Masuk di sini",
                    color = ElegantPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.clickable { onBackLogin() }
                )
            }

            Spacer(modifier = Modifier.height(120.dp)) // Jarak ekstra agar tidak tertutup grafik gunung
        }

        // Bottom polygon graphics (Grafik Gunung)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height

            // Triangle 1
            val path1 = Path().apply {
                moveTo(0f, height)
                lineTo(width * 0.35f, height * 0.25f)
                lineTo(width * 0.7f, height)
                close()
            }
            drawPath(path1, ElegantPurple.copy(alpha = 0.15f))

            // Triangle 2
            val path2 = Path().apply {
                moveTo(width * 0.2f, height)
                lineTo(width * 0.65f, height * 0.15f)
                lineTo(width, height)
                close()
            }
            drawPath(path2, DeepIndigo.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current

    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            messageText = it
            showErrorDialog = true
            viewModel.resetError()
        }
    }

    RegisterScreen(
        onRegisterClick = { nik, name, email, password ->
            if (nik.trim().isEmpty() || name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(context, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(nik, name, email, password)
            }
        },
        onBackLogin = {
            onRegisterSuccess() // Navigasi kembali ke form login
        }
    )

    // Dialog Error Modern
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Gagal Mendaftar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDark) Color.White else Color.Black
                )
            },
            text = {
                Text(
                    text = messageText,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK TERTUTUP", fontWeight = FontWeight.Bold, color = ElegantPurple)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDark) Color(0xFF1E1E2E) else Color.White
        )
    }

    // Dialog Sukses Modern
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onRegisterSuccess()
            },
            title = {
                Text(
                    text = "Pendaftaran Berhasil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDark) Color.White else Color.Black
                )
            },
            text = {
                Text(
                    text = "Akun Anda telah berhasil dibuat. Silakan masuk untuk melanjutkan.",
                    fontSize = 15.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onRegisterSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("MASUK", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDark) Color(0xFF1E1E2E) else Color.White
        )
    }

    // Overlay Loading Modern
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