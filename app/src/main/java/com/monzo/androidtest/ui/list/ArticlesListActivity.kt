package com.monzo.androidtest.ui.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.airbnb.lottie.LottieDrawable.REVERSE
import com.monzo.androidtest.R
import com.monzo.androidtest.common.DaggerBaseActivity
import com.monzo.androidtest.model.ItemArticle
import com.monzo.androidtest.ui.detail.ArticleDetailActivity
import com.monzo.androidtest.ui.list.adapter.ArticleItem
import com.monzo.androidtest.ui.list.adapter.TitleItem
import com.monzo.androidtest.ui.utils.DateUtils
import com.monzo.androidtest.ui.utils.io
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.groupiex.plusAssign
import kotlinx.android.synthetic.main.activity_article_list.*
import javax.inject.Inject

/**
 * View responsible to display the list of articles loaded from the server
 */
class ArticlesListActivity : DaggerBaseActivity() {

    @Inject
    lateinit var viewModelFactory: ListArticlesViewModel.Factory

    private lateinit var viewModel: ListArticlesViewModel

    // use groupie to display items in the recyclerview
    private val groupAdapter = GroupAdapter<ViewHolder>()

    // sections in the list
    private lateinit var sectionFavorites: Section
    private lateinit var sectionThisWeek: Section
    private lateinit var sectionPastWeek: Section
    private lateinit var articlesItems: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_list)

        init()
        initViewModel()
    }

    /**
     * Init the view
     */
    private fun init() {
        initAnimation()
        initRecyclerView()
        initListeners()
        initViews()
    }

    /**
     * listeners in the views
     */
    private fun initListeners() {
        tvReload.setOnClickListener { viewModel.onReloadClicked() }
        swipeRefreshLayout.setOnRefreshListener { viewModel.onReloadClicked() }
    }


    /**
     * Init animation of the comment button to push the user to click on it
     */
    private fun initAnimation() {
        // set animation
        animation.repeatMode = REVERSE
        animation.repeatCount = INFINITE
    }

    /**
     * Init recycler view with the different sections
     */
    private fun initRecyclerView() {

        groupAdapter.apply {
            setOnItemClickListener { item, _ -> if (item is ArticleItem) viewModel.onRowClicked(item.article) }
        }

        sectionFavorites = Section(TitleItem(getString(R.string.article_list_section_favourites)))
        sectionThisWeek = Section(TitleItem(getString(R.string.article_list_section_this_week)))
        sectionPastWeek = Section(TitleItem(getString(R.string.article_list_section_previous_week)))
        articlesItems = Section(TitleItem(getString(R.string.article_list_section_articles)))

        addGroupToAdapter(sectionFavorites)
        addGroupToAdapter(sectionThisWeek)
        addGroupToAdapter(sectionPastWeek)
        addGroupToAdapter(articlesItems)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = groupAdapter
    }

    private fun addGroupToAdapter(section: Section) {
        section.setHideWhenEmpty(true)
        groupAdapter += section
    }

    /**
     * init viewmodel
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListArticlesViewModel::class.java)
        lifecycle.addObserver(viewModel)
        disposables.add(
            viewModel.viewStateObservable
                .io()
                .subscribe(this::render, Throwable::printStackTrace)
        )
    }

    /**
     * Render the view with the actions given the current state of the view
     * @param viewState current state of the view
     */
    private fun render(viewState: ListArticlesViewState) {
        initViews()
        when (viewState) {
            is ListArticlesViewState.ShowLoading -> checkLoadingState(true)
            is ListArticlesViewState.DisplayArticlesList -> updateLatestArticlesList(viewState.articles)
            is ListArticlesViewState.NoInternet -> noInternet()
            is ListArticlesViewState.OpenArticleDetail -> openArticleDetail(viewState.articleURL)
            is ListArticlesViewState.Error -> displayError()
            is ListArticlesViewState.DisplayEmptyListMessage -> displayEmptyListMessage()
        }
    }

    /**
     * update the list given a new list of articles
     * Update the different section and update the adapter given an anonymous function to validate an article against
     * The adapter is using DiffUtil, then it will only update the modified items (better in performance)
     * @param articles list of articles
     */
    private fun updateLatestArticlesList(articles: List<ItemArticle>) {

        updateSection(articles, sectionFavorites) { article -> article.isSaved }
        updateSection(articles, sectionThisWeek) { article -> !article.isSaved && DateUtils.isThisWeek(article.published) }
        updateSection(articles, sectionPastWeek) { article -> !article.isSaved && DateUtils.isPreviousWeek(article.published) }
        updateSection(articles, articlesItems) { article -> !article.isSaved && !DateUtils.isPreviousWeek(article.published) && !DateUtils.isThisWeek(article.published) }

        // update all the sections
        groupAdapter.update(listOf(sectionFavorites, sectionThisWeek, sectionPastWeek, articlesItems))
    }

    /**
     * filtered out a list of articles given a condition and update a section of the list with this new list
     * @param articles list of articles
     * @param section section to update
     * @param condition condition for validating the article to add it in the new list
     */
    private fun updateSection(articles: List<ItemArticle>, section: Section, condition: (ItemArticle) -> Boolean) {
        val filteredItems = articles.filter { article -> condition(article) }
        section.update(filteredItems.map { ArticleItem(it) })
    }

    /**
     * Inform the user to that the list of articles is empty
     */
    private fun displayEmptyListMessage() {
        llState.visibility = View.VISIBLE
        animation.setAnimation(R.raw.empty_list_animation)
        tvStateTitle.text = getString(R.string.error_no_article_available)
        animation.playAnimation()
        hideRecyclerView()
    }


    /**
     * Inform the user to the error
     */
    private fun displayError() {
        llState.visibility = View.VISIBLE
        animation.setAnimation(R.raw.error_animation)
        tvStateTitle.text = getString(R.string.error_try_again_later)
        animation.playAnimation()
        hideRecyclerView()
    }

    /**
     * put the views in their original state
     */
    private fun initViews() {
        checkLoadingState(false)
        animation.clearAnimation()
        recyclerView.visibility = View.VISIBLE
        tvReload.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false

        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    /**
     * Check the state of the loading animation
     * @param isLoading true if the request is still loading, false otherwise
     */
    private fun checkLoadingState(isLoading: Boolean) {
        if (isLoading) {
            animation.setAnimation(R.raw.loading_animation)
            animation.playAnimation()
            tvStateTitle.text = getString(R.string.loading_in_progress)
            llState.visibility = View.VISIBLE
            hideRecyclerView()
        } else {
            llState.visibility = View.GONE
        }
    }

    /**
     * Inform the user to that he lost the internet connexion
     */
    private fun noInternet() {
        llState.visibility = View.VISIBLE
        tvStateTitle.text = getString(R.string.lost_internet)
        animation.setAnimation(R.raw.error_animation)
        tvReload.visibility = View.VISIBLE
        animation.playAnimation()
        hideRecyclerView()
    }

    /**
     * hide recycler view
     */
    private fun hideRecyclerView() {
        recyclerView.visibility = View.INVISIBLE
    }

    /**
     * open the detail of an article in the different view
     * @param articleURL url of the article to load
     */
    private fun openArticleDetail(articleURL: String) {
        ArticleDetailActivity.start(this, articleURL)
    }
}
