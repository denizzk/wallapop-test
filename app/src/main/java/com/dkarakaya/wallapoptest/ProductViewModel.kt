package com.dkarakaya.wallapoptest

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.util.AdInitializer
import com.dkarakaya.core.util.RecyclerViewPaginator.Companion.PAGE_SIZE
import com.dkarakaya.wallapoptest.mapper.mapToProductItemModel
import com.dkarakaya.wallapoptest.model.ProductItemModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.min

class ProductViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val itemClickCountSubject = BehaviorSubject.createDefault(0)

    // inputs
    private val pageNumberInput = BehaviorSubject.create<Int>()
    private val distanceRangeInput = BehaviorSubject.createDefault<Pair<Int, Int>>(0 to 5)
    private val productIdInput = PublishSubject.create<String>()
    private val productListLoadedInput = PublishSubject.create<Unit>()
    private val itemClickedInput = BehaviorSubject.create<Unit>()

    // outputs
    private val productListOutput = BehaviorSubject.create<List<ProductItemModel>>()
    private val pagedListOutput = BehaviorSubject.create<List<ProductItemModel>>()
    private val isLastPageOutput = PublishSubject.create<Boolean>()
    private val distanceRangeOutput = PublishSubject.create<Pair<Int, Int>>()
    private val productOutput = BehaviorSubject.create<ProductItemModel>()
    private val isShowingAdOutput = BehaviorSubject.createDefault<Boolean>(false)

    init {
        // distinct and sorted by distance product list stream
        productRepository.getProduct()
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .distinct()
            .map { it.mapToProductItemModel() }
            .toSortedList { item1, item2 ->
                sortListByDistance(item1, item2)
            }
            .subscribeBy(
                onSuccess = productListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // paging stream
        Observables
            .combineLatest(pageNumberInput, productListOutput) { pageNumber, itemList ->
                val fromIndex = min(pageNumber * PAGE_SIZE, itemList.size)
                val toIndex = min(fromIndex + PAGE_SIZE, itemList.size)
                itemList.subList(fromIndex, toIndex)
            }
            .subscribeBy(
                onNext = pagedListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // is last page stream
        pagedListOutput
            .withLatestFrom(productListOutput) { pagedList, itemList ->
                pagedList.last() == itemList.last()
            }
            .subscribeBy(
                onNext = isLastPageOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // distance range stream
        Observables
            .combineLatest(distanceRangeInput, productListOutput) { range, itemList ->
                val firstItemDistance = itemList[range.first].distanceInMeters ?: 0
                val lastItemDistance = itemList[range.second].distanceInMeters ?: 0
                firstItemDistance to lastItemDistance
            }
            .distinctUntilChanged()
            .subscribeBy(
                onNext = distanceRangeOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // get product by given id stream
        productIdInput
            .delay(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .withLatestFrom(productListOutput) { id, products ->
                products.first { it.id == id }
            }
            .subscribeBy(
                onNext = productOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // item click count stream
        itemClickedInput
            .withLatestFrom(itemClickCountSubject) { _, count ->
                increaseClickCount(count)
            }
            .subscribeBy(
                onNext = itemClickCountSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // is showing ad stream
        itemClickCountSubject
            .map { isThirdClick(it) }
            .subscribeBy(
                onNext = isShowingAdOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Inputs
     */

    fun setPageNumber(pageNumber: Int) {
        pageNumberInput.onNext(pageNumber)
    }

    fun setFirstLastVisibleItems(range: Pair<Int, Int>) {
        distanceRangeInput.onNext(range)
    }

    fun setProductId(id: String) {
        productIdInput.onNext(id)
    }

    fun productListLoaded() {
        productListLoadedInput.onNext(Unit)
    }

    fun itemClicked() {
        itemClickedInput.onNext(Unit)
    }

    /**
     * Outputs
     */

    fun getProductList(): Observable<List<ProductItemModel>> = productListOutput

    fun getPagedList(): Observable<List<ProductItemModel>> = pagedListOutput

    fun isLastPage(): Observable<Boolean> = isLastPageOutput

    fun getDistanceRange(): Observable<Pair<Int, Int>> = distanceRangeOutput

    fun getProduct(): Observable<ProductItemModel> = productOutput

    fun isShowingAd(): Observable<Boolean> = isShowingAdOutput

    /**
     * Methods
     */

    private fun sortListByDistance(
        product1: ProductItemModel,
        product2: ProductItemModel
    ): Int {
        val distance1 = product1.distanceInMeters ?: Int.MAX_VALUE
        val distance2 = product2.distanceInMeters ?: Int.MAX_VALUE
        return distance1.compareTo(distance2)
    }

    private fun increaseClickCount(count: Int) = count + 1

    private fun isThirdClick(count: Int): Boolean {
        return count != 0 && count % AdInitializer.REQUIRED_CLICK_COUNT_TO_SHOW_AD == 0
    }
}
