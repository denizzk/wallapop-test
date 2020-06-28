package com.dkarakaya.consumer_goods.mapper

import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.dkarakaya.core.model.ConsumerGoods
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.util.formatPrice
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest

fun ProductRemoteModel.mapToConsumerGoodsItemModel(): ConsumerGoodsItemModel {
    return ConsumerGoodsItemModel(
        id = (this.item as ConsumerGoods).id,
        image = (this.item as ConsumerGoods).image,
        price = (this.item as ConsumerGoods).price.formatPrice(),
        name = (this.item as ConsumerGoods).name.uppercaseFirstLetterAndLowercaseRest(),
        description = (this.item as ConsumerGoods).description,
        distanceInMeters = (this.item as ConsumerGoods).distanceInMeters,
        color = (this.item as ConsumerGoods).color.uppercaseFirstLetterAndLowercaseRest(),
        category = (this.item as ConsumerGoods).category.uppercaseFirstLetterAndLowercaseRest()
    )
}
