package com.example.dermai.data.model

import android.net.Uri

data class Product(
    val id: Int = 0,
    val name: String = "",
    val rating: Float = 0.0f,
    val price: Int = 0,
    val isFavorited: Boolean = false,
    val tags: String = "",
    val imageUri: String = "",
    val link: String = "",
    val category: String = ""
)

