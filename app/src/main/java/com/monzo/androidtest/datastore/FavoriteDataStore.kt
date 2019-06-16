package com.monzo.androidtest.datastore

// Currently store the list of favorites from the user
// in the future, could store those info in Room to persist the data between sessions
class FavoriteDataStore {
    private val favorites: ArrayList<String> = arrayListOf()

    fun isAddedToFavorite(articleID: String) = favorites.contains(articleID)

    fun removeFavorite(articleID: String) {

        favorites.remove(articleID)
    }

    fun addFavorite(articleID: String) {
        if (!favorites.contains(articleID))
            favorites.add(articleID)
    }

}
