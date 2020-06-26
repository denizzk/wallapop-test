package com.dkarakaya.car.mapper

import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.model.*
import com.dkarakaya.core.util.formatPrice
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest

fun ProductRemoteModel.mapToCarItemModel(): CarItemModel {
    return CarItemModel(
        id = (this.item as Car).id,
        image = (this.item as Car).image,
        price = (this.item as Car).price.formatPrice(),
        name = (this.item as Car).name.uppercaseFirstLetterAndLowercaseRest(),
        description = (this.item as Car).description,
        distanceInMeters = (this.item as Car).distanceInMeters,
        motor = (this.item as Car).motor.uppercaseFirstLetterAndLowercaseRest(),
        gearbox = (this.item as Car).gearbox.uppercaseFirstLetterAndLowercaseRest(),
        brand = (this.item as Car).brand.uppercaseFirstLetterAndLowercaseRest(),
        km = (this.item as Car).km
    )
}
