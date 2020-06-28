package com.dkarakaya.consumer_goods.di

import com.dkarakaya.consumer_goods.ConsumerGoodsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ConsumerGoodsModule {

    @ContributesAndroidInjector(
        modules = [
            ConsumerGoodsViewModelModule::class
        ]
    )
    fun contributeConsumerGoodsActivity(): ConsumerGoodsActivity
}
