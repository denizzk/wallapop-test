package com.dkarakaya.wallapoptest.repository.di

import com.dkarakaya.wallapoptest.model.remote.ProductRemoteModel
import com.dkarakaya.wallapoptest.repository.ProductRepository
import com.dkarakaya.wallapoptest.repository.gsonadapter.ProductDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class RepositoryModule {

    @Provides
    fun provideProductDeserializer(): Gson {
        return GsonBuilder().registerTypeAdapter(
            ProductRemoteModel::class.java,
            ProductDeserializer()
        ).create()
    }

    @Provides
    fun provideProductRetrofitInstance(productDeserializer: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ProductRepository.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(productDeserializer))
            .build()
    }

    @Provides
    fun provideProductRepository(retrofit: Retrofit): ProductRepository {
        return retrofit.create(ProductRepository::class.java)
    }
}
