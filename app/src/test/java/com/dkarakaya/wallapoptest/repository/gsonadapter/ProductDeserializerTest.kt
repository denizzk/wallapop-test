package com.dkarakaya.wallapoptest.repository.gsonadapter

import com.dkarakaya.wallapoptest.model.remote.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test

class ProductDeserializerTest {

    private val gson: Gson = GsonBuilder()
        .apply {
            registerTypeAdapter(ProductRemoteModel::class.java, ProductDeserializer())
        }
        .create()

    @Test
    fun `GIVEN json input with car kind THEN parse the json to product with car model`() {
        val jsonInput =
            """
            {
                "kind": "car",
                "item": {
                  "id": "5a7ab5107934615b51cc7fc9",
                  "image": "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image6.jpg",
                  "price": "474817€",
                  "name": "duis",
                  "motor": "electric",
                  "gearbox": "manual",
                  "brand": "duis",
                  "km": 5636,
                  "description": "Lorem voluptate labore sint non.",
                  "distanceInMeters": 952
                }
            }
            """.trimIndent()

        val expectedOutput =
            ProductRemoteModel(
                kind = ProductKind.CAR,
                item = Car(
                    id = "5a7ab5107934615b51cc7fc9",
                    image = "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image6.jpg",
                    price = "474817€",
                    name = "duis",
                    motor = "electric",
                    gearbox = "manual",
                    brand = "duis",
                    km = 5636,
                    description = "Lorem voluptate labore sint non.",
                    distanceInMeters = 952
                )
            )


        Assert.assertEquals(expectedOutput, gson.fromJson(jsonInput, ProductRemoteModel::class.java))
    }

    @Test
    fun `GIVEN json input with consumer goods kind THEN parse the json to product with consumer goods model`() {
        val jsonInput =
            """
              {
                "kind": "consumer_goods",
                "item": {
                  "id": "5a7ab3e09798181675dc1751",
                  "image": "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image9.jpg",
                  "price": "447€",
                  "name": "enim",
                  "color": "orange",
                  "category": "children",
                  "description": "Officia excepteur exercitation laborum tempor anim non.",
                  "distanceInMeters": 414
                }
              }
            """.trimIndent()

        val expectedOutput =
            ProductRemoteModel(
                kind = ProductKind.CONSUMER_GOODS,
                item = ConsumerGoods(
                    id = "5a7ab3e09798181675dc1751",
                    image = "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image9.jpg",
                    price = "447€",
                    name = "enim",
                    color = "orange",
                    category = "children",
                    description = "Officia excepteur exercitation laborum tempor anim non.",
                    distanceInMeters = 414
                )
            )


        Assert.assertEquals(expectedOutput, gson.fromJson(jsonInput, ProductRemoteModel::class.java))
    }

    @Test
    fun `GIVEN json input with service kind THEN parse the json to product with service model`() {
        val jsonInput =
            """
              {
                "kind": "service",
                "item": {
                  "id": "5a7abb02425794c72f650d7f",
                  "image": "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image8.jpg",
                  "price": "108€",
                  "name": "fugiat",
                  "closeDay": "saturday",
                  "category": "aventure",
                  "minimunAge": 27,
                  "description": "Deserunt ariatur ullamco nulla enim do cillum velit.",
                  "distanceInMeters": 958
                }
              }
            """.trimIndent()

        val expectedOutput =
            ProductRemoteModel(
                kind = ProductKind.SERVICE,
                item = Service(
                    id = "5a7abb02425794c72f650d7f",
                    image = "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/images/image8.jpg",
                    price = "108€",
                    name = "fugiat",
                    closeDay = "saturday",
                    category = "aventure",
                    minimumAge = 27,
                    description = "Deserunt ariatur ullamco nulla enim do cillum velit.",
                    distanceInMeters = 958
                )
            )


        Assert.assertEquals(expectedOutput, gson.fromJson(jsonInput, ProductRemoteModel::class.java))
    }

}
