package com.monzo.androidtest.ui.utils

import java.util.*

/**
 * Utils used to check if a date is from this week of the previous week given the week number of the date
 */
object DateUtils {

    /**
     * Get this week number
     *  @return [Int]  week number of this week
     */
    private fun getThisWeekNumber(): Int {
        val calNow = Calendar.getInstance()
        calNow.time = Date()
        return calNow.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * check if the date is from this week
     *  @return [Boolean]  true is the date is from this week, false otherwise
     */
    fun isThisWeek(date: Date): Boolean {
        val calDate = Calendar.getInstance()
        calDate.time = date
        val weekDate = calDate.get(Calendar.WEEK_OF_YEAR)


        return getThisWeekNumber() == weekDate
    }

    /**
     * check if the date is from the previous week
     *  @return [Boolean]  true is the date is from the previous week, false otherwise
     */
    fun isPreviousWeek(date: Date): Boolean {
        val calDate = Calendar.getInstance()
        calDate.time = date
        val weekDate = calDate.get(Calendar.WEEK_OF_YEAR)


        return getThisWeekNumber() - 1 == weekDate
    }
}