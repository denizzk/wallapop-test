package com.dkarakaya.wallapoptest.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.wallapoptest.ProductViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface ProductViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel::class)
    fun bindProductViewModel(productViewModel: ProductViewModel): ViewModel

    companion object {
        @Provides
        fun provideProductViewModel(productRepository: ProductRepository): ProductViewModel {
            return ProductViewModel(
                productRepository = productRepository
            )
        }
    }
}
