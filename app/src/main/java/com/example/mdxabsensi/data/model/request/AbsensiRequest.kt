package com.example.mdxabsensi.data.model.request

data class AbsensiRequest(
    val nik: String,
    val type: String,
    val timeinout: String,
    val foto_base64: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)