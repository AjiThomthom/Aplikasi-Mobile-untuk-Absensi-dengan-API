package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.data.model.request.AbsensiRequest
import com.example.mdxabsensi.repository.AbsensiRepository
import com.example.mdxabsensi.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AbsensiViewModel(
    private val repository: AbsensiRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun absensi(
        nik: String,
        type: String,
        fotoBase64: String,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isSuccess.value = false
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val timeinout = sdf.format(Date())

                val request = AbsensiRequest(
                    nik = nik,
                    type = type,
                    timeinout = timeinout,
                    foto_base64 = fotoBase64,
                    latitude = latitude,
                    longitude = longitude
                )

                val response = repository.absensi(request)
                if (response.success) {
                    _isSuccess.value = true
                    notificationHelper.showNotification(
                        title = "Absensi Berhasil",
                        message = "Anda telah berhasil melakukan absen $type"
                    )
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Koneksi internet bermasalah"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetStatus() {
        _isSuccess.value = false
        _error.value = null
    }
}
