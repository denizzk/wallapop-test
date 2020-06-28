package com.dkarakaya.service.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dkarakaya.service.R
import com.dkarakaya.service.model.ServiceItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_service_details.*

class ServiceDetailsFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        val item = requireArguments().getParcelable<ServiceItemModel>(ARG_SERVICE)
            ?: throw IllegalArgumentException("$ARG_SERVICE must be provided!")

        render(item)
    }

    private fun render(item: ServiceItemModel) {
        Glide
            .with(imageItem)
            .asBitmap()
            .load(item.image)
            .into(imageItem)

        textHeader.text = item.price
        textName.text = item.name
        textDistance.text = getString(R.string.distance_details, item.distanceInMeters.toString())
        textCategory.text = item.category
        textCloseDay.text = item.closeDay
        textMinAge.text = item.minimumAge.toString()
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
        const val TAG_SERVICEDETAILSFRAGMENT = "ServiceDetailsFragment"
        private const val ARG_SERVICE = "service"
        fun newInstance(item: ServiceItemModel): ServiceDetailsFragment {
            return ServiceDetailsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_SERVICE, item)
                    }
                }


        }
    }
}
