package com.dkarakaya.service

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.util.AdInitializer
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.service.details.ServiceDetailsFragment
import com.dkarakaya.service.details.ServiceDetailsFragment.Companion.TAG_SERVICEDETAILSFRAGMENT
import com.dkarakaya.service.model.ServiceItemModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_service.*
import timber.log.Timber
import javax.inject.Inject

class ServiceActivity : DaggerAppCompatActivity(R.layout.activity_service) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ServiceViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ServiceViewModel::class.java)
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
        val controller = ServiceController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
        }
        showCars(controller)
        viewModel.itemClicked() // workaround for showing the ad on first third click
        controller.serviceClickListener = { item ->
            viewModel.itemClicked()
            showDetails(item)
        }
    }

    private fun showCars(controller: ServiceController) {
        viewModel.getServiceList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { controller.services = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun showDetails(item: ServiceItemModel) {
        if (isShowingAd) {
            runAdEvents(item)
            adInitializer.showInterstitialAd()
        } else {
            Timber.e("The interstitial ad wasn't loaded yet.")
            val newInstance = ServiceDetailsFragment.newInstance(item = item)
            newInstance.show(supportFragmentManager, TAG_SERVICEDETAILSFRAGMENT)
        }
    }

    private fun runAdEvents(item: ServiceItemModel) {
        adInitializer.interstitialAd.adListener = object : AdListener() {
            // If user clicks on the ad and then presses the back, s/he is directed to DetailsFragment.
            override fun onAdClicked() {
                super.onAdOpened()
                adInitializer.interstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailsFragment.
            override fun onAdClosed() {
                val newInstance = ServiceDetailsFragment.newInstance(item = item)
                newInstance.show(supportFragmentManager, TAG_SERVICEDETAILSFRAGMENT)
                adInitializer.interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
