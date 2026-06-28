package com.example.mdxabsensi.ui.riwayat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.mdxabsensi.data.model.response.RiwayatItem
import com.example.mdxabsensi.utils.ImageUtils
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import com.example.mdxabsensi.viewmodel.RiwayatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    riwayatList: List<RiwayatItem>,
    isLoading: Boolean,
    error: String?,
    onNavigateBack: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filterOptions = listOf("Semua", "Masuk", "Keluar")

    var selectedItemForDetail by remember { mutableStateOf<RiwayatItem?>(null) }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF0D0B26)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF3E5F5), Color.White))
    }

    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)

    val groupedItems = remember(riwayatList, selectedFilter) {
        val filtered = when (selectedFilter) {
            "Masuk" -> riwayatList.filter { it.type.lowercase() == "masuk" }
            "Keluar" -> riwayatList.filter { it.type.lowercase() == "keluar" }
            else -> riwayatList
        }
        filtered.groupBy { item ->
            try {
                val dateStr = if (item.timeinout.isNotEmpty() && item.timeinout != "0000-00-00 00:00:00") item.timeinout else item.timestamp
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateStr)
                SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(date!!)
            } catch (e: Exception) { "Lainnya" }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Riwayat Absensi", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = contentColor) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = contentColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    filterOptions.forEach { option ->
                        val isSelected = selectedFilter == option
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = option },
                            label = { Text(option, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElegantPurple,
                                selectedLabelColor = Color.White,
                                containerColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                labelColor = contentColor.copy(alpha = 0.7f)
                            ),
                            border = BorderStroke(1.dp, if (isSelected) ElegantPurple else contentColor.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ElegantPurple) }
                } else if (error != null) {
                    EmptyStateView("Gagal memuat data:\n$error", true)
                } else if (riwayatList.isEmpty() || groupedItems.isEmpty()) {
                    EmptyStateView("Belum ada riwayat absensi", false)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        groupedItems.forEach { (monthHeader, items) ->
                            item { Text(monthHeader.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = contentColor.copy(alpha = 0.4f), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp), letterSpacing = 1.sp) }
                            items(items, key = { it.timeinout + it.type + it.timestamp }) { item ->
                                RiwayatCardModern(
                                    item = item,
                                    onClick = { selectedItemForDetail = item }
                                )
                            }
                        }
                    }
                }
            }
        }

        selectedItemForDetail?.let { item ->
            DetailAbsensiDialog(
                item = item,
                onDismiss = { selectedItemForDetail = null }
            )
        }
    }
}

@Composable
fun RiwayatCardModern(item: RiwayatItem, onClick: () -> Unit) {
    val context = LocalContext.current
    // PERBAIKAN 1: Menggunakan LocalIsDarkTheme agar selalu sinkron dengan tema aplikasi
    val isDark = LocalIsDarkTheme.current
    val isMasuk = item.type.lowercase() == "masuk"
    val badgeColor = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFFF5252)
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White

    val dateToParse = if (item.timeinout.isNotEmpty() && item.timeinout != "0000-00-00 00:00:00") item.timeinout else item.timestamp
    var dayNum = "--"; var monthName = "---"; var timeStr = "--:--"
    try {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateToParse)
        if (date != null) {
            dayNum = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            monthName = SimpleDateFormat("MMM", Locale("id", "ID")).format(date)
            timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) { e.printStackTrace() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isDark) 8.dp else 2.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).background(contentColor.copy(alpha = 0.05f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(dayNum, fontSize = 22.sp, fontWeight = FontWeight.Black, color = contentColor)
                        Text(monthName.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor.copy(alpha = 0.6f)) // Alpha diubah dari 0.5f ke 0.6f
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(badgeColor, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isMasuk) "Check In" else "Check Out", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                    Text("$timeStr WIB", fontSize = 13.sp, color = contentColor.copy(alpha = 0.7f)) // Alpha diubah agar lebih kontras
                    val loc = if (item.latitude != null) "Kordinat: ${item.latitude}, ${item.longitude}" else if (item.timestamp.contains(".")) "Lokasi: ${item.timestamp}" else "Lokasi: Kantor Pusat"
                    Text(loc, fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) // PERBAIKAN 2: Alpha diubah dari 0.4f menjadi 0.6f
                }
                Box(modifier = Modifier.size(54.dp).clip(RoundedCornerShape(12.dp)).background(contentColor.copy(alpha = 0.05f)).border(1.dp, contentColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))) {
                    if (item.fotoselfie.isNotEmpty()) AsyncImage(model = ImageUtils.getAbsoluteUrl(item.fotoselfie), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Icon(Icons.Default.Image, null, tint = contentColor.copy(alpha = 0.3f), modifier = Modifier.align(Alignment.Center))
                }
            }
            if (item.latitude != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}(Lokasi Absen)")).apply { setPackage("com.google.android.apps.maps") }) }, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple.copy(alpha = 0.1f), contentColor = ElegantPurple), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(8.dp)); Text("Cek di Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailAbsensiDialog(item: RiwayatItem, onDismiss: () -> Unit) {
    // PERBAIKAN 3: Menggunakan LocalIsDarkTheme
    val isDark = LocalIsDarkTheme.current
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                val (title, image, info, closeBtn) = createRefs()

                Text(
                    text = "Detail Absensi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(contentColor.copy(alpha = 0.05f))
                        .constrainAs(image) {
                            top.linkTo(title.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    if (item.fotoselfie.isNotEmpty()) {
                        AsyncImage(
                            model = ImageUtils.getAbsoluteUrl(item.fotoselfie),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Image,
                            null,
                            tint = contentColor.copy(alpha = 0.2f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .constrainAs(info) {
                            top.linkTo(image.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tipe: ${item.type.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = "Waktu: ${item.timeinout.ifEmpty { item.timestamp }}",
                        color = contentColor.copy(alpha = 0.8f) // Alpha dinaikkan
                    )
                    val loc = if (item.latitude != null) {
                        "Lokasi: ${item.latitude}, ${item.longitude}"
                    } else {
                        "Lokasi: Kantor Pusat"
                    }
                    Text(
                        text = loc,
                        color = contentColor.copy(alpha = 0.8f), // Alpha dinaikkan
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .constrainAs(closeBtn) {
                            top.linkTo(info.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Tutup", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String, isError: Boolean) {
    // PERBAIKAN 4: Menggunakan LocalIsDarkTheme
    val isDark = LocalIsDarkTheme.current
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = contentColor.copy(alpha = 0.2f)) // Transparansi disesuaikan
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = if (isError) Color(0xFFFF5252) else contentColor.copy(alpha = 0.6f), textAlign = TextAlign.Center, fontSize = 15.sp) // Alpha dinaikkan untuk keterbacaan
    }
}

@Composable
fun RiwayatRoute(nik: String, viewModel: RiwayatViewModel, onNavigateBack: () -> Unit) {
    val riwayatList by viewModel.riwayatList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    LaunchedEffect(nik) { if (nik.isNotEmpty()) viewModel.fetchRiwayat(nik) }
    RiwayatScreen(riwayatList, isLoading, error, onNavigateBack)
}