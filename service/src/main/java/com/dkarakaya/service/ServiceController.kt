package com.dkarakaya.service

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.dkarakaya.service.model.ServiceItemModel

class ServiceController : EpoxyController() {

    var services: List<ServiceItemModel> = listOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var serviceClickListener: (item: ServiceItemModel) -> Unit = { }

    override fun buildModels() {
        services.forEachIndexed { id, item ->
            service {
                id(id)
                item(item)
                onClickListener(View.OnClickListener {
                    serviceClickListener(item)
                })
            }
        }
    }
}
