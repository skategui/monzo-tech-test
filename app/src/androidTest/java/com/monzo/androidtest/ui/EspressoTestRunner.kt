package com.monzo.androidtest.ui

import android.app.Instrumentation
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.monzo.androidtest.GuardianApp

class EspressoTestRunner: AndroidJUnitRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?)
            = Instrumentation.newApplication(GuardianApp::class.java, context)

}