package com.dkarakaya.consumer_goods.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsumerGoodsItemModel(
    val id: String,
    val image: String,
    val price: String,
    val name: String,
    val description: String,
    val distanceInMeters: Int?,
    val color: String,
    val category: String
) : Parcelable
