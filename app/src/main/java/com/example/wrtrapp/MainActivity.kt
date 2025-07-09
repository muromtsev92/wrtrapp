package com.example.wrtrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.wrtrapp.data.DatabaseProvider
import com.example.wrtrapp.data.entities.NounEntity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.nounDao()

        lifecycleScope.launch {
            if (dao.getAll().isEmpty()) {
                dao.insert(NounEntity(word = "Hund", article = "der", pluralForm = "Hunde"))
                dao.insert(NounEntity(word = "Katze", article = "die", pluralForm = "Katzen"))
                dao.insert(NounEntity(word = "Kind", article = "das", pluralForm = "Kinder"))
            }
        }

        setContent {
            MaterialTheme {
                val scope = rememberCoroutineScope()
                var allWords by remember { mutableStateOf<List<NounEntity>>(emptyList()) }
                var currentWord by remember { mutableStateOf<NounEntity?>(null) }
                var resultText by remember { mutableStateOf("") }

                var correctCount by remember { mutableStateOf(0) }
                var incorrectCount by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    val loaded = dao.getAll()
                    if (loaded.isNotEmpty()) {
                        allWords = loaded
                        currentWord = loaded.random()
                    } else {
                        resultText = "⚠️ Нет слов в базе"
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = currentWord?.word ?: "Загрузка...",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("der", "die", "das").forEach { choice ->
                            Button(onClick = {
                                if (currentWord != null) {
                                    val correct = choice == currentWord!!.article
                                    resultText = if (correct) {
                                        correctCount++
                                        "✅ Правильно!"
                                    } else {
                                        incorrectCount++
                                        "❌ Неправильно. Было: ${currentWord!!.article}"
                                    }

                                    scope.launch {
                                        kotlinx.coroutines.delay(800)
                                        currentWord = allWords.random()
                                        resultText = ""
                                    }
                                }
                            }) {
                                Text(choice)
                            }
                        }
                    }

                    Text(text = resultText)

                    HorizontalDivider()

                    Text("Правильных: $correctCount")
                    Text("Неправильных: $incorrectCount")

                    Button(onClick = {
                        correctCount = 0
                        incorrectCount = 0
                        resultText = "🔄 Сброшено"
                    }) {
                        Text("Сбросить счётчик")
                    }
                }
            }
        }

    }
}