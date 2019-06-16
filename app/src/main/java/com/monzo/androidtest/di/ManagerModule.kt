package com.monzo.androidtest.di

import com.monzo.androidtest.datastore.FavoriteDataStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ManagerModule {

    @Singleton
    @Provides
    fun providesProfileManager() = FavoriteDataStore()
}