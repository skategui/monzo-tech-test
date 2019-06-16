package com.monzo.androidtest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.common.BaseViewModel
import com.monzo.androidtest.model.ArticleDetail
import com.monzo.androidtest.ui.utils.io
import com.monzo.androidtest.usecase.ArticleUseCase
import javax.inject.Inject

/**
 * Viewmodel responsible for the business logic associated to the article detail view
 */
class ArticleDetailViewModel
@Inject constructor(
    private val articleUseCase: ArticleUseCase,
    private val httpErrorUtils: HttpErrorUtils
) : BaseViewModel<ArticleDetailViewState>() {

    /**
     * loadArticleFromURL an article from its URL
     *  @param articleURL URL of the article to load
     */
    fun loadArticleFromURL(articleURL: String) {
        disposables.add(
            articleUseCase.getDetailArticle(articleURL)
                .doOnSubscribe { emitViewState(ArticleDetailViewState.ShowLoading) }
                .io()
                .subscribe(this::success, this::articlesErrors)
        )
    }

    /**
     * Emit the article to display
     *  @param article articles fetched
     */
    private fun success(article: ArticleDetail) {
        emitViewState(ArticleDetailViewState.RenderArticleDetail(article))
    }

    /**
     * Error thrown from the request. Emit the error with the message to the view
     */
    private fun articlesErrors(throwable: Throwable) {
        when (httpErrorUtils.hasLostInternet(throwable)) {
            true -> emitViewState(ArticleDetailViewState.NoInternet)
            false -> emitViewState(ArticleDetailViewState.Error(throwable.message))
        }
    }

    class Factory
    @Inject constructor(
        private val articleUseCase: ArticleUseCase,
        private val httpErrorUtils: HttpErrorUtils
    ) : ViewModelProvider.Factory {
        @SuppressWarnings("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticleDetailViewModel(articleUseCase, httpErrorUtils) as T
        }
    }
}