package com.monzo.androidtest.ui.detail

import com.monzo.androidtest.model.ArticleDetail


// states possible of the  article detail view
sealed class ArticleDetailViewState {
    class RenderArticleDetail(val article: ArticleDetail) : ArticleDetailViewState()
    class Error(val error: String? = "") : ArticleDetailViewState()
    object NoInternet : ArticleDetailViewState()
    object ShowLoading : ArticleDetailViewState()
}