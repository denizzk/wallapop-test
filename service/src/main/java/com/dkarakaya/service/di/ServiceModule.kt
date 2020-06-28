package com.dkarakaya.service.di

import com.dkarakaya.service.ServiceActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceModule {

    @ContributesAndroidInjector(
        modules = [
            ServiceViewModelModule::class
        ]
    )
    fun contributeServiceActivity(): ServiceActivity
}
