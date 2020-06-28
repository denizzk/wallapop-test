package com.dkarakaya.car.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dkarakaya.car.R
import com.dkarakaya.car.model.CarItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_car_details.*


class CarDetailsFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_car_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        val item = requireArguments().getParcelable<CarItemModel>(ARG_CAR)
            ?: throw IllegalArgumentException("$ARG_CAR must be provided!")

        render(item)
    }

    private fun render(item: CarItemModel) {
        Glide
            .with(imageItem)
            .asBitmap()
            .load(item.image)
            .into(imageItem)

        textHeader.text = item.price
        textName.text = item.name
        textDistance.text = getString(R.string.distance_details, item.distanceInMeters.toString())
        textBrand.text = item.brand
        textGearbox.text = item.gearbox
        textMotor.text = item.motor
        textKm.text = item.km.toString()
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
        const val TAG_CARDETAILSFRAGMENT = "CarDetailsFragment"
        private const val ARG_CAR = "car"
        fun newInstance(item: CarItemModel): CarDetailsFragment {
            return CarDetailsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CAR, item)
                    }
                }


        }
    }

    var companion = Companion
}
