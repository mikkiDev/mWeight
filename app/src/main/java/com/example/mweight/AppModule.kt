package com.example.mweight

import androidx.room.Room

object AppModule {

    private lateinit var database: AppDatabase
    private lateinit var repository: WeightRepository

    fun initialize(applicationContext: MainActivity) {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "weight_database"
        ).build()

        repository = WeightRepository(database.weightDao())
    }


    fun getRepository(): WeightRepository {
        if (!::repository.isInitialized) {
            throw IllegalStateException("AppModule has not been initialized. Call `initialize()` first.")
        }
        return repository
    }

}