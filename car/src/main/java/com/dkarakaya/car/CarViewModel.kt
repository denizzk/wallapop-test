package com.dkarakaya.car

import androidx.lifecycle.ViewModel
import com.dkarakaya.car.mapper.mapToCarItemModel
import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.core.sorting.SortingType
import com.dkarakaya.core.util.AdInitializer.Companion.REQUIRED_CLICK_COUNT_TO_SHOW_AD
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

class CarViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val itemClickCountSubject = BehaviorSubject.createDefault(0)

    // inputs
    private val sortingTypeInput = PublishSubject.create<SortingType>()
    private val itemClickedInput = BehaviorSubject.create<Unit>()

    // outputs
    private val carListOutput = BehaviorSubject.create<List<CarItemModel>>()
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

    fun setSortingType(type: SortingType) {
        sortingTypeInput.onNext(type)
    }

    fun itemClicked() {
        itemClickedInput.onNext(Unit)
    }

    /**
     * Outputs
     */

    fun getCarList(): Observable<List<CarItemModel>> = carListOutput

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

    private fun increaseClickCount(count: Int) = count + 1

    private fun isThirdClick(count: Int): Boolean {
        return count != 0 && count % REQUIRED_CLICK_COUNT_TO_SHOW_AD == 0
    }
}
