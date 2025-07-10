package com.example.wrtrapp.ui.state

import com.example.wrtrapp.data.entities.NounEntity

data class ArticleUiState(
    val currentWord: NounEntity? = null,
    val resultText: String = "",
    val correctCount: Int = 0,
    val incorrectCount: Int = 0
)
