package com.example.mdxabsensi.repository

import com.example.mdxabsensi.data.model.request.LoginRequest
import com.example.mdxabsensi.data.model.request.RegisterRequest
import com.example.mdxabsensi.data.model.request.ChangePasswordRequest
import com.example.mdxabsensi.data.remote.RetrofitClient
import com.example.mdxabsensi.data.model.response.BaseResponse
class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(
        request: LoginRequest
    ) = api.login(request)

    suspend fun changePassword(
        request: ChangePasswordRequest
    ) = api.changePassword(request)

    suspend fun register(
        request: RegisterRequest
    ) : BaseResponse {

        val response =
            api.register(request)

        if (response.isSuccessful) {

            return response.body()
                ?: BaseResponse(
                    false,
                    "Response kosong"
                )
        }

        return BaseResponse(
            false,
            "NIK atau Email sudah terdaftar"
        )
    }
}