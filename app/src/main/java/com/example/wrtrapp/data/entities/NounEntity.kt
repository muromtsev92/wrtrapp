package com.example.wrtrapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nouns")
data class NounEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val article: String,
    val pluralForm: String,
    val translation: String? = null,
    val pluralTranslation: String? = null
)
