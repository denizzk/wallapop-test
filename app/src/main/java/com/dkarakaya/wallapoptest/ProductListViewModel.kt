package com.dkarakaya.wallapoptest

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.sorting.SortingType
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
import javax.inject.Inject
import kotlin.math.min

class ProductListViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val sortingTypeSubject = BehaviorSubject.create<SortingType>()
    private val itemClickCountSubject = BehaviorSubject.createDefault(0)

    // inputs
    private val pageNumberInput = BehaviorSubject.createDefault<Int>(0)
    private val distanceRangeInput = BehaviorSubject.createDefault<Pair<Int, Int>>(0 to 5)
    private val sortingInput = PublishSubject.create<Unit>()
    private val productIdInput = PublishSubject.create<String>()
    private val productListLoadedInput = PublishSubject.create<Unit>()
    private val itemClickedInput = BehaviorSubject.create<Unit>()

    // outputs
    private val productListOutput = BehaviorSubject.create<List<ProductItemModel>>()
    private val pagedListOutput = BehaviorSubject.create<List<ProductItemModel>>()
    private val isLastPageOutput = PublishSubject.create<Boolean>()
    private val distanceRangeOutput = PublishSubject.create<Pair<Int, Int>>()
    private val sortedProductListOutput = BehaviorSubject.create<List<ProductItemModel>>()
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
            .combineLatest(pageNumberInput, productListOutput) { pageNumber, productList ->
                val fromIndex = min(pageNumber * PAGE_SIZE, productList.size)
                val toIndex = min(fromIndex + PAGE_SIZE, productList.size)
                productList.subList(fromIndex, toIndex)
            }
            .subscribeBy(
                onNext = pagedListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // is last page stream
        pagedListOutput
            .withLatestFrom(productListOutput) { pagedList, productList ->
                pagedList.last() == productList.last()
            }
            .subscribeBy(
                onNext = isLastPageOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // distance range stream
        Observables.combineLatest(distanceRangeInput, productListOutput) { range, productList ->
            val firstItemDistance = productList[range.first].distanceInMeters ?: 0
            val lastItemDistance = productList[range.second].distanceInMeters ?: 0
            Timber.e("$firstItemDistance to $lastItemDistance")
            firstItemDistance to lastItemDistance
        }
            .distinctUntilChanged()
            .subscribeBy(
                onNext = distanceRangeOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // get product by given id stream
        productListLoadedInput
            .withLatestFrom(productIdInput, productListOutput) { _, id, products ->
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

        // TODO: fix
        // sort list by sorting type stream
        sortingTypeSubject
            .withLatestFrom(productListOutput)
            .doOnNext { sortProductList(it.first, it.second) }
            .map { it.second }
            .doOnNext(productListOutput::onNext)
            .subscribeBy(
                onNext = productListOutput::onNext,
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

    fun setSortingType(type: SortingType) {
        sortingTypeSubject.onNext(type)
    }

    fun setSorting() {
        sortingInput.onNext(Unit)
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

    fun getSortedProductList(): Observable<List<ProductItemModel>> = sortedProductListOutput

    fun getSortingType(): Observable<SortingType> = sortingTypeSubject


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

    private fun sortProductList(
        type: SortingType?,
        productList: List<ProductItemModel>
    ): List<ProductItemModel> {
        return when (type) {
            SortingType.DISTANCE_ASC -> productList.sortedBy { it.distanceInMeters }
            SortingType.DISTANCE_DESC -> productList.sortedByDescending { it.distanceInMeters }
            SortingType.PRICE_ASC -> productList.sortedBy { it.price }
            SortingType.PRICE_DESC -> productList.sortedByDescending { it.price }
            else -> productList.sortedBy { it.distanceInMeters }
        }
    }

    private fun increaseClickCount(count: Int) = count + 1

    private fun isThirdClick(count: Int): Boolean {
        return count != 0 && count % AdInitializer.REQUIRED_CLICK_COUNT_TO_SHOW_AD == 0
    }
}
