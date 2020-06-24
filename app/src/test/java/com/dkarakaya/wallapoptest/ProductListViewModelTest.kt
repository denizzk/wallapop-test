package com.dkarakaya.wallapoptest

import com.dkarakaya.wallapoptest.model.ConsumerGoods
import com.dkarakaya.wallapoptest.model.Product
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
    fun `GIVEN product list WHEN get product list THEN return the product list`() {
        givenProductList(
            listOf(
                Product(
                    kind = "consumer_goods",
                    item = ConsumerGoods(
                        id = "123",
                        image = "https://test.jpg",
                        price = "10€",
                        name = "ball",
                        color = "pink",
                        category = "toy",
                        description = "Officia excepteur exercitation laborum tempor anim.",
                        distanceInMeters = 100
                    )
                )
            )
        )

        val getProductList = viewModel.getProductList().test()

        getProductList
            .assertValue(
                listOf(
                    Product(
                        kind = "consumer_goods",
                        item = ConsumerGoods(
                            id = "123",
                            image = "https://test.jpg",
                            price = "10€",
                            name = "ball",
                            color = "pink",
                            category = "toy",
                            description = "Officia excepteur exercitation laborum tempor anim.",
                            distanceInMeters = 100
                        )
                    )
                )
            )
    }

    private fun givenProductList(productList: List<Product>) {
        whenever(productRepository.getProduct()).thenReturn(Observable.just(productList))
    }
}
