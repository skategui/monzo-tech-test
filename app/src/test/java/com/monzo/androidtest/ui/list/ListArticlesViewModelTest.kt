package com.monzo.androidtest.ui.list

import com.google.common.truth.Truth.assertThat
import com.monzo.androidtest.DataBuilder
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.datastore.FavoriteDataStore
import com.monzo.androidtest.ui.utils.BaseRule
import com.monzo.androidtest.usecase.ArticleUseCase
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class ListArticlesViewModelTest : BaseRule() {
    private lateinit var viewModel: ListArticlesViewModel

    private val articleUseCase = mock<ArticleUseCase>()
    private val articles = DataBuilder.getArticles()


    @Before
    fun setUp() {
        viewModel = ListArticlesViewModel(articleUseCase, HttpErrorUtils(), FavoriteDataStore())
        whenever(articleUseCase.getArticles()).thenReturn(Single.just(articles))
    }


    @Test
    fun `Given the viewmodel created when the articles list is loading then display the loader `() {


        val testObserver = viewModel.viewStateObservable.test()

        viewModel.onCreate(mock())
        testObserver
            .assertNoErrors()
            .assertValueAt(0, ListArticlesViewState.ShowLoading)
    }


    @Test
    fun `Given the viewmodel created when the refresh button is clicked then load the articles list`() {


        val testObserver = viewModel.viewStateObservable.test()

        viewModel.onReloadClicked()
        testObserver
            .assertNoErrors()
            .assertValueAt(0, ListArticlesViewState.ShowLoading)
    }

    @Test
    fun `Given an empty list of articles when viewModel is created then send DisplayEmptyListMessage`() {
        whenever(articleUseCase.getArticles()).thenReturn(Single.just(emptyList()))

        val testObserver = viewModel.viewStateObservable.test()

        viewModel.onCreate(mock())

        val state = testObserver
            .assertNoErrors()
            .values()[1]

        assertThat(state is ListArticlesViewState.DisplayEmptyListMessage).isTrue()
    }

    @Test
    fun `Given an error when loading the articles then send an error`() {
        val errorMsg = "errorMsg"
        whenever(articleUseCase.getArticles()).thenReturn(Single.error(Throwable(errorMsg)))

        val testObserver = viewModel.viewStateObservable.test()

        viewModel.onCreate(mock())


        val state = testObserver
            .assertNoErrors()
            .values()[1]

        assertThat(state.cast<ListArticlesViewState.Error>().error).isEqualTo(errorMsg)
    }

    @Test
    fun `Given no internet connexion when loading the comments then emit No internet action`() {
        whenever(articleUseCase.getArticles()).thenReturn(Single.error(UnknownHostException()))

        val testObserver = viewModel.viewStateObservable.test()

        viewModel.onCreate(mock())

        testObserver
            .assertNoErrors()
            .assertValueAt(1, ListArticlesViewState.NoInternet)
    }

    // fixme : add test to verify the generated list of item for the recyclerview, and sorted

}


internal fun <T> ListArticlesViewState.cast(): T = this as T