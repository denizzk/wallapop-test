package com.dkarakaya.wallapoptest.repository.gsonadapter

import com.dkarakaya.wallapoptest.model.*
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import timber.log.Timber
import java.lang.reflect.Type

class ProductDeserializer : JsonDeserializer<Product> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Product? {
        if (json == null) {
            return null
        }
        val jsonObject = json as JsonObject
        val kindElement: JsonElement = jsonObject.get("kind")
        val itemElement: JsonElement = jsonObject.get("item")

        return Product(
            kind = kindElement.asString,
            item = item(kindElement, itemElement, context)
        )
    }

    private fun item(
        kindElement: JsonElement,
        itemElement: JsonElement,
        context: JsonDeserializationContext?
    ): Item {
        val itemClass = when (kindElement.asString) {
            "car" -> Car::class.java
            "consumer_goods" -> ConsumerGoods::class.java
            "service" -> Service::class.java
            else -> {
                Timber.e("Could not parse product item type $kindElement")
                null
            }
        }
        return context?.deserialize(itemElement, itemClass) as Item
    }
}
