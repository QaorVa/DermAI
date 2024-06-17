package com.example.dermai.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Skincare(
    @SerializedName("face-moisturisers")
    val faceMoisturisers: List<Product>,
    @SerializedName("cleanser")
    val cleansers: List<Product>,
    @SerializedName("mask-and-peel")
    val masksAndPeels: List<Product>,
    @SerializedName("eye-cream")
    val eyeCreams: List<Product>
) : Parcelable
