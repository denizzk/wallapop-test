package com.dkarakaya.wallapoptest.model.domain

import com.dkarakaya.wallapoptest.model.remote.ProductKind


data class ProductItemModel(
    val kind: ProductKind,
    val id: String,
    val image: String,
    val price: String,
    val name: String,
    val description: String,
    val distanceInMeters: Int?,
    val item: ProductItem?
)

sealed class ProductItem {
    data class Car(
        val motor: String,
        val gearbox: String,
        val brand: String,
        val km: Int
    ) : ProductItem()

    data class ConsumerGoods(
        val color: String,
        val category: String
    ) : ProductItem()

    data class Service(
        val closeDay: String,
        val category: String,
        val minimumAge: Int
    ) : ProductItem()
}
