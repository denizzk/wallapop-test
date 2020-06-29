package com.dkarakaya.wallapoptest

import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.sorting.SortingType
import com.dkarakaya.core.util.await
import com.dkarakaya.test_utils.ProductFactory.dummyCar
import com.dkarakaya.test_utils.ProductFactory.dummyConsumerGoods
import com.dkarakaya.test_utils.ProductFactory.dummyProduct
import com.dkarakaya.test_utils.ProductFactory.dummyService
import com.dkarakaya.wallapoptest.model.ProductItem
import com.dkarakaya.wallapoptest.model.ProductItemModel
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

        val getProductList = viewModel.getProductList().test().await(2)

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

        val getProductList = viewModel.getProductList().test().await(1)

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

        val getProductList = viewModel.getProductList().test().await(3)

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        price = "1,000.00 €",
                        name = "duis",
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
                        price = "10.00 €",
                        name = "ball",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        price = "100.00 €",
                        name = "pariatur",
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
    fun `GIVEN product list WHEN get first page of list THEN return the first page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100")),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105")),
                dummyProduct(item = dummyConsumerGoods(id = "106")),
                dummyProduct(item = dummyConsumerGoods(id = "107")),
                dummyProduct(item = dummyConsumerGoods(id = "108")),
                dummyProduct(item = dummyConsumerGoods(id = "109")),
                dummyProduct(item = dummyConsumerGoods(id = "110")),
                dummyProduct(item = dummyConsumerGoods(id = "111"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyProductItem(id = "100"),
                    dummyProductItem(id = "101"),
                    dummyProductItem(id = "102"),
                    dummyProductItem(id = "103"),
                    dummyProductItem(id = "104"),
                    dummyProductItem(id = "105"),
                    dummyProductItem(id = "106"),
                    dummyProductItem(id = "107"),
                    dummyProductItem(id = "108"),
                    dummyProductItem(id = "109")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get second page of list THEN return the first page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100")),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105")),
                dummyProduct(item = dummyConsumerGoods(id = "106")),
                dummyProduct(item = dummyConsumerGoods(id = "107")),
                dummyProduct(item = dummyConsumerGoods(id = "108")),
                dummyProduct(item = dummyConsumerGoods(id = "109")),
                dummyProduct(item = dummyConsumerGoods(id = "110")),
                dummyProduct(item = dummyConsumerGoods(id = "111")),
                dummyProduct(item = dummyConsumerGoods(id = "112")),
                dummyProduct(item = dummyConsumerGoods(id = "113")),
                dummyProduct(item = dummyConsumerGoods(id = "114")),
                dummyProduct(item = dummyConsumerGoods(id = "115")),
                dummyProduct(item = dummyConsumerGoods(id = "116")),
                dummyProduct(item = dummyConsumerGoods(id = "117")),
                dummyProduct(item = dummyConsumerGoods(id = "118")),
                dummyProduct(item = dummyConsumerGoods(id = "119"))
            )
        )

        viewModel.setPageNumber(1)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyProductItem(id = "110"),
                    dummyProductItem(id = "111"),
                    dummyProductItem(id = "112"),
                    dummyProductItem(id = "113"),
                    dummyProductItem(id = "114"),
                    dummyProductItem(id = "115"),
                    dummyProductItem(id = "116"),
                    dummyProductItem(id = "117"),
                    dummyProductItem(id = "118"),
                    dummyProductItem(id = "119")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get last page of list THEN return the last page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100")),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyProductItem(id = "100"),
                    dummyProductItem(id = "101"),
                    dummyProductItem(id = "102"),
                    dummyProductItem(id = "103"),
                    dummyProductItem(id = "104"),
                    dummyProductItem(id = "105")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get not last page of list THEN return false`() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100")),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105")),
                dummyProduct(item = dummyConsumerGoods(id = "106")),
                dummyProduct(item = dummyConsumerGoods(id = "107")),
                dummyProduct(item = dummyConsumerGoods(id = "108")),
                dummyProduct(item = dummyConsumerGoods(id = "109")),
                dummyProduct(item = dummyConsumerGoods(id = "110"))
            )
        )

        val getProductList = viewModel.isLastPage().test().await(1)
        viewModel.setPageNumber(0)

        getProductList
            .assertValue(false)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get last page of list THEN return true`() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100")),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105"))
            )
        )

        val getProductList = viewModel.isLastPage().test().await(1)
        viewModel.setPageNumber(0)

        getProductList
            .assertValue(true)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get distance range THEN return the distances of given first and last visible item `() {
        givenProductList(
            listOf(
                dummyProduct(item = dummyConsumerGoods(id = "100", distanceInMeters = 100)),
                dummyProduct(item = dummyConsumerGoods(id = "101")),
                dummyProduct(item = dummyConsumerGoods(id = "102")),
                dummyProduct(item = dummyConsumerGoods(id = "103")),
                dummyProduct(item = dummyConsumerGoods(id = "104")),
                dummyProduct(item = dummyConsumerGoods(id = "105", distanceInMeters = 200))
            )
        )

        val getProductList = viewModel.getDistanceRange().test().await(1)
        viewModel.setFirstLastVisibleItems(0 to 5)

        getProductList
            .assertValue(100 to 200)
            .assertNoErrors()
            .assertNotComplete()
    }


    @Test
    fun `GIVEN product list WHEN item click twice third THEN don't show ad`() {
        givenProductList(listOf(dummyProduct()))

        viewModel.itemClicked()
        viewModel.itemClicked()
        val isShowingAd = viewModel.isShowingAd().test()

        isShowingAd
            .assertValue(false)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN item click thrice third THEN don't show ad`() {
        givenProductList(listOf(dummyProduct()))

        viewModel.itemClicked()
        viewModel.itemClicked()
        viewModel.itemClicked()
        val isShowingAd = viewModel.isShowingAd().test()

        isShowingAd
            .assertValue(true)
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
        val getProductList = viewModel.getProductList().test().await(3)
        viewModel.setSortingType(SortingType.DISTANCE_DESC)
        viewModel.setSorting()

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        price = "100.00 €",
                        name = "pariatur",
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
                        price = "10.00 €",
                        name = "ball",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        price = "1,000.00 €",
                        name = "duis",
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

        val getProductList = viewModel.getSortedProductList().test().await(3)
        viewModel.setSortingType(SortingType.PRICE_ASC)

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CONSUMER_GOODS,
                        id = "567",
                        price = "10.00 €",
                        name = "ball",
                        distanceInMeters = 150,
                        item = dummyConsumerGoodsItem(
                            color = "pink",
                            category = "toy"
                        )
                    ),
                    dummyProductItem(
                        kind = ProductKind.SERVICE,
                        id = "345",
                        price = "100.00 €",
                        name = "pariatur",
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
                        price = "1,000.00 €",
                        name = "duis",
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

        val getProductList = viewModel.getProductList().test().await(3)
        viewModel.setSortingType(SortingType.PRICE_ASC)

        getProductList
            .assertValue(
                listOf(
                    dummyProductItem(
                        kind = ProductKind.CAR,
                        id = "123",
                        price = "1,000.00 €",
                        name = "duis",
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
                        price = "100.00 €",
                        name = "pariatur",
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
                        price = "10.00 €",
                        name = "ball",
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

    private fun dummyProductItem(
        kind: ProductKind = ProductKind.CONSUMER_GOODS,
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "10.00 €",
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

    private fun dummyConsumerGoodsItem(
        color: String = "pink",
        category: String = "toy"
    ): ProductItem.ConsumerGoods {
        return ProductItem.ConsumerGoods(
            color = color,
            category = category
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
