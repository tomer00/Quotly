package com.tomer.quotly.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ModalSample::class], version = 1, exportSchema = false)
abstract class Database :RoomDatabase() {
    abstract fun channelDao(): Dao
}