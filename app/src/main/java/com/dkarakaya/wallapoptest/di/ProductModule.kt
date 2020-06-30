package com.dkarakaya.wallapoptest.di

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.wallapoptest.ProductActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ProductModule {

    @ContributesAndroidInjector(
        modules = [
            ProductViewModelModule::class
        ]
    )
    fun contributeProductActivity(): ProductActivity

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
