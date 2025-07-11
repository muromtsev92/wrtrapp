package com.example.wrtrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.ui.state.ArticleUiState
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
            .padding(77.dp),
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

@Preview(showBackground = true)
@Composable
fun PreviewArticleQuizScreen() {
    val testState = ArticleUiState(
        currentWord = NounEntity(1, "Hund", "der", "Hunde"),
        resultText = "✅ Правильно!",
        correctCount = 1,
        incorrectCount = 0
    )

    ArticleQuizContent(
        state = testState,
        onAnswerSelected = {},
        onNext = {},
        onReset = {}
    )
}

@Composable
fun ArticleQuizContent(
    state: ArticleUiState,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.currentWord?.word ?: "¯\\_(ツ)_/¯",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = state.resultText,
            style = MaterialTheme.typography.bodyLarge
        )

        Button(onClick = onNext) {
            Text("Следующее слово")
        }

        Button(onClick = onReset) {
            Text("Сбросить статистику")
        }

        Spacer(modifier = Modifier.height(150.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("der", "die", "das").forEach { article ->
                Button(onClick = { onAnswerSelected(article) },
                    modifier = Modifier
                        .height(60.dp)
                        .weight(1f)
                ) {
                    Text(article, fontSize = 20.sp)
                }

            }
        }
    }
}

