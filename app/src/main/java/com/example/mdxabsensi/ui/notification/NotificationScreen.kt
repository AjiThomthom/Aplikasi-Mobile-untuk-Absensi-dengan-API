package com.example.mdxabsensi.ui.notification

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mdxabsensi.data.model.response.RiwayatItem
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notifications: List<RiwayatItem>
) {
    val isDark = LocalIsDarkTheme.current
    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF0D0B26)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF3E5F5), Color.White))
    }
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)

    // State untuk menyimpan notifikasi yang sedang diklik (jika null, dialog tertutup)
    var selectedNotification by remember { mutableStateOf<RiwayatItem?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Notifikasi", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = contentColor) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Notifications, null, modifier = Modifier.size(80.dp), tint = contentColor.copy(alpha = 0.2f)) // Transparansi disesuaikan
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Belum ada notifikasi baru", color = contentColor.copy(alpha = 0.6f), fontWeight = FontWeight.Bold) // Transparansi disesuaikan
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(notifications.sortedByDescending { it.timeinout.ifEmpty { it.timestamp } }) { item ->
                        NotificationCardModern(
                            item = item,
                            onClick = {
                                // Membuka popup dengan mengisi state ini
                                selectedNotification = item
                            }
                        )
                    }
                }
            }
        }

        // Memunculkan Dialog jika ada notifikasi yang dipilih
        selectedNotification?.let { item ->
            NotificationDetailDialog(
                item = item,
                onDismiss = { selectedNotification = null }
            )
        }
    }
}

@Composable
fun NotificationCardModern(item: RiwayatItem, onClick: () -> Unit) {
    // PERBAIKAN: Gunakan LocalIsDarkTheme agar selalu sinkron dengan tema dari Pengaturan
    val isDark = LocalIsDarkTheme.current
    val isMasuk = item.type.lowercase() == "masuk"
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White

    // --- Logika Animasi Skala (Bounce Effect) ---
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, // Mengecil menjadi 95% saat ditekan
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce_animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(if (isDark) 8.dp else 2.dp, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Menghilangkan efek ripple bawaan agar animasi bounce lebih menonjol
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)) else null
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(if (isMasuk) Color(0xFF4CAF50).copy(alpha = 0.15f) else ElegantPurple.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (isMasuk) Icons.Default.CheckCircle else Icons.Default.Info, contentDescription = null, tint = if (isMasuk) Color(0xFF4CAF50) else ElegantPurple, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Absensi ${item.type.replaceFirstChar { it.uppercase() }} Berhasil", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = contentColor)
                Text(
                    text = "Waktu: ${formatTimeModern(if (item.timeinout.isNotEmpty() && item.timeinout != "0000-00-00 00:00:00") item.timeinout else item.timestamp)}",
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.7f) // PERBAIKAN: Alpha dinaikkan agar jelas di Mode Terang
                )
            }
        }
    }
}

// KOMPONEN BARU: Desain Popup untuk Detail Notifikasi
@Composable
fun NotificationDetailDialog(item: RiwayatItem, onDismiss: () -> Unit) {
    val isDark = LocalIsDarkTheme.current
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val surfaceColor = if (isDark) Color(0xFF1E1E2E) else Color.White
    val isMasuk = item.type.lowercase() == "masuk"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ikon Utama Popup
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            if (isMasuk) Color(0xFF4CAF50).copy(alpha = 0.15f) else ElegantPurple.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMasuk) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isMasuk) Color(0xFF4CAF50) else ElegantPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Detail Notifikasi",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Data absensi ${item.type.lowercase()} Anda telah berhasil direkam oleh sistem dengan baik.",
                    fontSize = 14.sp,
                    color = contentColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Kartu Info Kecil di dalam Popup
                Card(
                    colors = CardDefaults.cardColors(containerColor = contentColor.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Waktu Tercatat:", fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimeModern(if (item.timeinout.isNotEmpty() && item.timeinout != "0000-00-00 00:00:00") item.timeinout else item.timestamp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = contentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

fun formatTimeModern(timeStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = parser.parse(timeStr)
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        formatter.format(date!!)
    } catch (e: Exception) { timeStr }
}