package com.dkarakaya.car

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.dkarakaya.car.model.CarItemModel

class CarController : EpoxyController() {

    var cars: List<CarItemModel> = listOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var carClickListener: (productItem: CarItemModel) -> Unit = { }

    override fun buildModels() {
        cars.forEachIndexed { id, car ->
            car {
                id(id)
                item(car)
                onClickListener(View.OnClickListener {
                    carClickListener(car)
                })
            }
        }
    }
}
