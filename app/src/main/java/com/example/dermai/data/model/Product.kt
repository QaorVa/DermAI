package com.example.dermai.data.model

import android.net.Uri

data class Product(
    val id: Int,
    val name: String,
    val rating: Float,
    val price: Int,
    val isFavorited: Boolean = false,
    val tags: String,
    val imageUri: Uri?,
    val link: String,
    val category: String
)
