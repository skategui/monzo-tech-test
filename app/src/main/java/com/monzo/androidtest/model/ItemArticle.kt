package com.monzo.androidtest.model

import java.util.*

data class ItemArticle(val isSaved: Boolean,
                       val title: String,
                       val thumbnail: String,
                       val published: Date,
                       val url : String)