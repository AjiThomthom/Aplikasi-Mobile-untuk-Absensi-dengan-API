package com.example.mdxabsensi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.data.model.request.UpdateUserRequest
import com.example.mdxabsensi.datastore.UserPreferences
import com.example.mdxabsensi.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _nama = MutableStateFlow("")
    val nama: StateFlow<String> = _nama

    private val _nik = MutableStateFlow("")
    val nik: StateFlow<String> = _nik

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _foto = MutableStateFlow("")
    val foto: StateFlow<String> = _foto

    private val _fotoLastUpdated = MutableStateFlow(0L)
    val fotoLastUpdated: StateFlow<Long> = _fotoLastUpdated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean> = _isUpdateSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            userPreferences.nama.collect {
                _nama.value = it
            }
        }

        viewModelScope.launch {
            userPreferences.nik.collect {
                _nik.value = it
            }
        }

        viewModelScope.launch {
            userPreferences.email.collect {
                _email.value = it
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

    fun updateProfile(namaBaru: String, emailBaru: String, fotoBase64: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = userRepository.updateUser(
                    UpdateUserRequest(
                        nik = _nik.value,
                        nama = namaBaru,
                        email = emailBaru,
                        foto_base64 = fotoBase64
                    )
                )

                if (response.success && response.data != null) {
                    val serverNik: String? = response.data.nik
                    val serverNama: String? = response.data.nama
                    val serverEmail: String? = response.data.email
                    val serverFoto: String? = response.data.foto

                    val finalNik = if (serverNik.isNullOrEmpty()) _nik.value else serverNik
                    val finalNama = if (serverNama.isNullOrEmpty()) namaBaru else serverNama
                    val finalEmail = if (serverEmail.isNullOrEmpty()) emailBaru else serverEmail
                    val finalFoto = if (serverFoto.isNullOrEmpty()) _foto.value else serverFoto

                    userPreferences.saveUser(
                        nik = finalNik,
                        nama = finalNama,
                        email = finalEmail,
                        foto = finalFoto
                    )
                    _nama.value = finalNama
                    _email.value = finalEmail
                    _foto.value = finalFoto
                    _fotoLastUpdated.value = System.currentTimeMillis()
                    _isUpdateSuccess.value = true
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

    fun resetUpdateStatus() {
        _isUpdateSuccess.value = false
        _error.value = null
    }

    fun loadProfileFromPreferences() {
        viewModelScope.launch {
            try {
                _nama.value = userPreferences.nama.first()
                _nik.value = userPreferences.nik.first()
                _email.value = userPreferences.email.first()
                _foto.value = userPreferences.foto.first()
                _fotoLastUpdated.value = userPreferences.fotoLastUpdated.first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserProfile() {
        val currentNik = _nik.value
        if (currentNik.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = userRepository.getUser(currentNik)
                if (response.success && response.data != null) {
                    val serverNama = response.data.nama
                    val serverEmail = response.data.email
                    val serverFoto = response.data.foto

                    userPreferences.saveUser(
                        nik = currentNik,
                        nama = serverNama,
                        email = serverEmail,
                        foto = serverFoto
                    )
                    _nama.value = serverNama
                    _email.value = serverEmail
                    _foto.value = serverFoto
                    _fotoLastUpdated.value = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}