package com.example.wrtrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wrtrapp.viewmodel.ArticleQuizViewModel

@Composable
fun ArticleQuizScreen(viewModel: ArticleQuizViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWords()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.currentWord?.word ?: "Загрузка...",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("der", "die", "das").forEach { article ->
                Button(
                    onClick = { viewModel.checkAnswer(article) },
                    enabled = state.currentWord != null
                ) {
                    Text(article)
                }
            }
        }

        Text(text = state.resultText)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("✅: ${state.correctCount}")
            Text("❌: ${state.incorrectCount}")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { viewModel.nextWord() }) {
                Text("Следующее")
            }
            Button(onClick = { viewModel.resetStats() }) {
                Text("Сбросить")
            }
        }
    }
}
