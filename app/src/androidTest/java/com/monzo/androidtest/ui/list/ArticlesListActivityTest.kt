package com.monzo.androidtest.ui.list

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.monzo.androidtest.DataBuilder
import com.monzo.androidtest.GuardianApp
import com.monzo.androidtest.R
import com.monzo.androidtest.api.HttpErrorUtils
import com.monzo.androidtest.datastore.FavoriteDataStore
import com.monzo.androidtest.ui.utils.CustomAssertions.Companion.hasItemCount
import com.monzo.androidtest.ui.utils.RecyclerViewMatcher.Companion.withRecyclerView
import com.monzo.androidtest.ui.utils.simpleFormat
import com.monzo.androidtest.usecase.ArticleUseCase
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import io.reactivex.Single
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.UnknownHostException
import javax.inject.Provider

@RunWith(AndroidJUnit4::class)
class ArticlesListActivityTest {
    private val articleUseCase = mock<ArticleUseCase>()


    private val testViewModelFactory =
        ListArticlesViewModel.Factory(articleUseCase, HttpErrorUtils(), FavoriteDataStore())

    @get:Rule
    val activityTestRule = ActivityTestRule<ArticlesListActivity>(ArticlesListActivity::class.java, false, false)

    // inject the mock and the created ViewModelFactory (with the articleUseCase mocked) into the view
    private fun initDispatcherAndLaunchActivity() {
        val myApp =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GuardianApp
        myApp.dispatchingAndroidInjector = createFakeActivityInjector {
            viewModelFactory = testViewModelFactory
        }
        activityTestRule.launchActivity(null)
    }


    @Test
    fun given_a_connexion_when_fetching_articles_then_show_the_articles_list() {

        val articles = DataBuilder.getArticles()

        whenever(articleUseCase.getArticles()).thenReturn(Single.just(articles))
        initDispatcherAndLaunchActivity()

        onView(withId(R.id.tvStateTitle)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvReload)).check(matches(not(isDisplayed())))
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(hasItemCount(articles.size + 1))// + 1 for the section title


        for (index in 0 until articles.size) {
            val article = articles[index]
            onView(
                withRecyclerView(R.id.recyclerView)
                    .atPositionOnView(index + 1, R.id.tvArticleHeadline) //  +1 to skip the section title
            ).check(matches(withText(article.title)))

            onView(
                withRecyclerView(R.id.recyclerView)
                    .atPositionOnView(index + 1, R.id.tvDate)
            ).check(matches(withText(article.published.simpleFormat())))
        }
    }


    @Test
    fun given_an_error_of_connexion_when_fetching_articles_then_show_error_msg() {

        whenever(articleUseCase.getArticles()).thenReturn(Single.error(UnknownHostException()))
        initDispatcherAndLaunchActivity()


        onView(withId(R.id.tvStateTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvStateTitle)).check(matches(withText(R.string.lost_internet)))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvReload)).check(matches(isClickable()))
        onView(withId(R.id.tvReload)).check(matches(isDisplayed()))
    }

    @Test
    fun given_an_empty_list_when_fetching_articles_then_show_empty_list_msg() {

        whenever(articleUseCase.getArticles()).thenReturn(Single.just(emptyList()))
        initDispatcherAndLaunchActivity()

        onView(withId(R.id.tvStateTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvStateTitle)).check(matches(withText(R.string.error_no_article_available)))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvReload)).check(matches(isDisplayed()))

    }

    @Test
    fun given_an_error_when_fetching_articles_then_show_a_generic_error_msg() {

        whenever(articleUseCase.getArticles()).thenReturn(Single.error(Throwable("error")))

        initDispatcherAndLaunchActivity()

        onView(withId(R.id.tvStateTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvStateTitle)).check(matches(withText((R.string.error_try_again_later))))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvReload)).check(matches(isDisplayed()))
    }

    // todo : add test for the case when we have  some articles in the favorites
    // todo : add test to verify if the list is sorted by date
    // todo : add test to verify section titles and items in those section


    // utils
    private fun createFakeActivityInjector(block: ArticlesListActivity.() -> Unit)
            : DispatchingAndroidInjector<Activity> {
        val injector = AndroidInjector<Activity> { instance ->
            if (instance is ArticlesListActivity) {
                instance.block()
            }
        }
        val factory = AndroidInjector.Factory<Activity> { injector }
        val map = mapOf(
            Pair<Class<*>,
                    Provider<AndroidInjector.Factory<*>>>(ArticlesListActivity::class.java, Provider { factory })
        )
        return DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(map, emptyMap(), emptyMap(), emptyMap())
    }
}