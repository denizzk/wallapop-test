package com.dkarakaya.wallapoptest

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.car.CarActivity
import com.dkarakaya.core.viewmodel.ViewModelFactory
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
//        registerSubscriptions()
        registerListeners()

        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerSubscriptions() {
        viewModel.getProductList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { it.toString() },
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
//            showDetails(product)
        }
    }

    private fun showProducts(controller: ProductController) {
        viewModel.getProductList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { controller.products = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }
}
