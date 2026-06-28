package com.example.mdxabsensi.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mdxabsensi.utils.ImageUtils
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme

@Composable
fun ProfileScreen(
    nama: String,
    nik: String,
    email: String,
    foto: String = "",
    fotoLastUpdated: Long = 0L,
    themeMode: String = "system",
    onThemeSelected: (String) -> Unit = {},
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF090814)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF8F0FA), Color(0xFFFDFDFD)))
    }

    val contentColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subContentColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Dekorasi Latar Belakang (Glow)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ElegantPurple.copy(alpha = if (isDark) 0.15f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Profil Saya",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = contentColor,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // User Info Glass Card
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                        .shadow(if (isDark) 16.dp else 8.dp, RoundedCornerShape(32.dp), spotColor = ElegantPurple.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = nama.ifEmpty { "Nama Pengguna" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = nik.ifEmpty { "NIK tidak tersedia" },
                            fontSize = 15.sp,
                            color = subContentColor,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Perubahan 1: Teks menjadi "Status: Aktif"
                        Surface(
                            color = ElegantPurple.copy(alpha = if (isDark) 0.2f else 0.1f),
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(1.dp, ElegantPurple.copy(alpha = 0.3f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF4CAF50), CircleShape) // Tanda Hijau
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Status: Aktif",
                                    color = if (isDark) Color.White else ElegantPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(12.dp, CircleShape, spotColor = ElegantPurple.copy(alpha = 0.3f))
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF1E1E2E) else Color.White)
                        .border(4.dp, surfaceColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (foto.isNotEmpty()) {
                        AsyncImage(
                            model = ImageUtils.getAbsoluteUrl(foto) + if (fotoLastUpdated > 0L) "?t=$fotoLastUpdated" else "",
                            contentDescription = "Foto Profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = subContentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Perubahan 2: Menu List (Logout disatukan ke sini)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isDark) 8.dp else 4.dp, RoundedCornerShape(28.dp), spotColor = ElegantPurple.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
            ) {
                Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)) {
                    ProfileMenuRowModern(Icons.Default.Person, "Data Pribadi", contentColor, ElegantPurple, onEditProfile)
                    ProfileMenuRowModern(Icons.Default.Lock, "Ubah Password", contentColor, ElegantPurple, onChangePassword)
                    ProfileMenuRowModern(Icons.Default.Settings, "Pengaturan Tema", contentColor, ElegantPurple, { showSettingsDialog = true })
                    ProfileMenuRowModern(Icons.Default.HelpOutline, "Pusat Bantuan", contentColor, ElegantPurple, {
                        val url = "https://wa.me/6283153610195?text=${Uri.encode("Halo, saya membutuhkan bantuan untuk aplikasi absensi")}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    })

                    // Garis Pemisah Tipis sebelum Logout
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                        color = contentColor.copy(alpha = 0.05f)
                    )

                    // Tombol Keluar dari Akun (disamakan dengan menu tapi berwarna merah)
                    val redAccent = Color(0xFFFF4B4B)
                    ProfileMenuRowModern(Icons.AutoMirrored.Filled.ExitToApp, "Keluar dari Akun", redAccent, redAccent, onLogout)
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Ruang untuk Bottom Nav
        }
    }

    // Perubahan 3: Dialog Pengaturan Tema Kustom (Lebih Ramah Mata)
    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E2E) else Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Pilih Tema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = contentColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val options = listOf(
                        "system" to "Sistem Default",
                        "light" to "Mode Terang",
                        "dark" to "Mode Gelap"
                    )

                    options.forEach { (mode, label) ->
                        val isSelected = themeMode == mode
                        val bgColor = if (isSelected) ElegantPurple.copy(alpha = 0.1f) else Color.Transparent
                        val textColor = if (isSelected) ElegantPurple else contentColor

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .clickable {
                                    onThemeSelected(mode)
                                    showSettingsDialog = false // Otomatis menutup saat dipilih
                                }
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Terpilih",
                                    tint = ElegantPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showSettingsDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("BATAL", color = subContentColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRowModern(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    contentColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(18.dp))

        Text(
            title,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.3f), // Tetap redup agar tidak mendominasi
            modifier = Modifier.size(20.dp)
        )
    }
}