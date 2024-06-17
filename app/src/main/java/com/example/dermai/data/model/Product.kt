package com.example.dermai.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product(
    val brand: String,
    val name: String,
    val price: String,
    @PrimaryKey
    val url: String,
    val img: String,
    @SerializedName("skin type")
    val skinType: String,
    @SerializedName("concern")
    val concerns: List<String>,
    var isFavorited: Boolean = false,
) : Parcelable
