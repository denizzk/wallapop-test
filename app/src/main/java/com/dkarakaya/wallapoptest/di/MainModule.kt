package com.dkarakaya.wallapoptest.di

import com.dkarakaya.wallapoptest.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainModule {

    @ContributesAndroidInjector(
        modules = [
            ProductListViewModelModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity
}
