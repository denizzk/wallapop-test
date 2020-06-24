package com.dkarakaya.wallapoptest.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.wallapoptest.ProductListViewModel
import com.dkarakaya.wallapoptest.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface ProductListViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProductListViewModel::class)
    fun bindProductListViewModel(productListViewModel: ProductListViewModel): ViewModel

    companion object {
        @Provides
        fun provideTransactionViewModel(productRepository: ProductRepository): ProductListViewModel {
            return ProductListViewModel(
                productRepository = productRepository
            )
        }
    }
}
