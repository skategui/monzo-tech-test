package com.monzo.androidtest.api

import java.net.UnknownHostException

/**
 * Utils to check the type of HTTP error thrown
 */
class HttpErrorUtils {

    /**
     * Check if the user has list the connexion given the throwable thrown
     * @param throwable throwable to check
     * @return true if he user has lost connexion, false otherwise
     */
    fun hasLostInternet(throwable: Throwable): Boolean {
        return throwable is UnknownHostException
    }
}