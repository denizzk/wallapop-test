package com.dkarakaya.car

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.car.details.CarDetailsFragment
import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.viewmodel.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_car.*
import timber.log.Timber
import javax.inject.Inject

class CarActivity : DaggerAppCompatActivity(R.layout.activity_car) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CarViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CarViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onStart() {
        super.onStart()

//        registerSubscriptions()

        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerSubscriptions() {
        viewModel.getCarList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { it.toString() },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun initRecyclerView() {
        val controller = CarController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
        }
        showCars(controller)
        controller.carClickListener = { car ->
            showDetails(car)
        }
    }

    private fun showCars(controller: CarController) {
        viewModel.getCarList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { controller.cars = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun showDetails(item: CarItemModel) {
        val newInstance = CarDetailsFragment.newInstance(item = item)
        newInstance.show(supportFragmentManager, TAG_CARDETAILSFRAGMENT)
    }

    companion object {
        const val TAG_CARDETAILSFRAGMENT = "CarDetailsFragment"
    }
}
