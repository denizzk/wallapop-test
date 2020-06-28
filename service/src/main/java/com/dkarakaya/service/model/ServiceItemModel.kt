package com.dkarakaya.service.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceItemModel(
    val id: String,
    val image: String,
    val price: String,
    val name: String,
    val description: String,
    val distanceInMeters: Int?,
    val closeDay: String,
    val category: String,
    val minimumAge: Int
) : Parcelable
