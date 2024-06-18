package com.example.dermai.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dermai.utils.Converters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@TypeConverters(Converters::class)
data class ResultResponse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("type") val type: String,
    @SerializedName("tone") val tone: String,
    @SerializedName("acne") val acne: String,
    @SerializedName("skincare_recommendations")
    val recommendedProducts: Skincare
) : Parcelable