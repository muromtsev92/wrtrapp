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
                var currentWord by remember { mutableStateOf<NounEntity?>(null) }
                var resultText by remember { mutableStateOf("") }

                // Загружаем слово при старте
                LaunchedEffect(Unit) {
                    val all = dao.getAll()
                    currentWord = all.random()
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
                                    resultText = if (choice == currentWord!!.article) {
                                        "✅ Правильно!"
                                    } else {
                                        "❌ Неправильно. Было: ${currentWord!!.article}"
                                    }
                                }
                            }) {
                                Text(choice)
                            }
                        }
                    }
                    Text(resultText)
                }
            }
        }
    }
}