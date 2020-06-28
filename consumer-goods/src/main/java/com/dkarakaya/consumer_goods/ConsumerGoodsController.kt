package com.dkarakaya.consumer_goods

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel

class ConsumerGoodsController : EpoxyController() {

    var consumerGoods: List<ConsumerGoodsItemModel> = listOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var consumerGoodsClickListener: (item: ConsumerGoodsItemModel) -> Unit = { }

    override fun buildModels() {
        consumerGoods.forEachIndexed { id, item ->
            consumerGoods {
                id(id)
                item(item)
                onClickListener(View.OnClickListener {
                    consumerGoodsClickListener(item)
                })
            }
        }
    }
}
