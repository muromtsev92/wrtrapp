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

                var usedWordIds by remember { mutableStateOf(mutableSetOf<Long>()) }

                // Загрузка слов при старте
                LaunchedEffect(Unit) {
                    val loaded = dao.getAll()
                    if (loaded.isNotEmpty()) {
                        allWords = loaded
                        val first = loaded.random()
                        currentWord = first
                        usedWordIds.add(first.id)
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
                                currentWord?.let { word ->
                                    if (choice == word.article) {
                                        correctCount++
                                        resultText = "✅ Правильно! ${word.article} ${word.word}"
                                    } else {
                                        incorrectCount++
                                        resultText = "❌ Неправильно. Было: ${word.article} ${word.word}"
                                    }
                                }
                            }) {
                                Text(choice)
                            }
                        }
                    }

                    Text(resultText)

                    Button(onClick = {
                        if (allWords.isNotEmpty()) {
                            val unusedWords = allWords.filterNot { usedWordIds.contains(it.id) }

                            if (unusedWords.isNotEmpty()) {
                                val next = unusedWords.random()
                                currentWord = next
                                usedWordIds.add(next.id)
                                resultText = ""
                            } else {
                                resultText = "🎉 Вы прошли все слова!"
                                usedWordIds.clear()
                                currentWord = null
                            }
                        }
                    }) {
                        Text("Следующее слово")
                    }

                    HorizontalDivider()

                    Text("Правильных: $correctCount")
                    Text("Ошибок: $incorrectCount")

                    Button(onClick = {
                        correctCount = 0
                        incorrectCount = 0
                        resultText = "🔄 Сброшено"
                    }) {
                        Text("Сбросить счётчики")
                    }
                }
            }
        }



    }
}