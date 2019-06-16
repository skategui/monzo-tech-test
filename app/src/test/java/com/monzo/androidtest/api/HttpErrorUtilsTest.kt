package com.monzo.androidtest.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.net.UnknownHostException

class HttpErrorUtilsTest {

    private val httpErrorUtils = HttpErrorUtils()

    @Test
    fun `Check if the user has lost the connexion given a UnknownHostException`() {
        assertThat(httpErrorUtils.hasLostInternet(UnknownHostException())).isTrue()
    }

    @Test
    fun `Check if the user has lost the connexion given an exception that is not UnknownHostException`() {
        assertThat(httpErrorUtils.hasLostInternet(RuntimeException())).isFalse()
    }

}