package com.dkarakaya.wallapoptest

import com.dkarakaya.wallapoptest.ProductListViewModel.SortingType
import com.dkarakaya.wallapoptest.model.domain.ProductItem
import com.dkarakaya.wallapoptest.model.domain.ProductItemModel
import com.dkarakaya.wallapoptest.model.remote.*
import com.dkarakaya.wallapoptest.repository.ProductRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Test

class ProductListViewModelTest {

    private val productRepository = mock<ProductRepository>()
    private val viewModel by lazy {
        ProductListViewModel(
            productRepository = productRepository
        )
    }

    @Test
    fun `GIVEN product list WHEN get product list THEN return the product item list`() {
        val product1 = dummyProduct(item = dummyConsumerGoods(id = "123"))
        val product2 = dummyProduct(item = dummyConsumerGoods(id = "456"))
        givenProductList(listOf(product1, product2))

        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(
                mutableListOf(
                    dummyProductItem(id = "123"),
                    dummyProductItem(id = "456")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list with duplicated products WHEN get product list THEN return the list without duplicated products`() {
        val product = dummyProduct()
        givenProductList(listOf(product, product))

        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(listOf(dummyProductItem()))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get product list THEN return the sorted product item list by ascending distance`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val service = dummyService(id = "345", distanceInMeters = 200)
        val consumerGoods = dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.SERVICE, item = service),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods)
            )
        )

        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        image = "https://test.jpg",
                        price = "1000€",
                        name = "duis",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 100,
                        item = dummyCarItem(
                            motor = "gasoline",
                            gearbox = "manual",
                            brand = "irure",
                            km = 1234
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.CONSUMER_GOODS,
                        id = "567",
                        image = "https://test.jpg",
                        price = "10€",
                        name = "ball",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        image = "https://test.jpg",
                        price = "100€",
                        name = "pariatur",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 200,
                        item = dummyServiceItem(
                            closeDay = "monday",
                            category = "leisure",
                            minimumAge = 18
                        )
                    )
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN sort product list by descending distance THEN return the sorted product item list`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val service = dummyService(id = "345", distanceInMeters = 200)
        val consumerGoods = dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.SERVICE, item = service),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods)
            )
        )

        viewModel.setSortingType(SortingType.DISTANCE_DESC)
        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        image = "https://test.jpg",
                        price = "1000€",
                        name = "duis",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 100,
                        item = dummyCarItem(
                            motor = "gasoline",
                            gearbox = "manual",
                            brand = "irure",
                            km = 1234
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.CONSUMER_GOODS,
                        id = "567",
                        image = "https://test.jpg",
                        price = "10€",
                        name = "ball",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        image = "https://test.jpg",
                        price = "100€",
                        name = "pariatur",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 200,
                        item = dummyServiceItem(
                            closeDay = "monday",
                            category = "leisure",
                            minimumAge = 18
                        )
                    )
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN sort product list by ascending price THEN return the sorted product item list`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val service = dummyService(id = "345", distanceInMeters = 200)
        val consumerGoods = dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.SERVICE, item = service),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods)
            )
        )

        viewModel.setSortingType(SortingType.PRICE_ASC)
        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CONSUMER_GOODS,
                        id = "567",
                        image = "https://test.jpg",
                        price = "10€",
                        name = "ball",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        image = "https://test.jpg",
                        price = "100€",
                        name = "pariatur",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 200,
                        item = dummyServiceItem(
                            closeDay = "monday",
                            category = "leisure",
                            minimumAge = 18
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        image = "https://test.jpg",
                        price = "1000€",
                        name = "duis",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 100,
                        item = dummyCarItem(
                            motor = "gasoline",
                            gearbox = "manual",
                            brand = "irure",
                            km = 1234
                        )
                    )
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN sort product list by descending price THEN return the sorted product item list`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val service = dummyService(id = "345", distanceInMeters = 200)
        val consumerGoods = dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.SERVICE, item = service),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods)
            )
        )

        val getProductList = viewModel.getProductList().test()
        viewModel.setSortingType(SortingType.PRICE_ASC)

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        image = "https://test.jpg",
                        price = "1000€",
                        name = "duis",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 100,
                        item = dummyCarItem(
                            motor = "gasoline",
                            gearbox = "manual",
                            brand = "irure",
                            km = 1234
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        image = "https://test.jpg",
                        price = "100€",
                        name = "pariatur",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 200,
                        item = dummyServiceItem(
                            closeDay = "monday",
                            category = "leisure",
                            minimumAge = 18
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.CONSUMER_GOODS,
                        id = "567",
                        image = "https://test.jpg",
                        price = "10€",
                        name = "ball",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    )
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    private fun givenProductList(productList: List<ProductRemoteModel>) {
        whenever(productRepository.getProduct()).thenReturn(Observable.just(productList))
    }

    private fun dummyProduct(
        kind: ProductKind = ProductKind.CONSUMER_GOODS,
        item: Item = dummyConsumerGoods()
    ): ProductRemoteModel {
        return ProductRemoteModel(
            kind = kind,
            item = item
        )
    }

    private fun dummyProductItem(
        kind: ProductKind = ProductKind.CONSUMER_GOODS,
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "10€",
        name: String = "ball",
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100,
        item: ProductItem = dummyConsumerGoodsItem()
    ): ProductItemModel {
        return ProductItemModel(
            kind = kind,
            id = id,
            image = image,
            price = price,
            name = name,
            description = description,
            distanceInMeters = distanceInMeters,
            item = item
        )
    }

    private fun dummyCar(
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

    private fun dummyCarItem(
        motor: String = "gasoline",
        gearbox: String = "manual",
        brand: String = "irure",
        km: Int = 1234
    ): ProductItem.Car {
        return ProductItem.Car(
            motor = motor,
            gearbox = gearbox,
            brand = brand,
            km = km
        )
    }

    private fun dummyConsumerGoods(
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

    private fun dummyConsumerGoodsItem(
        color: String = "pink",
        category: String = "toy"
    ): ProductItem.ConsumerGoods {
        return ProductItem.ConsumerGoods(
            color = color,
            category = category
        )
    }

    private fun dummyService(
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

    private fun dummyServiceItem(
        closeDay: String = "monday",
        category: String = "leisure",
        minimumAge: Int = 18
    ): ProductItem.Service {
        return ProductItem.Service(
            closeDay = closeDay,
            category = category,
            minimumAge = minimumAge
        )
    }
}
