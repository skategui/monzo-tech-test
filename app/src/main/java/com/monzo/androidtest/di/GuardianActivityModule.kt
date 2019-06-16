package com.monzo.androidtest.di

import com.monzo.androidtest.ui.list.ArticlesListActivity
import com.monzo.androidtest.ui.list.ArticlesListModule
import com.monzo.androidtest.ui.detail.ArticleDetailActivity
import com.monzo.androidtest.ui.detail.ArticleDetailModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class GuardianActivityModule {

    @ContributesAndroidInjector(modules = [ArticlesListModule::class])
    abstract fun contributeArticlesActivityInjector(): ArticlesListActivity

    @ContributesAndroidInjector(modules = [ArticleDetailModule::class])
    abstract fun contributearticleDetailActivityInjector(): ArticleDetailActivity
}