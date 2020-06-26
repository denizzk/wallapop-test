package com.dkarakaya.car.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.car.CarViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface CarViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CarViewModel::class)
    fun bindCarViewModel(carViewModel: CarViewModel): ViewModel

    companion object {
        @Provides
        fun provideCarViewModel(productRepository: ProductRepository): CarViewModel {
            return CarViewModel(
                productRepository = productRepository
            )
        }
    }
}
