package com.monzo.androidtest.di

import com.monzo.androidtest.usecase.ArticleUseCase
import com.monzo.androidtest.usecase.ArticleUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class UseCaseModule {

    @Binds
    abstract fun bindArticlesUseCase(articleUseCaseImpl: ArticleUseCaseImpl): ArticleUseCase


}