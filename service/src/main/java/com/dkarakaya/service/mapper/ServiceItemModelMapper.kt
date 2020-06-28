package com.dkarakaya.service.mapper

import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.model.Service
import com.dkarakaya.core.util.formatPrice
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest
import com.dkarakaya.service.model.ServiceItemModel

fun ProductRemoteModel.mapToServiceItemModel(): ServiceItemModel {
    return ServiceItemModel(
        id = (this.item as Service).id,
        image = (this.item as Service).image,
        price = (this.item as Service).price.formatPrice(),
        name = (this.item as Service).name.uppercaseFirstLetterAndLowercaseRest(),
        description = (this.item as Service).description,
        distanceInMeters = (this.item as Service).distanceInMeters,
        category = (this.item as Service).category.uppercaseFirstLetterAndLowercaseRest(),
        closeDay = (this.item as Service).closeDay.uppercaseFirstLetterAndLowercaseRest(),
        minimumAge = (this.item as Service).minimumAge
    )
}
