package com.example.mdxabsensi.ui.dashboard

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.mdxabsensi.R
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import com.example.mdxabsensi.utils.ImageUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    nama: String = "",
    nik: String = "",
    foto: String = "",
    fotoLastUpdated: Long = 0L,
    monthlyMasukCount: Int = 0,
    monthlyKeluarCount: Int = 0,
    monthlyTotalHours: Int = 0,
    absensiLoading: Boolean = false,
    absensiSuccess: Boolean = false,
    absensiError: String? = null,
    checkInTime: String? = null,
    checkOutTime: String? = null,
    todayWorkHours: String = "--:--",
    onLogout: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAbsenCameraClick: (String) -> Unit = {},
    onResetAbsensiStatus: () -> Unit = {},
    onRefreshStatus: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onKalenderClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAbsenGallerySubmit: (String, String, Double?, Double?) -> Unit = { _, _, _, _ -> },
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) }
    
    var pendingAbsenType by remember { mutableStateOf("") }
    var showMethodDialog by remember { mutableStateOf(false) }
    var selectedAbsenType by remember { mutableStateOf("") }

    var showLocationVerification by remember { mutableStateOf(false) }
    var capturedLat by remember { mutableStateOf<Double?>(null) }
    var capturedLon by remember { mutableStateOf<Double?>(null) }
    var pendingBase64 by remember { mutableStateOf("") }

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    val contentColor = if (isDark) Color.White else Color(0xFF2D2D2D)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.entries.all { it.value }
            if (!granted) {
                Toast.makeText(context, "Izin lokasi diperlukan untuk akurasi absensi.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onAbsenCameraClick(pendingAbsenType)
            } else {
                Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(permission)
            }
        }
        while (true) {
            val date = Date()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            currentDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(date)
            delay(1000)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
       val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) onRefreshStatus() }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(absensiSuccess, absensiError) {
        if (absensiSuccess) {
            Toast.makeText(context, "Absen Berhasil!", Toast.LENGTH_SHORT).show()
            onResetAbsensiStatus(); onRefreshStatus()
        }
        absensiError?.let { Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show(); onResetAbsensiStatus() }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = ImageUtils.uriToBase64(context, it)
            if (base64 != null) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            capturedLat = location?.latitude; capturedLon = location?.longitude
                            pendingBase64 = base64; showLocationVerification = true
                        }.addOnFailureListener {
                            capturedLat = null; capturedLon = null
                            pendingBase64 = base64; showLocationVerification = true
                        }
                    } catch (e: SecurityException) { capturedLat = null; pendingBase64 = base64; showLocationVerification = true }
                } else { pendingBase64 = base64; showLocationVerification = true }
            }
        }
    }

    fun handleAbsenCameraClick(type: String) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            onAbsenCameraClick(type)
        } else {
            pendingAbsenType = type
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    val isMasukCompleted = checkInTime != null
    val isKeluarCompleted = checkOutTime != null
    val nextAbsenType = when { !isMasukCompleted -> "masuk"; !isKeluarCompleted -> "keluar"; else -> null }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(Color(0xFF0D0B26), Color(0xFF1A1640), Color(0xFF2E266F)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF3E5F5), Color.White))
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        if (isDark) {
            Box(modifier = Modifier.offset(x = (-100).dp, y = 50.dp).size(300.dp).background(ElegantPurple.copy(alpha = 0.15f), CircleShape).graphicsLayer(alpha = 0.5f))
            Box(modifier = Modifier.align(Alignment.CenterEnd).offset(x = 100.dp, y = (-150).dp).size(250.dp).background(Color(0xFF4A148C).copy(alpha = 0.12f), CircleShape))
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).shadow(if (isDark) 24.dp else 8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)) else null
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        val activeColor = ElegantPurple
                        val inactiveColor = contentColor.copy(alpha = 0.4f)

                        BottomNavItem(Icons.Default.Home, "Home", true, activeColor, inactiveColor, Modifier.weight(1f)) { onRefreshStatus() }
                        BottomNavItem(Icons.Outlined.FormatListBulleted, "Riwayat", false, activeColor, inactiveColor, Modifier.weight(1f)) { onRiwayatClick() }
                        BottomNavItem(Icons.Outlined.CalendarMonth, "Kalender", false, activeColor, inactiveColor, Modifier.weight(1f)) { onKalenderClick() }
                        BottomNavItem(Icons.Outlined.ChatBubbleOutline, "Pesan", false, activeColor, inactiveColor, Modifier.weight(1f)) { onNotificationClick() }
                        BottomNavItem(Icons.Outlined.Person, "Profil", false, activeColor, inactiveColor, Modifier.weight(1f)) { onProfileClick() }
                    }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Hai, ${nama.ifEmpty { "Karyawan" }}", color = contentColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Bagaimana kabarnya hari ini...?", color = contentColor.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(contentColor.copy(alpha = 0.1f)).border(2.dp, ElegantPurple, CircleShape).clickable { onProfileClick() }, contentAlignment = Alignment.Center) {
                        if (foto.isNotEmpty()) {
                            AsyncImage(model = ImageUtils.getAbsoluteUrl(foto) + if (fotoLastUpdated > 0L) "?t=$fotoLastUpdated" else "", contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(32.dp), tint = contentColor.copy(alpha = 0.8f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().shadow(if (isDark) 12.dp else 4.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)) else null
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = currentDate, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = contentColor.copy(alpha = 0.7f))
                        Text(text = currentTime, fontSize = 64.sp, fontWeight = FontWeight.Bold, color = contentColor, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(32.dp))

                        Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                            val transition = rememberInfiniteTransition(label = "ripple")
                            val rippleScale by transition.animateFloat(initialValue = 1f, targetValue = 1.8f, animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Restart), label = "scale")
                            val rippleAlpha by transition.animateFloat(initialValue = 0.4f, targetValue = 0f, animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Restart), label = "alpha")
                            Box(modifier = Modifier.size(120.dp).graphicsLayer(scaleX = rippleScale, scaleY = rippleScale, alpha = rippleAlpha).background(ElegantPurple.copy(alpha = 0.3f), CircleShape))
                            
                            val dialEnabled = nextAbsenType != null
                            val dialModifier = Modifier.size(130.dp).shadow(if (dialEnabled) 16.dp else 4.dp, CircleShape).clip(CircleShape).background(if (dialEnabled) Brush.linearGradient(listOf(ElegantPurple, DeepIndigo)) else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))).border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape).then(if (dialEnabled) Modifier.clickable { selectedAbsenType = nextAbsenType!!; showMethodDialog = true } else Modifier)

                            Column(modifier = dialModifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Image(painter = painterResource(id = R.drawable.logoapp), contentDescription = null, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = when(nextAbsenType) { "masuk" -> "CHECK IN"; "keluar" -> "CHECK OUT"; else -> "SELESAI" }, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatusItem("In", checkInTime?.substringAfter("Jam ") ?: "--:--", Icons.Default.Login, Modifier.weight(1f), isMasukCompleted, isDark)
                            StatusItem("Out", checkOutTime?.substringAfter("Jam ") ?: "--:--", Icons.Default.Logout, Modifier.weight(1f), isKeluarCompleted, isDark)
                            StatusItem("Total", if (todayWorkHours == "--:--" || todayWorkHours.isEmpty()) "0h" else todayWorkHours.take(4), Icons.Default.Timer, Modifier.weight(1f), true, isDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Laporan Bulan Ini", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard("$monthlyMasukCount", "Masuk", Color(0xFF4CAF50), Modifier.weight(1f), isDark)
                    SummaryCard("$monthlyKeluarCount", "Keluar", Color(0xFFFF5252), Modifier.weight(1f), isDark)
                    SummaryCard("${monthlyTotalHours}h", "Jam", Color(0xFFFFC107), Modifier.weight(1f), isDark)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (showMethodDialog) {
                AlertDialog(onDismissRequest = { showMethodDialog = false }, title = { Text("Metode Absensi", fontWeight = FontWeight.Bold) }, text = { Text("Pilih metode pengambilan foto selfie:") }, confirmButton = { Button(onClick = { showMethodDialog = false; handleAbsenCameraClick(selectedAbsenType) }, colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple)) { Text("Kamera Selfie", color = Color.White) } }, dismissButton = { TextButton(onClick = { showMethodDialog = false; imagePickerLauncher.launch("image/*") }) { Text("Dari Galeri", color = ElegantPurple) } }, shape = RoundedCornerShape(24.dp))
            }

            if (showLocationVerification) {
                AlertDialog(onDismissRequest = { showLocationVerification = false }, title = { Text("Verifikasi Lokasi", fontWeight = FontWeight.Bold) }, text = { Column { Text("Lokasi terdeteksi:"); Spacer(modifier = Modifier.height(8.dp)); Box(modifier = Modifier.fillMaxWidth().background(contentColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp)).padding(12.dp)) { Text(text = if (capturedLat != null) "Kordinat: $capturedLat, $capturedLon" else "GPS tidak akurat", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = contentColor) }; Spacer(modifier = Modifier.height(8.dp)); Text("Kirim absensi dengan lokasi ini?") } }, confirmButton = { Button(onClick = { showLocationVerification = false; onAbsenGallerySubmit(selectedAbsenType, pendingBase64, capturedLat, capturedLon) }, colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple)) { Text("Ya, Kirim", color = Color.White) } }, dismissButton = { TextButton(onClick = { showLocationVerification = false }) { Text("Batal", color = Color.Red) } }, shape = RoundedCornerShape(24.dp))
            }

            if (absensiLoading) { Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ElegantPurple) } }
        }
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isActive: Boolean, activeColor: Color, inactiveColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = icon, contentDescription = label, tint = if (isActive) activeColor else inactiveColor, modifier = Modifier.size(if (isActive) 28.dp else 24.dp))
        if (isActive) { Box(modifier = Modifier.padding(top = 4.dp).size(4.dp).background(activeColor, CircleShape)) }
    }
}

@Composable
fun StatusItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, isDone: Boolean = false, isDark: Boolean = true) {
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    Column(modifier = modifier.background(if (isDone) contentColor.copy(alpha = 0.12f) else contentColor.copy(alpha = 0.05f), RoundedCornerShape(20.dp)).padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = if (isDone) ElegantPurple else contentColor.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = contentColor.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 14.sp, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SummaryCard(count: String, label: String, color: Color, modifier: Modifier = Modifier, isDark: Boolean = true) {
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.06f) else Color.White
    Card(modifier = modifier.shadow(if (isDark) 8.dp else 2.dp, RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = surfaceColor), border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)) else null) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = label, fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        }
    }
}
