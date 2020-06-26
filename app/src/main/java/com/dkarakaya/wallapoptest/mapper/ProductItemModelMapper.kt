package com.dkarakaya.wallapoptest.mapper

import com.dkarakaya.core.model.*
import com.dkarakaya.core.util.formatPrice
import com.dkarakaya.wallapoptest.model.ProductItem
import com.dkarakaya.wallapoptest.model.ProductItemModel

fun ProductRemoteModel.mapToProductItemModel(): ProductItemModel {
    return ProductItemModel(
        kind = this.kind,
        id = this.item.mapToItemId(),
        image = this.item.mapToItemImage(),
        price = this.item.mapToItemPrice().formatPrice(),
        name = this.item.mapToItemName(),
        description = this.item.mapToItemDescription(),
        distanceInMeters = this.item.mapToItemDistanceInMeters(),
        item = this.item.mapToProductItem()
    )
}

private fun Item.mapToItemId(): String {
    return when (this) {
        is Car -> this.id
        is ConsumerGoods -> this.id
        is Service -> this.id
        else -> ""
    }
}

private fun Item.mapToItemImage(): String {
    return when (this) {
        is Car -> this.image
        is ConsumerGoods -> this.image
        is Service -> this.image
        else -> ""
    }
}

private fun Item.mapToItemPrice(): String {
    return when (this) {
        is Car -> this.price
        is ConsumerGoods -> this.price
        is Service -> this.price
        else -> ""
    }
}

private fun Item.mapToItemName(): String {
    return when (this) {
        is Car -> this.name
        is ConsumerGoods -> this.name
        is Service -> this.name
        else -> ""
    }
}

private fun Item.mapToItemDescription(): String {
    return when (this) {
        is Car -> this.description
        is ConsumerGoods -> this.description
        is Service -> this.description
        else -> ""
    }
}

private fun Item.mapToItemDistanceInMeters(): Int? {
    return when (this) {
        is Car -> this.distanceInMeters
        is ConsumerGoods -> this.distanceInMeters
        is Service -> this.distanceInMeters
        else -> null
    }
}

private fun Item.mapToProductItem(): ProductItem? {
    return when (this) {
        is Car -> this.mapToCarItem()
        is ConsumerGoods -> this.mapToConsumerGoodsItem()
        is Service -> this.mapToServiceItem()
        else -> null
    }
}

private fun Car.mapToCarItem(): ProductItem.Car {
    return ProductItem.Car(
        motor = this.motor,
        gearbox = this.gearbox,
        brand = this.brand,
        km = this.km
    )
}

private fun ConsumerGoods.mapToConsumerGoodsItem(): ProductItem.ConsumerGoods {
    return ProductItem.ConsumerGoods(
        color = this.color,
        category = this.category
    )
}

private fun Service.mapToServiceItem(): ProductItem.Service {
    return ProductItem.Service(
        closeDay = this.closeDay,
        category = this.category,
        minimumAge = this.minimumAge
    )
}
