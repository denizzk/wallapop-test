package com.dkarakaya.service.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.repository.ProductRepository
import com.dkarakaya.service.ServiceViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface ServiceViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ServiceViewModel::class)
    fun bindServiceViewModel(serviceViewModel: ServiceViewModel): ViewModel

    companion object {
        @Provides
        fun provideServiceViewModel(productRepository: ProductRepository): ServiceViewModel {
            return ServiceViewModel(
                productRepository = productRepository
            )
        }
    }
}
