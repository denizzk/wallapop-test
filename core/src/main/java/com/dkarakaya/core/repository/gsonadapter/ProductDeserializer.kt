package com.dkarakaya.core.repository.gsonadapter

import com.dkarakaya.core.model.*
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import timber.log.Timber
import java.lang.reflect.Type

class ProductDeserializer : JsonDeserializer<ProductRemoteModel> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ProductRemoteModel? {
        if (json == null) {
            return null
        }
        val jsonObject = json as JsonObject
        val kindElement: JsonElement = jsonObject.get("kind")
        val itemElement: JsonElement = jsonObject.get("item")

        return kindElement.asProductKind()?.let {
            ProductRemoteModel(
                kind = it,
                item = item(kindElement, itemElement, context)
            )
        }
    }

    private fun JsonElement.asProductKind(): ProductKind? {
        return when (this.asString) {
            "car" -> ProductKind.CAR
            "consumer_goods" -> ProductKind.CONSUMER_GOODS
            "service" -> ProductKind.SERVICE
            else -> null
        }
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

