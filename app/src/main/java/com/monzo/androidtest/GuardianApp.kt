package com.monzo.androidtest


import android.app.Activity
import android.app.Application
import com.monzo.androidtest.di.DaggerGuardianAppComponent
import com.monzo.androidtest.di.GuardianAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

// App , init dagger and the AndroidInjector
class GuardianApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        DaggerGuardianAppComponent.builder()
                .context(this)
                .build()
                .also { guardianAppComponent = it }
        guardianAppComponent.inject(this)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    companion object {
        @JvmStatic
        lateinit var guardianAppComponent: GuardianAppComponent
    }
}