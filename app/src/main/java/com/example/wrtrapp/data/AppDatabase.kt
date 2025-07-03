package com.example.wrtrapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wrtrapp.data.dao.NounDao
import com.example.wrtrapp.data.entities.NounEntity

@Database(
    entities = [NounEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun nounDao(): NounDao
}