package com.dkarakaya.consumer_goods

import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.await
import com.dkarakaya.test_utils.ProductFactory
import com.dkarakaya.test_utils.ProductFactory.dummyConsumerGoods
import com.dkarakaya.test_utils.ProductFactory.dummyProduct
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Test

class ConsumerGoodsViewModelTest {

    private val productRepository = mock<ProductRepository>()
    private val viewModel by lazy {
        ConsumerGoodsViewModel(
            productRepository = productRepository
        )
    }

    @Test
    fun `GIVEN product list WHEN get consumer goods list THEN return the consumer goods items from the list`() {
        val car = dummyConsumerGoods(id = "123", distanceInMeters = 100)
        val consumerGoods = dummyConsumerGoods(id = "345")
        val service = ProductFactory.dummyService(id = "567")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CAR, item = car),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods),
                dummyProduct(kind = ProductKind.SERVICE, item = service)
            )
        )

        val getProductList = viewModel.getConsumerGoodsList().test().await(1)

        getProductList
            .assertValue(mutableListOf(dummyConsumerGoodsItem(id = "345")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list with duplicated consumer goods WHEN get consumer goods list THEN return the list without duplicated consumer goods`() {
        val consumerGoods1 = dummyConsumerGoods(id = "123")
        val consumerGoods2 = dummyConsumerGoods(id = "123")
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods1),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods2)
            )
        )

        val getProductList = viewModel.getConsumerGoodsList().test().await(1)

        getProductList
            .assertValue(listOf(dummyConsumerGoodsItem(id = "123")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get consumer goods list THEN return the sorted consumer goods item list by ascending distance`() {
        val consumerGoods1 = dummyConsumerGoods(id = "123", distanceInMeters = 100)
        val consumerGoods2 = dummyConsumerGoods(id = "345", distanceInMeters = 200)
        val consumerGoods3 = dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods1),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods2),
                dummyProduct(kind = ProductKind.CONSUMER_GOODS, item = consumerGoods3)
            )
        )

        val getProductList = viewModel.getConsumerGoodsList().test().await(3)

        getProductList
            .assertValue(
                listOf(
                    dummyConsumerGoodsItem(id = "123", distanceInMeters = 100),
                    dummyConsumerGoodsItem(id = "567", distanceInMeters = 150),
                    dummyConsumerGoodsItem(id = "345", distanceInMeters = 200)
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
                    dummyConsumerGoodsItem(id = "100"),
                    dummyConsumerGoodsItem(id = "101"),
                    dummyConsumerGoodsItem(id = "102"),
                    dummyConsumerGoodsItem(id = "103"),
                    dummyConsumerGoodsItem(id = "104"),
                    dummyConsumerGoodsItem(id = "105"),
                    dummyConsumerGoodsItem(id = "106"),
                    dummyConsumerGoodsItem(id = "107"),
                    dummyConsumerGoodsItem(id = "108"),
                    dummyConsumerGoodsItem(id = "109")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get second page of list THEN return the second page of item list`() {
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
                    dummyConsumerGoodsItem(id = "110"),
                    dummyConsumerGoodsItem(id = "111"),
                    dummyConsumerGoodsItem(id = "112"),
                    dummyConsumerGoodsItem(id = "113"),
                    dummyConsumerGoodsItem(id = "114"),
                    dummyConsumerGoodsItem(id = "115"),
                    dummyConsumerGoodsItem(id = "116"),
                    dummyConsumerGoodsItem(id = "117"),
                    dummyConsumerGoodsItem(id = "118"),
                    dummyConsumerGoodsItem(id = "119")
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
                    dummyConsumerGoodsItem(id = "100"),
                    dummyConsumerGoodsItem(id = "101"),
                    dummyConsumerGoodsItem(id = "102"),
                    dummyConsumerGoodsItem(id = "103"),
                    dummyConsumerGoodsItem(id = "104"),
                    dummyConsumerGoodsItem(id = "105")
                )
            )
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list WHEN get not last page of list THEN return isLastPage false`() {
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
    fun `GIVEN product list WHEN get last page of list THEN return isLastPage true`() {
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
    fun `GIVEN product list WHEN item click thrice third THEN show ad`() {
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

    private fun dummyConsumerGoodsItem(
        id: String = "123",
        image: String = "https://test.jpg",
        price: String = "10.00 €",
        name: String = "Ball",
        description: String = "Officia excepteur exercitation laborum tempor anim.",
        distanceInMeters: Int = 100,
        category: String = "Toy",
        color: String = "Pink"
    ): ConsumerGoodsItemModel {
        return ConsumerGoodsItemModel(
            id = id,
            image = image,
            price = price,
            name = name,
            description = description,
            distanceInMeters = distanceInMeters,
            category = category,
            color = color
        )
    }
}
