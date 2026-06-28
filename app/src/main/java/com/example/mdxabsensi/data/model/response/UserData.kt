package com.example.mdxabsensi.data.model.response

import com.google.gson.annotations.SerializedName

data class UserData(

    @SerializedName("NIK")
    val nik: String,

    @SerializedName("NAMA")
    val nama: String,

    @SerializedName("EMAIL")
    val email: String,

    @SerializedName("FOTO")
    val foto: String
)