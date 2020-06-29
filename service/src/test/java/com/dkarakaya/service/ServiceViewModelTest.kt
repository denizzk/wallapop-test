package com.dkarakaya.service

import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.await
import com.dkarakaya.service.model.ServiceItemModel
import com.dkarakaya.test_utils.ProductFactory.dummyCar
import com.dkarakaya.test_utils.ProductFactory.dummyConsumerGoods
import com.dkarakaya.test_utils.ProductFactory.dummyProduct
import com.dkarakaya.test_utils.ProductFactory.dummyService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Test

class ServiceViewModelTest {

    private val productRepository = mock<ProductRepository>()
    private val viewModel by lazy {
        ServiceViewModel(
            productRepository = productRepository
        )
    }

    @Test
    fun `GIVEN product list WHEN get service list THEN return the service items from the list`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val consumerGoods = dummyConsumerGoods(id = "345")
        val service = dummyService(id = "567")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods
                ),
                dummyProduct(kind = ProductKind.SERVICE, item = service)
            )
        )

        val getProductList = viewModel.getServiceList().test().await(1)

        getProductList
            .assertValue(mutableListOf(dummyServiceItem(id = "567")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list with duplicated services WHEN get service list THEN return the list without duplicated services`() {
        val service1 = dummyService(id = "123")
        val service2 = dummyService(id = "123")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = service1),
                dummyProduct(kind = ProductKind.SERVICE, item = service2)
            )
        )

        val getProductList = viewModel.getServiceList().test().await(1)

        getProductList
            .assertValue(listOf(dummyServiceItem(id = "123")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get service list THEN return the sorted service item list by ascending distance`() {
        val service1 = dummyService(id = "123", distanceInMeters = 100)
        val service2 = dummyService(id = "345", distanceInMeters = 200)
        val service3 = dummyService(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = service1),
                dummyProduct(kind = ProductKind.SERVICE, item = service2),
                dummyProduct(kind = ProductKind.SERVICE, item = service3)
            )
        )

        val getProductList = viewModel.getServiceList().test().await(3)

        getProductList
            .assertValue(
                listOf(
                    dummyServiceItem(id = "123", distanceInMeters = 100),
                    dummyServiceItem(id = "567", distanceInMeters = 150),
                    dummyServiceItem(id = "345", distanceInMeters = 200)
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get first page of list THEN return the first page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "100")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "105")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "106")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "107")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "108")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "109")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "110")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "111"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyServiceItem(id = "100"),
                    dummyServiceItem(id = "101"),
                    dummyServiceItem(id = "102"),
                    dummyServiceItem(id = "103"),
                    dummyServiceItem(id = "104"),
                    dummyServiceItem(id = "105"),
                    dummyServiceItem(id = "106"),
                    dummyServiceItem(id = "107"),
                    dummyServiceItem(id = "108"),
                    dummyServiceItem(id = "109")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get second page of list THEN return the second page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "100")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "105")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "106")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "107")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "108")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "109")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "110")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "111")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "112")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "113")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "114")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "115")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "116")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "117")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "118")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "119"))
            )
        )

        viewModel.setPageNumber(1)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyServiceItem(id = "110"),
                    dummyServiceItem(id = "111"),
                    dummyServiceItem(id = "112"),
                    dummyServiceItem(id = "113"),
                    dummyServiceItem(id = "114"),
                    dummyServiceItem(id = "115"),
                    dummyServiceItem(id = "116"),
                    dummyServiceItem(id = "117"),
                    dummyServiceItem(id = "118"),
                    dummyServiceItem(id = "119")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get last page of list THEN return the last page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "100")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "105"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyServiceItem(id = "100"),
                    dummyServiceItem(id = "101"),
                    dummyServiceItem(id = "102"),
                    dummyServiceItem(id = "103"),
                    dummyServiceItem(id = "104"),
                    dummyServiceItem(id = "105")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get not last page of list THEN return isLastPage false`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "100")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "105")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "106")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "107")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "108")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "109")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "110"))
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
    fun `GIVEN product list WHEN get last page of list THEN return isLastPage true`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "100")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "105"))
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
                dummyProduct(
                    kind = ProductKind.SERVICE,
                    item = dummyService(id = "100", distanceInMeters = 100)
                ),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "101")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "102")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "103")),
                dummyProduct(kind = ProductKind.SERVICE, item = dummyService(id = "104")),
                dummyProduct(
                    kind = ProductKind.SERVICE,
                    item = dummyService(id = "105", distanceInMeters = 200)
                )
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
    fun `GIVEN product list WHEN item click once third THEN don't show ad`() {
        givenProductList(listOf(dummyProduct()))

        viewModel.itemClicked()
        val isShowingAd = viewModel.isShowingAd().test()

        isShowingAd
            .assertValue(false)
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

    // TODO: sorting tests

    private fun givenProductList(productList: List<ProductRemoteModel>) {
        whenever(productRepository.getProduct()).thenReturn(Observable.just(productList))
    }

    private fun dummyServiceItem(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "100.00 €",
        name: String = "Pariatur",
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100,
        category: String = "Leisure",
        closeDay: String = "Monday",
        minimumAge: Int = 18
    ): ServiceItemModel {
        return ServiceItemModel(
            id = id,
            image = image,
            price = price,
            name = name,
            description = description,
            distanceInMeters = distanceInMeters,
            category = category,
            closeDay = closeDay,
            minimumAge = minimumAge
        )
    }
}
