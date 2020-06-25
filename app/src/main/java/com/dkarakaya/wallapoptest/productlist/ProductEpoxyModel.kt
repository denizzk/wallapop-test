package com.dkarakaya.wallapoptest.productlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest
import com.dkarakaya.wallapoptest.R
import com.dkarakaya.wallapoptest.model.domain.ProductItemModel


@EpoxyModelClass
abstract class ProductEpoxyModel : EpoxyModelWithHolder<ProductEpoxyModel.ProductHolder>() {

    @EpoxyAttribute
    lateinit var productItem: ProductItemModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun getDefaultLayout(): Int {
        return R.layout.item_service
    }

    override fun bind(holder: ProductHolder) {
        holder.apply {
            val dimensionPixelSize =
                root.context.resources.getDimension(R.dimen.default_border_radius)
            val requestOptions = RequestOptions()
                .transform(
                    CenterCrop(),
                    GranularRoundedCorners(
                        dimensionPixelSize,
                        dimensionPixelSize,
                        0.toFloat(),
                        0.toFloat()
                    )
                )

            Glide
                .with(icon)
                .asBitmap()
                .load(productItem.image)
                .apply(requestOptions)
                .into(icon)

            textPrice.text = productItem.price
            textName.text = productItem.name.uppercaseFirstLetterAndLowercaseRest()
            root.setOnClickListener(onClickListener)
        }
    }

    inner class ProductHolder : EpoxyHolder() {

        lateinit var icon: ImageView
        lateinit var textPrice: TextView
        lateinit var textName: TextView
        lateinit var root: ConstraintLayout

        override fun bindView(itemView: View) {
            icon = itemView.findViewById(R.id.iconItem)
            textPrice = itemView.findViewById(R.id.textItemPrice)
            textName = itemView.findViewById(R.id.textItemName)
            root = itemView.findViewById(R.id.layoutItem)
        }
    }
}
