package com.example.mdxabsensi.repository


import com.example.mdxabsensi.data.model.request.AbsensiRequest
import com.example.mdxabsensi.data.remote.RetrofitClient

class AbsensiRepository {

    private val api = RetrofitClient.apiService

    suspend fun absensi(
        request: AbsensiRequest
    ) = api.absensi(request)

    suspend fun getRiwayat(
        nik: String
    ) = api.getRiwayat(nik)
}