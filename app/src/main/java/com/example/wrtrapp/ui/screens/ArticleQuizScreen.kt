package com.example.wrtrapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.ui.state.ArticleUiState
import com.example.wrtrapp.viewmodel.ArticleQuizViewModel
import kotlin.system.exitProcess

@Composable
fun ArticleQuizScreen(
    viewModel: ArticleQuizViewModel = viewModel(),
    onBackToMenu: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Загружаем слова при первом запуске экрана
    LaunchedEffect(Unit) {
        viewModel.loadWords()
    }

    // Состояние диалога выхода
    val showExitDialog = remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog.value = true
    }

    // Диалог выхода
    if (showExitDialog.value) {
        AlertDialog(
            onDismissRequest = { showExitDialog.value = false },
            title = { Text("Выход") },
            text = { Text("Желаете выйти в главное меню или закрыть приложение?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog.value = false
                    onBackToMenu()
                }) { Text("Главное меню") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog.value = false
                    exitProcess(0)
                }) { Text("Закрыть приложение") }
            }
        )
    }

    if (state.showMistakesScreen) {
        MistakesScreen(
            mistakes = state.mistakes,
            onRestart = { viewModel.startNewGame() }
        )
    } else {
        ArticleQuizContent(
            state = state,
            stopOnError = state.stopOnError,
            onToggleMode = { isStopOnError -> viewModel.setStopOnError(isStopOnError) },
            onAnswerSelected = { article -> viewModel.checkAnswer(article) },
            onReset = { viewModel.resetStats() }
        )
    }
}

@Composable
fun ArticleQuizContent(
    state: ArticleUiState,
    stopOnError: Boolean,
    onToggleMode: (Boolean) -> Unit,
    onAnswerSelected: (String) -> Unit,
    onReset: () -> Unit
) {
    Box( // Используем Box, чтобы разместить тумблер поверх
        modifier = Modifier.fillMaxSize()
    ) {
        // --- Тумблер в правом верхнем углу
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd) // фиксируем в правом верхнем углу
                .padding(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("До первой ошибки   ", style = MaterialTheme.typography.bodySmall)
            Switch(
                checked = stopOnError,
                onCheckedChange = { onToggleMode(it) }
            )
        }

        // --- Основной контент по центру
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Слово
            Text(
                text = state.currentWord?.word ?: "¯\\_(ツ)_/¯",
                style = MaterialTheme.typography.displayLarge
            )

            // Сообщение результата
            if (state.resultText.isNotEmpty()) {
                Text(
                    text = state.resultText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Статистика
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("✅: ${state.correctCount}")
                Text("❌: ${state.incorrectCount}")
            }

            // Кнопка сброса
            Button(onClick = onReset) {
                Text("Сбросить статистику")
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Кнопки выбора артиклей
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("der", "die", "das").forEach { article ->
                    Button(
                        onClick = { onAnswerSelected(article) },
                        enabled = state.currentWord != null,
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
}

@Composable
fun MistakesScreen(
    mistakes: List<NounEntity>,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ваши ошибки:", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (mistakes.isEmpty()) {
            Text("Ошибок нет! Отличный результат 👏")
        } else {
            mistakes.forEach { mistake ->
                val translationPart = mistake.translation?.let { " – $it" } ?: ""
                Text("${mistake.article} ${mistake.word}$translationPart")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRestart) {
            Text("Начать заново")
        }
    }
}


