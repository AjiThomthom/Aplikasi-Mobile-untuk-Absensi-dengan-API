package com.example.mdxabsensi.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdxabsensi.data.model.request.ChangePasswordRequest
import com.example.mdxabsensi.repository.AuthRepository
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    nik: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    val isDark = LocalIsDarkTheme.current

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Warna yang menyesuaikan tema
    val contentColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subContentColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
    val inputContainerColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color(0xFFF5F6F8)

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF090814)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF8F0FA), Color(0xFFFDFDFD)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent, // Trik Seamless Background
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Ubah Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = contentColor,
                        navigationIconContentColor = contentColor
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pastikan password baru Anda aman dan mudah diingat.",
                    fontSize = 14.sp,
                    color = subContentColor,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Kartu Latar untuk Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isDark) 8.dp else 4.dp, RoundedCornerShape(28.dp), spotColor = ElegantPurple.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Input Password Lama
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Password Lama") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ElegantPurple)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                    Icon(
                                        imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = subContentColor
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantPurple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = ElegantPurple,
                                unfocusedLabelColor = subContentColor,
                                focusedTextColor = contentColor,
                                unfocusedTextColor = contentColor,
                                focusedContainerColor = inputContainerColor,
                                unfocusedContainerColor = inputContainerColor
                            )
                        )

                        // Input Password Baru
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Password Baru") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ElegantPurple)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = subContentColor
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantPurple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = ElegantPurple,
                                unfocusedLabelColor = subContentColor,
                                focusedTextColor = contentColor,
                                unfocusedTextColor = contentColor,
                                focusedContainerColor = inputContainerColor,
                                unfocusedContainerColor = inputContainerColor
                            )
                        )

                        // Input Konfirmasi Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Konfirmasi Password Baru") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ElegantPurple)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = subContentColor
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantPurple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = ElegantPurple,
                                unfocusedLabelColor = subContentColor,
                                focusedTextColor = contentColor,
                                unfocusedTextColor = contentColor,
                                focusedContainerColor = inputContainerColor,
                                unfocusedContainerColor = inputContainerColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Tombol Simpan
                Button(
                    onClick = {
                        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            try {
                                val request = ChangePasswordRequest(nik, oldPassword, newPassword)
                                val response = repository.changePassword(request)
                                if (response.success) {
                                    Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                } else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ElegantPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple),
                    enabled = !isLoading
                ) {
                    Text(
                        "Simpan Perubahan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Overlay Loading seperti pada Edit Profil
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor)
                ) {
                    Box(modifier = Modifier.padding(32.dp)) {
                        CircularProgressIndicator(color = ElegantPurple)
                    }
                }
            }
        }
    }
}