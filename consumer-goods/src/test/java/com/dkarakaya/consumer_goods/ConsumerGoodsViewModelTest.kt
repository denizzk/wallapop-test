package com.dkarakaya.consumer_goods

import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.model.ProductRemoteModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.await
import com.dkarakaya.test_utils.ProductFactory
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

        val getProductList = viewModel.getConsumerGoodsList().test().await(1)

        getProductList
            .assertValue(mutableListOf(dummyConsumerGoodsItem(id = "345")))
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun `GIVEN product list with duplicated consumer goods WHEN get consumer goods list THEN return the list without duplicated consumer goods`() {
        val consumerGoods1 = ProductFactory.dummyConsumerGoods(id = "123")
        val consumerGoods2 = ProductFactory.dummyConsumerGoods(id = "123")
        givenProductList(
            listOf(
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods1
                ),
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods2
                )
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
        val consumerGoods1 = ProductFactory.dummyConsumerGoods(id = "123", distanceInMeters = 100)
        val consumerGoods2 = ProductFactory.dummyConsumerGoods(id = "345", distanceInMeters = 200)
        val consumerGoods3 = ProductFactory.dummyConsumerGoods(id = "567", distanceInMeters = 150)
        givenProductList(
            listOf(
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods1
                ),
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods2
                ),
                ProductFactory.dummyProduct(
                    kind = ProductKind.CONSUMER_GOODS,
                    item = consumerGoods3
                )
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
