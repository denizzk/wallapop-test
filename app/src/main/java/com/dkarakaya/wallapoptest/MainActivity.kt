package com.dkarakaya.wallapoptest

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.car.CarActivity
import com.dkarakaya.car.details.CarDetailsFragment
import com.dkarakaya.car.details.CarDetailsFragment.Companion.TAG_CARDETAILSFRAGMENT
import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.util.uppercaseFirstLetterAndLowercaseRest
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.wallapoptest.model.ProductItem
import com.dkarakaya.wallapoptest.model.ProductItemModel
import com.dkarakaya.wallapoptest.productlist.ProductController
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProductListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProductListViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        registerSubscriptions()
        registerListeners()
        initRecyclerView()
        openDeepLink()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerSubscriptions() {
        viewModel.getProduct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { showDetails(it) },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun registerListeners() {
        buttonCar.setOnClickListener {
            val intent = Intent(this, CarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val controller = ProductController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
        }
        showProducts(controller)
        controller.productClickListener = { product ->
            showDetails(product)
        }
    }

    private fun showProducts(controller: ProductController) {
        viewModel.getProductList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    controller.products = it
                    viewModel.productListLoaded()
                },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun showDetails(item: ProductItemModel) {
        val newInstance = when (item.kind) {
            ProductKind.CAR -> CarDetailsFragment.newInstance(item = item.mapToCarItem())
            ProductKind.CONSUMER_GOODS -> TODO()
            ProductKind.SERVICE -> TODO()
        }
        if (!newInstance.isVisible) {
            newInstance.show(supportFragmentManager, TAG_CARDETAILSFRAGMENT)
        }
    }

    private fun openDeepLink() {
        val deepLink = intent.data
        val itemId = deepLink?.pathSegments?.get(0)
        if (itemId != null) {
            viewModel.setProductId(itemId)
        }
    }
}

private fun ProductItemModel.mapToCarItem(): CarItemModel {
    val carItem = this.item as ProductItem.Car
    return CarItemModel(
        id = this.id,
        image = this.image,
        price = this.price,
        name = this.name.uppercaseFirstLetterAndLowercaseRest(),
        description = this.description,
        distanceInMeters = this.distanceInMeters,
        motor = carItem.motor.uppercaseFirstLetterAndLowercaseRest(),
        gearbox = carItem.gearbox.uppercaseFirstLetterAndLowercaseRest(),
        brand = carItem.brand.uppercaseFirstLetterAndLowercaseRest(),
        km = carItem.km
    )
}
