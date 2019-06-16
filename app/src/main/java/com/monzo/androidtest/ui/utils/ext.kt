package com.monzo.androidtest.ui.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Convert the current date into a dd/Mm/yyyy format
 */
fun Date.simpleFormat(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}


/**
 *   I/O
 * Kotlin extensions on Single and Observable Object, used while making HTTP requests, to make sure to make the requests
 * in the background thread and come back on the android main thread
 */

fun <T> Single<T>.io(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.io(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
}
