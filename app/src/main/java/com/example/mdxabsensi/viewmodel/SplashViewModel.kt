package com.example.mdxabsensi.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdxabsensi.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoggedIn =
        MutableStateFlow(false)

    val isLoggedIn: StateFlow<Boolean>
            = _isLoggedIn

    init {

        viewModelScope.launch {

            userPreferences.isLoggedIn
                .collect {

                    _isLoggedIn.value = it

                }
        }
    }
}