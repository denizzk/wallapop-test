package com.dkarakaya.wallapoptest

import androidx.lifecycle.ViewModel
import com.dkarakaya.wallapoptest.mapper.mapToProductItemModel
import com.dkarakaya.wallapoptest.model.domain.ProductItemModel
import com.dkarakaya.wallapoptest.repository.ProductRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class ProductListViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    // inputs
    private val sortingTypeInput = PublishSubject.create<SortingType>()

    // outputs
    private val productListOutput = BehaviorSubject.create<List<ProductItemModel>>()
//    private val carItemListOutput = BehaviorSubject.create<List<Car>>()
//    private val consumerGoodsItemListOutput = BehaviorSubject.create<List<ConsumerGoods>>()
//    private val serviceItemListOutput = BehaviorSubject.create<List<Service>>()

    init {
        // distinct and sorted by distance product list stream
        productRepository.getProduct()
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .distinct()
            .map { it.mapToProductItemModel() }
            .toSortedList { product1, product2 ->
                sortList(product1, product2, SortingType.DISTANCE_ASC)
            }
            .subscribeBy(
                onSuccess = productListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // sort list by sorting type
        sortingTypeInput
            .withLatestFrom(productListOutput) { type, productList ->
                when (type) {
                    SortingType.DISTANCE_ASC -> productList.sortedBy { it.distanceInMeters }
                    SortingType.DISTANCE_DESC -> productList.sortedByDescending { it.distanceInMeters }
                    SortingType.PRICE_ASC -> productList.sortedBy { it.price }
                    SortingType.PRICE_DESC -> productList.sortedByDescending { it.price }
                }
            }
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onNext = productListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

//        // car item list stream
//        productListOutput
//            .flatMapIterable { it }
//            .filter { it.kind == "car" }
//            .toList()
//            .subscribeBy(
//                onSuccess = carItemListOutput::onNext,
//                onError = Timber::e
//            )
//            .addTo(disposables)
//
//        // consumer goods item list stream
//        productListOutput
//            .flatMapIterable { it }
//            .ofType<ConsumerGoods>()
//            .toSortedList { consumerGoods1, consumerGoods2 ->
//                consumerGoods1.distanceInMeters.compareTo(consumerGoods2.distanceInMeters)
//            }
//            .subscribeBy(
//                onSuccess = consumerGoodsItemListOutput::onNext,
//                onError = Timber::e
//            )
//            .addTo(disposables)
//
//        // service item list stream
//        productListOutput
//            .flatMapIterable { it }
//            .ofType<Service>()
//            .toSortedList { service1, service2 ->
//                service1.distanceInMeters.compareTo(service2.distanceInMeters)
//            }
//            .subscribeBy(
//                onSuccess = serviceItemListOutput::onNext,
//                onError = Timber::e
//            )
//            .addTo(disposables)
    }

    private fun sortList(
        product1: ProductItemModel,
        product2: ProductItemModel,
        sortingType: SortingType
    ): Int {
        when (sortingType) {
            SortingType.DISTANCE_ASC -> {
                val distance1 = product1.distanceInMeters ?: Int.MAX_VALUE
                val distance2 = product2.distanceInMeters ?: Int.MAX_VALUE
                return distance1.compareTo(distance2)
            }
            SortingType.DISTANCE_DESC -> {
                val distance1 = product1.distanceInMeters ?: Int.MAX_VALUE
                val distance2 = product2.distanceInMeters ?: Int.MAX_VALUE
                return distance2.compareTo(distance1)
            }
            SortingType.PRICE_ASC -> {
                val distance1 = product1.price
                val distance2 = product2.price
                return distance1.compareTo(distance2)
            }
            SortingType.PRICE_DESC -> {
                val distance1 = product1.price
                val distance2 = product2.price
                return distance2.compareTo(distance1)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Inputs
     */

    fun setSortingType(type: SortingType) {
        sortingTypeInput.onNext(type)
    }

    /**
     * Outputs
     */

    fun getProductList(): Observable<List<ProductItemModel>> = productListOutput
//    fun getCarList(): Observable<List<Car>> = carItemListOutput
//    fun getConsumerList(): Observable<List<ConsumerGoods>> = consumerGoodsItemListOutput

    /**
     * Methods
     */

    enum class SortingType {
        DISTANCE_ASC,
        DISTANCE_DESC,
        PRICE_ASC,
        PRICE_DESC
    }
}
