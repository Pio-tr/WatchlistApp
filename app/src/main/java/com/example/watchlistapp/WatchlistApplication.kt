package com.example.watchlistapp

import android.app.Application
import com.example.watchlistapp.database.WatchlistDatabase
import com.example.watchlistapp.database.WatchlistRepository


class WatchlistApplication : Application() {

    lateinit var repository: WatchlistRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = WatchlistRepository(WatchlistDatabase.getDatabase(this).watchlistDao())
    }
}