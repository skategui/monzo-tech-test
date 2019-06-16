package com.monzo.androidtest.ui.list

import com.monzo.androidtest.model.ItemArticle

// states possible of the list articles view
sealed class ListArticlesViewState {
    class DisplayArticlesList(val articles: List<ItemArticle>) : ListArticlesViewState()
    class OpenArticleDetail(val articleURL: String) : ListArticlesViewState()
    class Error(val error: String? = "") : ListArticlesViewState()
    object NoInternet : ListArticlesViewState()
    object ShowLoading : ListArticlesViewState()
    object DisplayEmptyListMessage : ListArticlesViewState()
}