package com.dkarakaya.car.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CarItemModel(
    val id: String,
    val image: String,
    val price: String,
    val name: String,
    val description: String,
    val distanceInMeters: Int?,
    val motor: String,
    val gearbox: String,
    val brand: String,
    val km: Int
) : Parcelable
