package com.monzo.androidtest.di

import android.content.Context
import com.monzo.androidtest.GuardianApp
import com.monzo.androidtest.ui.customview.FavoriteView
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * dependencies needed in the app
 */
@Singleton
@Component(
        modules = [AndroidSupportInjectionModule::class,
            UseCaseModule::class,
            NetworkModule::class,
            RepositoryModule::class,
            ManagerModule::class,
            GuardianActivityModule::class
        ]
)
interface GuardianAppComponent : AndroidInjector<GuardianApp> {
    fun inject(favoriteView: FavoriteView)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): GuardianAppComponent
    }
}