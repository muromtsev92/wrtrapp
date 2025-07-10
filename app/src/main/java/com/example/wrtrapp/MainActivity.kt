package com.example.wrtrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.wrtrapp.ui.screens.ArticleQuizScreen
import com.example.wrtrapp.viewmodel.ArticleQuizViewModel


class MainActivity : ComponentActivity() {

    private val viewModel: ArticleQuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadWords()

        setContent {
            ArticleQuizScreen(viewModel)
        }
    }
}