package com.dkarakaya.wallapoptest.productlist

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.dkarakaya.wallapoptest.model.domain.ProductItemModel

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
                productItem(product)
                onClickListener(View.OnClickListener {
                    productClickListener(product)
                })
            }
        }
    }
}
