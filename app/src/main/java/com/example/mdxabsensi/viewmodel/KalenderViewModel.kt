package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.data.model.response.RiwayatItem
import com.example.mdxabsensi.repository.AbsensiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class KalenderViewModel(
    private val absensiRepository: AbsensiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _nik = MutableStateFlow("")
    val nik: StateFlow<String> = _nik

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: StateFlow<Int> = _currentYear

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH)) // 0-indexed
    val currentMonth: StateFlow<Int> = _currentMonth

    private val _allLogs = MutableStateFlow<List<RiwayatItem>>(emptyList())
    
    // Group logs by date string "yyyy-MM-dd" for efficient lookup
    val logsByDate: StateFlow<Map<String, List<RiwayatItem>>> = _allLogs
        .map { list ->
            list.groupBy { it.timeinout.substringBefore(" ") }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    private val _selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate

    val selectedDateLogs: StateFlow<List<RiwayatItem>> = combine(selectedDate, logsByDate) { date, logsMap ->
        logsMap[date] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            userPreferences.nik.collect { userNik ->
                _nik.value = userNik
                if (userNik.isNotEmpty()) {
                    fetchRiwayatLogs(userNik)
                }
            }
        }
    }

    fun fetchRiwayatLogs(nik: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = absensiRepository.getRiwayat(nik)
                if (response.success) {
                    _allLogs.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun nextMonth() {
        if (_currentMonth.value == 11) {
            _currentMonth.value = 0
            _currentYear.value += 1
        } else {
            _currentMonth.value += 1
        }
    }

    fun prevMonth() {
        if (_currentMonth.value == 0) {
            _currentMonth.value = 11
            _currentYear.value -= 1
        } else {
            _currentMonth.value -= 1
        }
    }

    fun selectDate(dateStr: String) {
        _selectedDate.value = dateStr
    }
}
