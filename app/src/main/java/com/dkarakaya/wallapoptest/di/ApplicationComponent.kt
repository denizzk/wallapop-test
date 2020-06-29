package com.dkarakaya.wallapoptest.di

import com.dkarakaya.car.di.CarModule
import com.dkarakaya.consumer_goods.di.ConsumerGoodsModule
import com.dkarakaya.core.di.BuilderModule
import com.dkarakaya.core.repository.di.RepositoryModule
import com.dkarakaya.service.di.ServiceModule
import com.dkarakaya.wallapoptest.BaseApplication
import com.dkarakaya.wallapoptest.sorting.di.SortingFragmentModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        RepositoryModule::class,
        BuilderModule::class,
        SortingFragmentModule::class,
        ProductModule::class,
        CarModule::class,
        ConsumerGoodsModule::class,
        ServiceModule::class
    ]
)
@Singleton
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: BaseApplication): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(instance: BaseApplication?) = Unit
}
