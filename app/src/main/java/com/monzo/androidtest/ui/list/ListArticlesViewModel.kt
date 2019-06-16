package com.monzo.androidtest.ui.list


import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.common.BaseViewModel
import com.monzo.androidtest.datastore.FavoriteDataStore
import com.monzo.androidtest.model.Article
import com.monzo.androidtest.model.ItemArticle
import com.monzo.androidtest.ui.utils.io
import com.monzo.androidtest.usecase.ArticleUseCase
import javax.inject.Inject


/**
 * Viewmodel responsible for the business logic associated to the list of articles view
 */
class ListArticlesViewModel
@Inject constructor(
    private val articleUseCase: ArticleUseCase,
    private val httpErrorUtils: HttpErrorUtils,
    private val favoriteDataStore: FavoriteDataStore
) : BaseViewModel<ListArticlesViewState>(), DefaultLifecycleObserver {


    private val articles = ArrayList<Article>()


    /**
     * Associated to the lifecycle of the activity; load the list of articles when the activity
     * is created
     *  @param owner LifecycleOwner
     */
    override fun onCreate(owner: LifecycleOwner) {
        loadArticles()
    }

    /**
     * When the view is onResume, update the list of articles taken in consideration the new articles added/removed
     * from the favourites
     */
    override fun onResume(owner: LifecycleOwner) {
        populateItemsList(articles)
    }


    /**
     * Reload articles list from the server
     */
    fun onReloadClicked() {
        loadArticles()
    }

    /**
     * Callback when a row of the list of articles is clicked; emit a OpenArticleDetail
     *  @param article article associated to the row clicked
     */
    fun onRowClicked(article: ItemArticle) {
        emitViewState(ListArticlesViewState.OpenArticleDetail(article.url))
    }

    /**
     * Load the list of articles from the server and generate the populate the list of items
     * to add in the list
     */
    private fun loadArticles() {
        disposables.add(
            articleUseCase.getArticles()
                .doOnSubscribe { emitViewState(ListArticlesViewState.ShowLoading) }
                .io()
                .subscribe(this::success, this::articlesErrors)
        )
    }

    /**
     * Emit the list of articles if not empty,
     * otherwise create the list of items to add in the lists
     *  @param articles articles loaded
     */
    private fun success(articles: List<Article>) {
        if (articles.isEmpty())
            emitViewState(ListArticlesViewState.DisplayEmptyListMessage)
        else {
            this.articles.clear()
            this.articles.addAll(articles)
            populateItemsList(articles)
        }
    }

    /**
     * Generate the list of items, sorted by the date(from most recent to the oldest)
     * from a list of articles
     */
    private fun populateItemsList(articles: List<Article>) {
        val itemList = articles.map { current ->
            ItemArticle(
                isSaved = favoriteDataStore.isAddedToFavorite(current.id),
                title = current.title,
                thumbnail = current.thumbnail,
                published = current.published,
                url = current.url
            )
        }
            .sortedByDescending { it.published }

        emitViewState(ListArticlesViewState.DisplayArticlesList(itemList))

    }

    /**
     * Error thrown from the request. Emit the error with the message to the view
     */
    private fun articlesErrors(throwable: Throwable) {
        when (httpErrorUtils.hasLostInternet(throwable)) {
            true -> emitViewState(ListArticlesViewState.NoInternet)
            false -> emitViewState(ListArticlesViewState.Error(throwable.message))
        }
    }


    class Factory
    @Inject constructor(
        private val articleUseCase: ArticleUseCase,
        private val httpErrorUtils: HttpErrorUtils,
        private val favoriteDataStore: FavoriteDataStore
    ) : ViewModelProvider.Factory {
        @SuppressWarnings("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListArticlesViewModel(articleUseCase, httpErrorUtils, favoriteDataStore) as T
        }
    }
}