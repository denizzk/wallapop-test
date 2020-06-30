package com.dkarakaya.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductRemoteModel(
    @SerializedName("kind")
    @Expose
    val kind: ProductKind,
    @SerializedName("item")
    @Expose
    val item: Item
)

enum class ProductKind {
    @SerializedName("car")
    CAR,
    @SerializedName("consumer_goods")
    CONSUMER_GOODS,
    @SerializedName("service")
    SERVICE
}

interface Item

data class Car(
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("image")
    @Expose
    val image: String,
    @SerializedName("price")
    @Expose
    val price: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("motor")
    @Expose
    val motor: String,
    @SerializedName("gearbox")
    @Expose
    val gearbox: String,
    @SerializedName("brand")
    @Expose
    val brand: String,
    @SerializedName("km")
    @Expose
    val km: Int,
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("distanceInMeters")
    @Expose
    val distanceInMeters: Int
) : Item

data class ConsumerGoods(
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("image")
    @Expose
    val image: String,
    @SerializedName("price")
    @Expose
    val price: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("color")
    @Expose
    val color: String,
    @SerializedName("category")
    @Expose
    val category: String,
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("distanceInMeters")
    @Expose
    val distanceInMeters: Int
) : Item

data class Service(
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("image")
    @Expose
    val image: String,
    @SerializedName("price")
    @Expose
    val price: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("closeDay")
    @Expose
    val closeDay: String,
    @SerializedName("category")
    @Expose
    val category: String,
    @SerializedName("minimunAge")
    @Expose
    val minimumAge: Int,
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("distanceInMeters")
    @Expose
    val distanceInMeters: Int
) : Item
