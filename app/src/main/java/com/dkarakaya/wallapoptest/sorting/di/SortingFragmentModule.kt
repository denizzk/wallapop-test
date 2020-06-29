package com.dkarakaya.wallapoptest.sorting.di

import com.dkarakaya.wallapoptest.di.ProductViewModelModule
import com.dkarakaya.wallapoptest.sorting.SortingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SortingFragmentModule {

    @ContributesAndroidInjector(
        modules = [ProductViewModelModule::class]
    )
    fun contributeSortingFragment(): SortingFragment
}
