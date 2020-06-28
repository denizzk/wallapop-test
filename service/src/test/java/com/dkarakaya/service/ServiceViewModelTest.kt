package com.dkarakaya.service

import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.await
import com.dkarakaya.service.model.ServiceItemModel
import com.dkarakaya.test_utils.ProductFactory
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
        val car = ProductFactory.dummyCar(id = "123", distanceInMeters = 100)
        val consumerGoods = ProductFactory.dummyConsumerGoods(id = "345")
        val service = ProductFactory.dummyService(id = "567")
        givenProductList(
            listOf(
                ProductFactory.dummyProduct(kind = ProductKind.CAR, item = car),
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods
                ),
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service)
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
        val service1 = ProductFactory.dummyService(id = "123")
        val service2 = ProductFactory.dummyService(id = "123")
        givenProductList(
            listOf(
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service1),
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service2)
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
        val service1 = ProductFactory.dummyService(id = "123", distanceInMeters = 100)
        val service2 = ProductFactory.dummyService(id = "345", distanceInMeters = 200)
        val service3 = ProductFactory.dummyService(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service1),
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service2),
                ProductFactory.dummyProduct(kind = ProductKind.SERVICE, item = service3)
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
    fun `GIVEN product list WHEN item click twice third THEN don't show ad`() {
        givenProductList(listOf(ProductFactory.dummyProduct()))

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
        givenProductList(listOf(ProductFactory.dummyProduct()))

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
