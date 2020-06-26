package com.dkarakaya.car.di

import com.dkarakaya.car.CarActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CarModule {

    @ContributesAndroidInjector(
        modules = [
            CarViewModelModule::class
        ]
    )
    fun contributeCarActivity(): CarActivity
}
