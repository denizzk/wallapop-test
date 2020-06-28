package com.dkarakaya.core.di

import com.dkarakaya.core.util.AdInitializer
import dagger.Module
import dagger.Provides

@Module
class BuilderModule {

    @Provides
    fun provideAdInitializer(): AdInitializer {
        return AdInitializer()
    }
}
