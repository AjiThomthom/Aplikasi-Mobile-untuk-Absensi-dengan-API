package com.example.mdxabsensi.data.remote

import com.example.mdxabsensi.data.model.request.*
import com.example.mdxabsensi.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BaseResponse>

    @POST("absensi")
    suspend fun absensi(
        @Body request: AbsensiRequest
    ): BaseResponse

    @GET("riwayat")
    suspend fun getRiwayat(
        @Query("nik") nik: String
    ): RiwayatResponse

    @GET("user")
    suspend fun getUser(
        @Query("nik") nik: String
    ): UserResponse

    @PUT("user")
    suspend fun updateUser(
        @Body request: UpdateUserRequest
    ): UserResponse

    @POST("change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): BaseResponse
}