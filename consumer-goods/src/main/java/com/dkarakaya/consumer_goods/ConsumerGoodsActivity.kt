package com.dkarakaya.consumer_goods

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.consumer_goods.details.ConsumerGoodsDetailsFragment
import com.dkarakaya.consumer_goods.details.ConsumerGoodsDetailsFragment.Companion.TAG_CONSUMERGOODSDETAILSFRAGMENT
import com.dkarakaya.consumer_goods.model.ConsumerGoodsItemModel
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
import kotlinx.android.synthetic.main.activity_consumer_goods.*
import timber.log.Timber
import javax.inject.Inject

class ConsumerGoodsActivity : DaggerAppCompatActivity(R.layout.activity_consumer_goods) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ConsumerGoodsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ConsumerGoodsViewModel::class.java)
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
        val controller = ConsumerGoodsController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
        }
        showConsumerGoods(controller)
        viewModel.itemClicked() // workaround for showing the ad on first third click
        controller.consumerGoodsClickListener = { item ->
            viewModel.itemClicked()
            showDetails(item)
        }
    }

    private fun showConsumerGoods(controller: ConsumerGoodsController) {
        viewModel.getConsumerGoodsList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { controller.consumerGoods = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun showDetails(item: ConsumerGoodsItemModel) {
        if (isShowingAd) {
            runAdEvents(item)
            adInitializer.showInterstitialAd()
        } else {
            Timber.e("The interstitial ad wasn't loaded yet.")
            val newInstance = ConsumerGoodsDetailsFragment.newInstance(item = item)
            newInstance.show(supportFragmentManager, TAG_CONSUMERGOODSDETAILSFRAGMENT)
        }
    }

    private fun runAdEvents(item: ConsumerGoodsItemModel) {
        adInitializer.interstitialAd.adListener = object : AdListener() {
            // If user clicks on the ad and then presses the back, s/he is directed to DetailsFragment.
            override fun onAdClicked() {
                super.onAdOpened()
                adInitializer.interstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailsFragment.
            override fun onAdClosed() {
                val newInstance = ConsumerGoodsDetailsFragment.newInstance(item = item)
                newInstance.show(supportFragmentManager, TAG_CONSUMERGOODSDETAILSFRAGMENT)
                adInitializer.interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
