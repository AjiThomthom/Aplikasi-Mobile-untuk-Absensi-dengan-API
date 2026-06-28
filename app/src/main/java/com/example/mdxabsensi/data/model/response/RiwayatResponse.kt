package com.example.mdxabsensi.data.model.response


data class RiwayatResponse(
    val success: Boolean,
    val message: String,
    val data: List<RiwayatItem>
)
