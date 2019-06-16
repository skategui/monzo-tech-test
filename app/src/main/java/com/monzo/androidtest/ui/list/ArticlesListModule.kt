package com.monzo.androidtest.ui.list


import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.datastore.FavoriteDataStore
import com.monzo.androidtest.usecase.ArticleUseCase
import dagger.Module
import dagger.Provides

@Module
class ArticlesListModule {

    @Provides
    fun providesListarticlesViewModelFactory(
        articleUseCase: ArticleUseCase,
        httpErrorUtils: HttpErrorUtils,
        favoriteDataStore: FavoriteDataStore
    ): ViewModelProvider.Factory {
        return ListArticlesViewModel.Factory(articleUseCase, httpErrorUtils, favoriteDataStore)
    }

}