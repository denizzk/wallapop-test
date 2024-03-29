package com.dkarakaya.wallapoptest

import android.content.Intent
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.car.CarActivity
import com.dkarakaya.car.details.CarDetailsFragment
import com.dkarakaya.consumer_goods.ConsumerGoodsActivity
import com.dkarakaya.consumer_goods.details.ConsumerGoodsDetailsFragment
import com.dkarakaya.core.model.ProductKind
import com.dkarakaya.core.util.AdInitializer
import com.dkarakaya.core.util.RecyclerViewPaginator
import com.dkarakaya.core.util.getSpannable
import com.dkarakaya.core.util.setTextSize
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.service.ServiceActivity
import com.dkarakaya.service.details.ServiceDetailsFragment
import com.dkarakaya.wallapoptest.mapper.mapToCarItemModel
import com.dkarakaya.wallapoptest.mapper.mapToConsumerGoodsItemModel
import com.dkarakaya.wallapoptest.mapper.mapToServiceItemModel
import com.dkarakaya.wallapoptest.model.ProductItemModel
import com.dkarakaya.wallapoptest.product.ProductController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class ProductActivity : DaggerAppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    private var isLastPageNumber: Boolean = false

    private lateinit var textDistanceRange: TextView

    @Inject
    lateinit var adInitializer: AdInitializer
    private var isShowingAd = false

    override fun onStart() {
        super.onStart()
        Timber.plant(Timber.DebugTree())
        registerSubscriptions()
        registerListeners()
        render()
        openDeepLink()
        adInitializer.initInterstitialAd(this)
    }

    override fun onResume() {
        super.onResume()
        // workaround to load the first paged list when returned back from another activity
        viewModel.setPageNumber(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerSubscriptions() {
        viewModel.getDistanceRange()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = ::setLayoutDistance,
                onError = Timber::e
            )
            .addTo(disposables)

        viewModel.getProduct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = ::showDetails,
                onError = Timber::e
            )
            .addTo(disposables)

        viewModel.isShowingAd()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { isShowingAd = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun setLayoutDistance(it: Pair<Int, Int>) {
        layout_distance.isVisible = true
        val textSize = resources.getDimensionPixelSize(R.dimen.text_size_M)
        textDistanceRange.text = getSpannable(
            R.string.distance_range_info,
            it.first.toString().setTextSize(textSize),
            it.second.toString().setTextSize(textSize)
        )
    }

    private fun registerListeners() {
        buttonCar.setOnClickListener {
            val intent = Intent(this, CarActivity::class.java)
            startActivity(intent)
        }

        buttonConsumerGoods.setOnClickListener {
            val intent = Intent(this, ConsumerGoodsActivity::class.java)
            startActivity(intent)
        }

        buttonService.setOnClickListener {
            val intent = Intent(this, ServiceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun render() {
        textDistanceRange = findViewById(R.id.textDistanceRange)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val controller = ProductController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
            addOnScrollListener(object : RecyclerViewPaginator(recyclerView) {
                override val isLastPage: Boolean
                    get() = isLastPageNumber

                override fun loadPage(pageNumber: Int) {
                    viewModel.setPageNumber(pageNumber)
                }

                override fun distanceRange(range: Pair<Int, Int>) {
                    viewModel.setFirstLastVisibleItems(range)
                }
            })
        }
        showProducts(controller)
        viewModel.itemClicked() // workaround for showing the ad on first third click
        controller.productClickListener = { item ->
            viewModel.itemClicked()
            showDetails(item)
        }
    }

    private fun showProducts(controller: ProductController) {
        viewModel.getPagedList()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    controller.products += it
                },
                onError = Timber::e
            )
            .addTo(disposables)

        viewModel.isLastPage()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { isLastPageNumber = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun showDetails(item: ProductItemModel) {
        if (isShowingAd) {
            runAdEvents(item)
            adInitializer.showInterstitialAd()
        } else {
            val newInstance = itemDetailsFragment(item)
            newInstance.show(supportFragmentManager, TAG_ITEMDETAILSFRAGMENT)
        }
    }

    private fun runAdEvents(item: ProductItemModel) {
        adInitializer.interstitialAd.adListener = object : AdListener() {
            // If user clicks on the ad and then presses the back, s/he is directed to DetailsFragment.
            override fun onAdClicked() {
                super.onAdOpened()
                adInitializer.interstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailsFragment.
            override fun onAdClosed() {
                val newInstance = itemDetailsFragment(item)
                newInstance.show(supportFragmentManager, TAG_ITEMDETAILSFRAGMENT)
                adInitializer.interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun itemDetailsFragment(item: ProductItemModel): BottomSheetDialogFragment {
        return when (item.kind) {
            ProductKind.CAR -> CarDetailsFragment.newInstance(item = item.mapToCarItemModel())
            ProductKind.CONSUMER_GOODS -> ConsumerGoodsDetailsFragment.newInstance(item = item.mapToConsumerGoodsItemModel())
            ProductKind.SERVICE -> ServiceDetailsFragment.newInstance(item = item.mapToServiceItemModel())
        }
    }

    private fun openDeepLink() {
        val deepLink = intent.data
        val itemId = deepLink?.pathSegments?.get(0)
        if (itemId != null) {
            viewModel.setProductId(itemId)
        }
    }

    companion object {
        const val TAG_ITEMDETAILSFRAGMENT = "ItemDetailsFragment"
    }
}
