package com.dkarakaya.car.details

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_car_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = requireArguments().getParcelable<CarItemModel>(ARG_CAR)
            ?: throw IllegalArgumentException("$ARG_CAR must be provided!")

        render(item)
    }

    private fun render(item: CarItemModel) {
        Glide
            .with(imageItem)
            .asBitmap()
            .load(item.image)
//            .apply(requestOptions)
            .into(imageItem)
        textAmount.text = item.price
        textName.text = item.name
        textDistance.text = getString(R.string.distance_details, item.distanceInMeters.toString())
        textBrand.text = item.brand
        textGearbox.text = item.gearbox
        textMotor.text = item.motor
        textKm.text = item.km.toString()
        textDescription.text = item.description
    }

    companion object {
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
}
