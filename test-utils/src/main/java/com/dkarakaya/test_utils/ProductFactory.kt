package com.dkarakaya.test_utils

import com.dkarakaya.core.model.*

object ProductFactory {
    fun dummyProduct(
        kind: ProductKind = ProductKind.CONSUMER_GOODS,
        item: Item = dummyConsumerGoods()
    ): ProductRemoteModel {
        return ProductRemoteModel(
            kind = kind,
            item = item
        )
    }

    fun dummyCar(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "1000€",
        name: String = "duis",
        motor: String = "gasoline",
        gearbox: String = "manual",
        brand: String = "irure",
        km: Int = 1234,
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100
    ): Car {
        return Car(
            id = id,
            image = image,
            price = price,
            name = name,
            motor = motor,
            gearbox = gearbox,
            brand = brand,
            km = km,
            description = description,
            distanceInMeters = distanceInMeters
        )
    }

    fun dummyConsumerGoods(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "10€",
        name: String = "ball",
        color: String = "pink",
        category: String = "toy",
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100
    ): ConsumerGoods {
        return ConsumerGoods(
            id = id,
            image = image,
            price = price,
            name = name,
            color = color,
            category = category,
            description = description,
            distanceInMeters = distanceInMeters
        )
    }

    fun dummyService(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "100€",
        name: String = "pariatur",
        closeDay: String = "monday",
        category: String = "leisure",
        minimumAge: Int = 18,
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100
    ): Service {
        return Service(
            id = id,
            image = image,
            price = price,
            name = name,
            closeDay = closeDay,
            category = category,
            minimumAge = minimumAge,
            description = description,
            distanceInMeters = distanceInMeters
        )
    }
}
