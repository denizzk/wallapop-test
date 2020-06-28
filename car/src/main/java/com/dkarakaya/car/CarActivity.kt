package com.dkarakaya.car

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.car.details.CarDetailsFragment
import com.dkarakaya.car.details.CarDetailsFragment.Companion.TAG_CARDETAILSFRAGMENT
import com.dkarakaya.car.model.CarItemModel
import com.dkarakaya.core.util.AdInitializer
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
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

    @Inject
    lateinit var adInitializer: AdInitializer
    private var isShowingAd = false

    override fun onStart() {
        super.onStart()
        registerSubscriptions()
        initRecyclerView()
        adInitializer.initInterstitialAd(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerSubscriptions() {
        viewModel.isShowingAd()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { isShowingAd = it },
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
        viewModel.itemClicked() // workaround for showing the ad on first third click
        controller.carClickListener = { item ->
            viewModel.itemClicked()
            showDetails(item)
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
        if (isShowingAd) {
            runAdEvents(item)
            adInitializer.showInterstitialAd()
        } else {
            Timber.e("The interstitial ad wasn't loaded yet.")
            val newInstance = CarDetailsFragment.newInstance(item = item)
            newInstance.show(supportFragmentManager, TAG_CARDETAILSFRAGMENT)
        }
    }

    private fun runAdEvents(item: CarItemModel) {
        adInitializer.interstitialAd.adListener = object : AdListener() {
            // If user clicks on the ad and then presses the back, s/he is directed to DetailsFragment.
            override fun onAdClicked() {
                super.onAdOpened()
                adInitializer.interstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailsFragment.
            override fun onAdClosed() {
                val newInstance = CarDetailsFragment.newInstance(item = item)
                newInstance.show(supportFragmentManager, TAG_CARDETAILSFRAGMENT)
                adInitializer.interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
