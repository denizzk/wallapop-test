package com.dkarakaya.consumer_goods.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.consumer_goods.ConsumerGoodsViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface ConsumerGoodsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ConsumerGoodsViewModel::class)
    fun bindConsumerGoodsViewModel(consumerGoodsViewModel: ConsumerGoodsViewModel): ViewModel

    companion object {
        @Provides
        fun provideConsumerGoodsViewModel(productRepository: ProductRepository): ConsumerGoodsViewModel {
            return ConsumerGoodsViewModel(
                productRepository = productRepository
            )
        }
    }
}
