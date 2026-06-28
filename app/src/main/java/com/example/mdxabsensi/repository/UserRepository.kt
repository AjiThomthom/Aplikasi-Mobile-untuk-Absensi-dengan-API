package com.example.mdxabsensi.repository

import com.example.mdxabsensi.data.model.request.UpdateUserRequest
import com.example.mdxabsensi.data.remote.RetrofitClient

class UserRepository {

    private val api = RetrofitClient.apiService

    suspend fun getUser(
        nik: String
    ) = api.getUser(nik)

    suspend fun updateUser(
        request: UpdateUserRequest
    ) = api.updateUser(request)
}