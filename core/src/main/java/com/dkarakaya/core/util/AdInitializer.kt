package com.dkarakaya.core.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

class AdInitializer {

    lateinit var interstitialAd: InterstitialAd

    fun initInterstitialAd(context: Context) {
        interstitialAd = InterstitialAd(context)
        MobileAds.initialize(context)
        interstitialAd.adUnitId = INTERSTITIAL_AD_ID
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    fun showInterstitialAd() {
        interstitialAd.show()
    }

    companion object {
        const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REQUIRED_CLICK_COUNT_TO_SHOW_AD = 3
    }
}
