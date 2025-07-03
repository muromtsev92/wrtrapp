package com.example.wrtrapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wrtrapp.data.entities.NounEntity

@Dao
interface NounDao {
    @Insert
    suspend fun insert(noun: NounEntity)

    @Query("SELECT * FROM nouns")
    suspend fun getAll(): List<NounEntity>

    @Query("SELECT * FROM nouns WHERE word = :word LIMIT 1")
    suspend fun findByWord(word: String): NounEntity?
}