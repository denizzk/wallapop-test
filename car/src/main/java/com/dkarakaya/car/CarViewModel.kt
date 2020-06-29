package com.dkarakaya.car

import androidx.lifecycle.ViewModel
import com.dkarakaya.car.mapper.mapToCarItemModel
import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.sorting.SortingType
import com.dkarakaya.core.util.AdInitializer.Companion.REQUIRED_CLICK_COUNT_TO_SHOW_AD
import com.dkarakaya.core.util.RecyclerViewPaginator
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

class CarViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val itemClickCountSubject = BehaviorSubject.createDefault(0)

    // inputs
    private val pageNumberInput = BehaviorSubject.create<Int>()
    private val distanceRangeInput = BehaviorSubject.createDefault<Pair<Int, Int>>(0 to 5)
    private val itemClickedInput = BehaviorSubject.create<Unit>()
    private val sortingTypeInput = PublishSubject.create<SortingType>()

    // outputs
    private val carListOutput = BehaviorSubject.create<List<CarItemModel>>()
    private val pagedListOutput = BehaviorSubject.create<List<CarItemModel>>()
    private val isLastPageOutput = PublishSubject.create<Boolean>()
    private val distanceRangeOutput = PublishSubject.create<Pair<Int, Int>>()
    private val isShowingAdOutput = BehaviorSubject.createDefault<Boolean>(false)

    init {
        // distinct and sorted by distance car list stream
        productRepository.getProduct()
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .filter { it.kind == ProductKind.CAR }
            .distinct()
            .map { it.mapToCarItemModel() }
            .toSortedList { item1, item2 ->
                sortList(item1, item2)
            }
            .subscribeBy(
                onSuccess = carListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // paging stream
        Observables
            .combineLatest(pageNumberInput, carListOutput) { pageNumber, itemList ->
                val fromIndex = min(pageNumber * RecyclerViewPaginator.PAGE_SIZE, itemList.size)
                val toIndex = min(fromIndex + RecyclerViewPaginator.PAGE_SIZE, itemList.size)
                itemList.subList(fromIndex, toIndex)
            }
            .subscribeBy(
                onNext = pagedListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // is last page stream
        pagedListOutput
            .withLatestFrom(carListOutput) { pagedList, itemList ->
                pagedList.last() == itemList.last()
            }
            .subscribeBy(
                onNext = isLastPageOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // distance range stream
        Observables
            .combineLatest(distanceRangeInput, carListOutput) { range, itemList ->
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

        // sort list by sorting type
        sortingTypeInput
            .withLatestFrom(carListOutput) { type, carList ->
                sortListBy(type, carList)
            }
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onNext = carListOutput::onNext,
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

    fun itemClicked() {
        itemClickedInput.onNext(Unit)
    }

    fun setSortingType(type: SortingType) {
        sortingTypeInput.onNext(type)
    }

    /**
     * Outputs
     */

    fun getCarList(): Observable<List<CarItemModel>> = carListOutput

    fun getPagedList(): Observable<List<CarItemModel>> = pagedListOutput

    fun isLastPage(): Observable<Boolean> = isLastPageOutput

    fun getDistanceRange(): Observable<Pair<Int, Int>> = distanceRangeOutput

    fun isShowingAd(): Observable<Boolean> = isShowingAdOutput

    /**
     * Methods
     */

    private fun sortList(
        product1: CarItemModel,
        product2: CarItemModel
    ): Int {
        val distance1 = product1.distanceInMeters ?: Int.MAX_VALUE
        val distance2 = product2.distanceInMeters ?: Int.MAX_VALUE
        return distance1.compareTo(distance2)
    }

    private fun increaseClickCount(count: Int) = count + 1

    private fun isThirdClick(count: Int): Boolean {
        return count != 0 && count % REQUIRED_CLICK_COUNT_TO_SHOW_AD == 0
    }

    private fun sortListBy(
        type: SortingType?,
        carList: List<CarItemModel>
    ): List<CarItemModel> {
        return when (type) {
            SortingType.DISTANCE_ASC -> carList.sortedBy { it.distanceInMeters }
            SortingType.DISTANCE_DESC -> carList.sortedByDescending { it.distanceInMeters }
            SortingType.PRICE_ASC -> carList.sortedBy { it.price }
            SortingType.PRICE_DESC -> carList.sortedByDescending { it.price }
            else -> carList.sortedByDescending { it.distanceInMeters }
        }
    }
}
