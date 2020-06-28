package com.dkarakaya.wallapoptest.di

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.wallapoptest.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainModule {

    @ContributesAndroidInjector(
        modules = [ProductListViewModelModule::class]
    )
    fun contributeMainActivity(): MainActivity

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
