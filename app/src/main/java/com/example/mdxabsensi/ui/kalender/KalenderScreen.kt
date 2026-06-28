package com.example.mdxabsensi.ui.kalender

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mdxabsensi.utils.ImageUtils
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import com.example.mdxabsensi.viewmodel.KalenderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalenderScreen(
    nik: String,
    viewModel: KalenderViewModel,
    onNavigateBack: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    
    val currentYear by viewModel.currentYear.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val logsByDate by viewModel.logsByDate.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedDateLogs by viewModel.selectedDateLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val tepatWaktuColor = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
    val terlambatColor = if (isDark) Color(0xFFFFB74D) else Color(0xFFEF6C00)
    val alpaColor = if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
    
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF1C1B1F)
    val subContentColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF1C1B1F).copy(alpha = 0.6f)

    var showDialogImage by remember { mutableStateOf<String?>(null) }

    val monthNamesId = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Dynamic Background Gradient
    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF0D0B26)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF3E5F5), Color.White))
    }

    val calendar = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val startOffset = when (firstDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    val totalCells = startOffset + totalDays
    val totalRows = (totalCells + 6) / 7

    val formattedSelectedDateTitle = remember(selectedDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(selectedDate) ?: Date()
            val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            formatter.format(date)
        } catch (e: Exception) {
            selectedDate
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Kalender Kerja",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = contentColor
                        )
                    },
                    navigationIcon = {},
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Calendar Main Glass Card
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(if (isDark) 12.dp else 4.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)) else null
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Month & Year Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.prevMonth() }) {
                                Icon(Icons.Default.ChevronLeft, null, tint = ElegantPurple, modifier = Modifier.size(28.dp))
                            }
                            Text(
                                text = "${monthNamesId[currentMonth]} $currentYear",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                fontFamily = FontFamily.SansSerif
                            )
                            IconButton(onClick = { viewModel.nextMonth() }) {
                                Icon(Icons.Default.ChevronRight, null, tint = ElegantPurple, modifier = Modifier.size(28.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weekdays Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = subContentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = subContentColor.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Days Grid
                        for (r in 0 until totalRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0 until 7) {
                                    val cellIndex = r * 7 + c
                                    val dayNumber = cellIndex - startOffset + 1
                                    
                                    Box(
                                        modifier = Modifier.weight(1f).aspectRatio(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (dayNumber in 1..totalDays) {
                                            val dateStr = String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonth + 1, dayNumber)
                                            val dayLogs = logsByDate[dateStr] ?: emptyList()
                                            
                                            val currentReal = Calendar.getInstance()
                                            val cellCalendar = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, currentYear)
                                                set(Calendar.MONTH, currentMonth)
                                                set(Calendar.DAY_OF_MONTH, dayNumber)
                                            }
                                            
                                            val isToday = cellCalendar.get(Calendar.YEAR) == currentReal.get(Calendar.YEAR) &&
                                                          cellCalendar.get(Calendar.DAY_OF_YEAR) == currentReal.get(Calendar.DAY_OF_YEAR)
                                            
                                            val isPast = cellCalendar.before(currentReal) && !isToday
                                            val isWeekend = cellCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                                                            cellCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

                                            val masukLog = dayLogs.firstOrNull { it.type.lowercase() == "masuk" }
                                            
                                            val dotColor = when {
                                                masukLog != null -> {
                                                    val checkInTimeStr = masukLog.timeinout.substringAfter(" ")
                                                    val isLate = try {
                                                        val parser = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                                        val time = parser.parse(checkInTimeStr)
                                                        val limit = parser.parse("09:00:00")
                                                        time != null && time.after(limit)
                                                    } catch (e: Exception) { false }
                                                    if (isLate) terlambatColor else tepatWaktuColor
                                                }
                                                isPast && !isWeekend -> alpaColor
                                                else -> null
                                            }

                                            val isSelected = selectedDate == dateStr
                                            
                                            Column(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) ElegantPurple else Color.Transparent)
                                                    .border(1.dp, if (isToday) ElegantPurple.copy(alpha = 0.5f) else Color.Transparent, CircleShape)
                                                    .clickable { viewModel.selectDate(dateStr) },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else if (isWeekend) contentColor.copy(alpha = 0.3f) else contentColor
                                                )
                                                if (dotColor != null) {
                                                    Box(modifier = Modifier.size(4.dp).background(dotColor, CircleShape))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Content Details
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp).shadow(if (isDark) 8.dp else 2.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)) else null
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = formattedSelectedDateTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = contentColor
                        )

                        val masukLog = selectedDateLogs.firstOrNull { it.type.lowercase() == "masuk" }
                        val keluarLog = selectedDateLogs.firstOrNull { it.type.lowercase() == "keluar" }

                        ModernDetailRow("Check In", masukLog?.timeinout, masukLog?.fotoselfie, tepatWaktuColor, contentColor, subContentColor, { showDialogImage = it })
                        HorizontalDivider(color = subContentColor.copy(alpha = 0.1f))
                        ModernDetailRow("Check Out", keluarLog?.timeinout, keluarLog?.fotoselfie, alpaColor, contentColor, subContentColor, { showDialogImage = it })
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElegantPurple)
                }
            }
        }
    }

    showDialogImage?.let { photo ->
        Dialog(onDismissRequest = { showDialogImage = null }) {
            Box(modifier = Modifier.padding(24.dp).clip(RoundedCornerShape(24.dp))) {
                AsyncImage(model = ImageUtils.getAbsoluteUrl(photo), contentDescription = null, modifier = Modifier.fillMaxWidth().aspectRatio(1f), contentScale = ContentScale.Crop)
            }
        }
    }
}

@Composable
fun ModernDetailRow(label: String, time: String?, photo: String?, color: Color, contentColor: Color, subContentColor: Color, onPhotoClick: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(8.dp).background(if (time != null) color else subContentColor.copy(alpha = 0.2f), CircleShape))
            Column {
                Text(label, fontSize = 11.sp, color = subContentColor)
                Text(time?.substringAfter(" ") ?: "--:--", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = contentColor)
            }
        }
        if (photo != null) {
            AsyncImage(
                model = ImageUtils.getAbsoluteUrl(photo), contentDescription = null,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).clickable { onPhotoClick(photo) },
                contentScale = ContentScale.Crop
            )
        }
    }
}
