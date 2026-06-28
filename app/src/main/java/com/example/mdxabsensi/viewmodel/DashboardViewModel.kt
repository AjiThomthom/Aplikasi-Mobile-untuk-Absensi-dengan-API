package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.AbsensiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val absensiRepository: AbsensiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _nama = MutableStateFlow("")
    val nama: StateFlow<String> = _nama

    private val _nik = MutableStateFlow("")
    val nik: StateFlow<String> = _nik

    private val _foto = MutableStateFlow("")
    val foto: StateFlow<String> = _foto

    private val _fotoLastUpdated = MutableStateFlow(0L)
    val fotoLastUpdated: StateFlow<Long> = _fotoLastUpdated

    private val _checkInTime = MutableStateFlow<String?>(null)
    val checkInTime: StateFlow<String?> = _checkInTime

    private val _checkOutTime = MutableStateFlow<String?>(null)
    val checkOutTime: StateFlow<String?> = _checkOutTime

    private val _todayWorkHours = MutableStateFlow("--:--")
    val todayWorkHours: StateFlow<String> = _todayWorkHours

    private val _monthlyMasukCount = MutableStateFlow(0)
    val monthlyMasukCount: StateFlow<Int> = _monthlyMasukCount

    private val _monthlyKeluarCount = MutableStateFlow(0)
    val monthlyKeluarCount: StateFlow<Int> = _monthlyKeluarCount

    private val _monthlyTotalHours = MutableStateFlow(0)
    val monthlyTotalHours: StateFlow<Int> = _monthlyTotalHours

    init {
        viewModelScope.launch {
            userPreferences.nama.collect {
                _nama.value = it
            }
        }

        viewModelScope.launch {
            userPreferences.nik.collect {
                _nik.value = it
                if (it.isNotEmpty()) {
                    fetchTodayAbsensiStatus()
                }
            }
        }

        viewModelScope.launch {
            userPreferences.foto.collect {
                _foto.value = it
            }
        }

        viewModelScope.launch {
            userPreferences.fotoLastUpdated.collect {
                _fotoLastUpdated.value = it
            }
        }
    }

    fun fetchTodayAbsensiStatus() {
        val currentNik = _nik.value
        if (currentNik.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = absensiRepository.getRiwayat(currentNik)
                if (response.success) {
                    val list = response.data
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val currentMonthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

                    // Filter today's history logs
                    val todayLogs = list.filter { 
                        it.timeinout.startsWith(todayStr) || it.timestamp.startsWith(todayStr) 
                    }

                    // Find latest "masuk" check-in today
                    val checkInLog = todayLogs.filter { it.type.lowercase() == "masuk" }.maxByOrNull { it.timeinout }
                    // Find latest "keluar" check-out today
                    val checkOutLog = todayLogs.filter { it.type.lowercase() == "keluar" }.maxByOrNull { it.timeinout }

                    // Format dates to friendly representation: e.g. "dd-MM-yyyy Jam HH:mm"
                    _checkInTime.value = checkInLog?.let { formatLogTime(it.timeinout) }
                    _checkOutTime.value = checkOutLog?.let { formatLogTime(it.timeinout) }

                    if (checkInLog != null && checkOutLog != null) {
                        try {
                            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val inDate = formatter.parse(checkInLog.timeinout)
                            val outDate = formatter.parse(checkOutLog.timeinout)
                            if (inDate != null && outDate != null) {
                                val diff = outDate.time - inDate.time
                                if (diff > 0) {
                                    val diffMinutes = diff / (1000 * 60)
                                    val hours = diffMinutes / 60
                                    val minutes = diffMinutes % 60
                                    _todayWorkHours.value = if (minutes > 0) {
                                        "$hours Jam $minutes Menit"
                                    } else {
                                        "$hours Jam"
                                    }
                                } else {
                                    _todayWorkHours.value = "0 Jam"
                                }
                            } else {
                                _todayWorkHours.value = "--:--"
                            }
                        } catch (e: Exception) {
                            _todayWorkHours.value = "--:--"
                        }
                    } else {
                        _todayWorkHours.value = "--:--"
                    }

                    // Calculate monthly stats dynamically
                    val monthlyLogs = list.filter { it.timeinout.startsWith(currentMonthStr) }
                    val monthlyMasuk = monthlyLogs.filter { it.type.lowercase() == "masuk" }
                    val monthlyKeluar = monthlyLogs.filter { it.type.lowercase() == "keluar" }

                    _monthlyMasukCount.value = monthlyMasuk.size
                    _monthlyKeluarCount.value = monthlyKeluar.size

                    // Calculate total minutes worked
                    var totalMinutes = 0
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dayGroups = monthlyLogs.groupBy { it.timeinout.substring(0, 10) }
                    for ((_, dayLogs) in dayGroups) {
                        val inLog = dayLogs.filter { it.type.lowercase() == "masuk" }.minByOrNull { it.timeinout }
                        val outLog = dayLogs.filter { it.type.lowercase() == "keluar" }.maxByOrNull { it.timeinout }
                        if (inLog != null && outLog != null) {
                            try {
                                val inDate = formatter.parse(inLog.timeinout)
                                val outDate = formatter.parse(outLog.timeinout)
                                if (inDate != null && outDate != null) {
                                    val diff = outDate.time - inDate.time
                                    if (diff > 0) {
                                        totalMinutes += (diff / (1000 * 60)).toInt()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    _monthlyTotalHours.value = totalMinutes / 60
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun formatLogTime(dateTimeStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateTimeStr) ?: return dateTimeStr
            
            // Format output including Date, Month, Year, and Time
            val formatter = SimpleDateFormat("dd-MM-yyyy 'Jam' HH:mm", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            dateTimeStr
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }
}