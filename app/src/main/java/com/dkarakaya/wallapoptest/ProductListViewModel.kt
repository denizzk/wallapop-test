package com.dkarakaya.wallapoptest

import androidx.lifecycle.ViewModel
import com.dkarakaya.wallapoptest.model.Product
import com.dkarakaya.wallapoptest.repository.ProductRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ProductListViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val productListOutput = BehaviorSubject.create<List<Product>>()

    init {

        productRepository.getProduct()
            .subscribeOn(Schedulers.io())
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

    /**
     * Outputs
     */

    fun getProductList(): Observable<List<Product>> = productListOutput

    /**
     * Methods
     */
}
