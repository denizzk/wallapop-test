package com.dkarakaya.consumer_goods

import androidx.lifecycle.ViewModel
import com.dkarakaya.consumer_goods.mapper.mapToConsumerGoodsItemModel
import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.repository.ProductRepository
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

class ConsumerGoodsViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val itemClickCountSubject = BehaviorSubject.createDefault(0)

    // inputs
    private val sortingTypeInput = PublishSubject.create<SortingType>()
    private val itemClickedInput = BehaviorSubject.create<Unit>()

    // outputs
    private val consumerGoodsListOutput = BehaviorSubject.create<List<ConsumerGoodsItemModel>>()
    private val isShowingAdOutput = BehaviorSubject.createDefault<Boolean>(false)

    init {
        // distinct and sorted by distance car list stream
        productRepository.getProduct()
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .filter { it.kind == ProductKind.CONSUMER_GOODS }
            .distinct()
            .map { it.mapToConsumerGoodsItemModel() }
            .toSortedList { item1, item2 ->
                sortList(item1, item2)
            }
            .subscribeBy(
                onSuccess = consumerGoodsListOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // sort list by sorting type
        sortingTypeInput
            .withLatestFrom(consumerGoodsListOutput) { type, itemList ->
                sortListBy(type, itemList)
            }
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onNext = consumerGoodsListOutput::onNext,
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

    fun getConsumerGoodsList(): Observable<List<ConsumerGoodsItemModel>> = consumerGoodsListOutput

    fun isShowingAd(): Observable<Boolean> = isShowingAdOutput

    /**
     * Methods
     */

    private fun sortList(
        product1: ConsumerGoodsItemModel,
        product2: ConsumerGoodsItemModel
    ): Int {
        val distance1 = product1.distanceInMeters ?: Int.MAX_VALUE
        val distance2 = product2.distanceInMeters ?: Int.MAX_VALUE
        return distance1.compareTo(distance2)
    }

    private fun sortListBy(
        type: SortingType?,
        carList: List<ConsumerGoodsItemModel>
    ): List<ConsumerGoodsItemModel> {
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

    enum class SortingType {
        DISTANCE_ASC,
        DISTANCE_DESC,
        PRICE_ASC,
        PRICE_DESC
    }
}
