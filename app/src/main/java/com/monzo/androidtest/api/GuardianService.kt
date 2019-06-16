package com.monzo.androidtest.api

import com.monzo.androidtest.api.model.ApiArticleDetailResponse
import com.monzo.androidtest.api.model.ApiArticleListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GuardianService {
    @GET("search?show-fields=headline,thumbnail")
    fun searchArticles(@Query("q") searchTerm: String, @Query("page-size") pageSize: Int = 50): Single<ApiArticleListResponse>

    @GET
    fun getArticle(@Url articleUrl: String, @Query("show-fields") fields: String): Single<ApiArticleDetailResponse>
}
