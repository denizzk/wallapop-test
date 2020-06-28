package com.dkarakaya.wallapoptest.mapper

import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest
import com.dkarakaya.service.model.ServiceItemModel
import com.dkarakaya.wallapoptest.model.ProductItem
import com.dkarakaya.wallapoptest.model.ProductItemModel

fun ProductItemModel.mapToCarItemModel(): CarItemModel {
    val item = this.item as ProductItem.Car
    return CarItemModel(
        id = this.id,
        image = this.image,
        price = this.price,
        name = this.name.uppercaseFirstLetterAndLowercaseRest(),
        description = this.description,
        distanceInMeters = this.distanceInMeters,
        motor = item.motor.uppercaseFirstLetterAndLowercaseRest(),
        gearbox = item.gearbox.uppercaseFirstLetterAndLowercaseRest(),
        brand = item.brand.uppercaseFirstLetterAndLowercaseRest(),
        km = item.km
    )
}

fun ProductItemModel.mapToConsumerGoodsItemModel(): ConsumerGoodsItemModel {
    val item = this.item as ProductItem.ConsumerGoods
    return ConsumerGoodsItemModel(
        id = this.id,
        image = this.image,
        price = this.price,
        name = this.name.uppercaseFirstLetterAndLowercaseRest(),
        description = this.description,
        distanceInMeters = this.distanceInMeters,
        category = item.category.uppercaseFirstLetterAndLowercaseRest(),
        color = item.color.uppercaseFirstLetterAndLowercaseRest()
    )
}

fun ProductItemModel.mapToServiceItemModel(): ServiceItemModel {
    val item = this.item as ProductItem.Service
    return ServiceItemModel(
        id = this.id,
        image = this.image,
        price = this.price,
        name = this.name.uppercaseFirstLetterAndLowercaseRest(),
        description = this.description,
        distanceInMeters = this.distanceInMeters,
        category = item.category.uppercaseFirstLetterAndLowercaseRest(),
        closeDay = item.closeDay.uppercaseFirstLetterAndLowercaseRest(),
        minimumAge = item.minimumAge
    )
}
