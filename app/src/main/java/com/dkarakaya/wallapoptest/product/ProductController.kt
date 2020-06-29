package com.dkarakaya.wallapoptest.product

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.dkarakaya.wallapoptest.model.ProductItemModel

class ProductController : EpoxyController() {

    var products: List<ProductItemModel> = listOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var productClickListener: (productItem: ProductItemModel) -> Unit = { }

    override fun buildModels() {
        products.forEachIndexed { id, product ->
            product {
                id(id)
                item(product)
                onClickListener(View.OnClickListener {
                    productClickListener(product)
                })
            }
        }
    }
}
