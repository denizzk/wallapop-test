package com.dkarakaya.wallapoptest.di

import com.dkarakaya.wallapoptest.BaseApplication
import com.dkarakaya.wallapoptest.repository.di.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        MainModule::class,
        RepositoryModule::class
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
