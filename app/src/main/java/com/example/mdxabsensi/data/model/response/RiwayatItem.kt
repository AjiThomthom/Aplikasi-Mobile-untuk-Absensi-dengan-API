package com.example.mdxabsensi.data.model.response



data class RiwayatItem(
    val NIK: String,
    val timeinout: String,
    val type: String,
    val fotoselfie: String,
    val timestamp: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
