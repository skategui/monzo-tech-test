package com.monzo.androidtest.di


import com.monzo.androidtest.repo.ArticleRepository
import com.monzo.androidtest.repo.ArticleRepositoryImpl
import dagger.Binds
import dagger.Module


@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsArticleRepository(articleRepository: ArticleRepositoryImpl): ArticleRepository


}