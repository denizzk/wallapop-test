package com.dkarakaya.consumer_goods.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dkarakaya.consumer_goods.R
import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_consumer_goods_details.*

class ConsumerGoodsDetailsFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consumer_goods_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        val item = requireArguments().getParcelable<ConsumerGoodsItemModel>(ARG_CONSUMERGOODS)
            ?: throw IllegalArgumentException("$ARG_CONSUMERGOODS must be provided!")

        render(item)
    }

    private fun render(item: ConsumerGoodsItemModel) {
        Glide
            .with(imageItem)
            .asBitmap()
            .load(item.image)
            .into(imageItem)

        textHeader.text = item.price
        textName.text = item.name
        textDistance.text = getString(R.string.distance_details, item.distanceInMeters.toString())
        textCategory.text = item.category
        textColor.text = item.color
        textDescription.text = item.description

        buttonShare.setOnClickListener { shareItem(item.id) }
    }

    private fun shareItem(itemId: String) {
        val deepLinkIntent = Intent(Intent.ACTION_SEND)
        deepLinkIntent.type = "text/plain"
        deepLinkIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_url))
        deepLinkIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.deep_link, itemId))
        startActivity(Intent.createChooser(deepLinkIntent, getString(R.string.share_url)))
    }

    companion object {
        const val TAG_CONSUMERGOODSDETAILSFRAGMENT = "ConsumerGoodsDetailsFragment"
        private const val ARG_CONSUMERGOODS = "consumergoods"
        fun newInstance(item: ConsumerGoodsItemModel): ConsumerGoodsDetailsFragment {
            return ConsumerGoodsDetailsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CONSUMERGOODS, item)
                    }
                }


        }
    }
}
