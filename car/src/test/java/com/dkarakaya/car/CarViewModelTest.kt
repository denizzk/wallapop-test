package com.dkarakaya.car

import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.await
import com.dkarakaya.test_utils.ProductFactory.dummyCar
import com.dkarakaya.test_utils.ProductFactory.dummyConsumerGoods
import com.dkarakaya.test_utils.ProductFactory.dummyProduct
import com.dkarakaya.test_utils.ProductFactory.dummyService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Test

class CarViewModelTest {

    private val productRepository = mock<ProductRepository>()
    private val viewModel by lazy {
        CarViewModel(
            productRepository = productRepository
        )
    }

    @Test
    fun `GIVEN product list WHEN get car list THEN return the car items from the list`() {
        val car = dummyCar(id = "123", distanceInMeters = 100)
        val consumerGoods = dummyConsumerGoods(id = "345")
        val service = dummyService(id = "567")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods),
                dummyProduct(kind = ProductKind.SERVICE, item = service)
            )
        )

        val getProductList = viewModel.getCarList().test().await(1)

        getProductList
            .assertValue(mutableListOf(dummyCarItem(id = "123")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list with duplicated cars WHEN get car list THEN return the list without duplicated cars`() {
        val car1 = dummyCar(id = "123")
        val car2 = dummyCar(id = "123")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car1),
                dummyProduct(kind = ProductKind.CAR, item = car2)
            )
        )

        val getProductList = viewModel.getCarList().test().await(1)

        getProductList
            .assertValue(listOf(dummyCarItem(id = "123")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get car list THEN return the sorted car item list by ascending distance`() {
        val car1 = dummyCar(id = "123", distanceInMeters = 100)
        val car2 = dummyCar(id = "345", distanceInMeters = 200)
        val car3 = dummyCar(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car1),
                dummyProduct(kind = ProductKind.CAR, item = car2),
                dummyProduct(kind = ProductKind.CAR, item = car3)
            )
        )

        val getProductList = viewModel.getCarList().test().await(3)

        getProductList
            .assertValue(
                listOf(
                    dummyCarItem(id = "123", distanceInMeters = 100),
                    dummyCarItem(id = "567", distanceInMeters = 150),
                    dummyCarItem(id = "345", distanceInMeters = 200)
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get first page of list THEN return the first page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "100")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "105")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "106")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "107")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "108")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "109")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "110")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "111"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyCarItem(id = "100"),
                    dummyCarItem(id = "101"),
                    dummyCarItem(id = "102"),
                    dummyCarItem(id = "103"),
                    dummyCarItem(id = "104"),
                    dummyCarItem(id = "105"),
                    dummyCarItem(id = "106"),
                    dummyCarItem(id = "107"),
                    dummyCarItem(id = "108"),
                    dummyCarItem(id = "109")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get second page of list THEN return the second page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "100")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "105")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "106")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "107")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "108")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "109")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "110")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "111")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "112")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "113")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "114")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "115")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "116")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "117")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "118")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "119"))
            )
        )

        viewModel.setPageNumber(1)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyCarItem(id = "110"),
                    dummyCarItem(id = "111"),
                    dummyCarItem(id = "112"),
                    dummyCarItem(id = "113"),
                    dummyCarItem(id = "114"),
                    dummyCarItem(id = "115"),
                    dummyCarItem(id = "116"),
                    dummyCarItem(id = "117"),
                    dummyCarItem(id = "118"),
                    dummyCarItem(id = "119")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get last page of list THEN return the last page of item list`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "100")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "105"))
            )
        )

        viewModel.setPageNumber(0)
        val getProductList = viewModel.getPagedList().test().await(2)

        getProductList
            .assertValue(
                mutableListOf(
                    dummyCarItem(id = "100"),
                    dummyCarItem(id = "101"),
                    dummyCarItem(id = "102"),
                    dummyCarItem(id = "103"),
                    dummyCarItem(id = "104"),
                    dummyCarItem(id = "105")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get not last page of list THEN return isLastPage false`() {
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "100")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "105")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "106")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "107")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "108")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "109")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "110"))
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
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "100")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "105"))
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
                    kind = ProductKind.CAR,
                    item = dummyCar(id = "100", distanceInMeters = 100)
                ),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "101")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "102")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "103")),
                dummyProduct(kind = ProductKind.CAR, item = dummyCar(id = "104")),
                dummyProduct(
                    kind = ProductKind.CAR,
                    item = dummyCar(id = "105", distanceInMeters = 200)
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

    private fun givenProductList(productList: List<ProductRemoteModel>) {
        whenever(productRepository.getProduct()).thenReturn(Observable.just(productList))
    }

    private fun dummyCarItem(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "1,000.00 €",
        name: String = "Duis",
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100,
        motor: String = "Gasoline",
        gearbox: String = "Manual",
        brand: String = "Irure",
        km: Int = 1234
    ): CarItemModel {
        return CarItemModel(
            id = id,
            image = image,
            price = price,
            name = name,
            description = description,
            distanceInMeters = distanceInMeters,
            motor = motor,
            gearbox = gearbox,
            brand = brand,
            km = km
        )
    }
}
