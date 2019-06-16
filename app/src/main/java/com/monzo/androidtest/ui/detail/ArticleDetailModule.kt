package com.monzo.androidtest.ui.detail

import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.usecase.ArticleUseCase
import dagger.Module
import dagger.Provides

@Module
class ArticleDetailModule {

    @Provides
    fun providesArticleDetailViewModelFactory(
        articleUseCase: ArticleUseCase,
        httpErrorUtils: HttpErrorUtils
    ): ViewModelProvider.Factory {
        return ArticleDetailViewModel.Factory(articleUseCase, httpErrorUtils)
    }
}