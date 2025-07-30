package com.example.wrtrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wrtrapp.viewmodel.ImportWordsViewModel
import com.example.wrtrapp.viewmodel.ImportWordsViewModelFactory

@Composable
fun ImportWordsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ImportWordsViewModel = viewModel(
        factory = ImportWordsViewModelFactory(context)
    )

    var jsonInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Импорт слов (JSON)", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = jsonInput,
            onValueChange = { jsonInput = it },
            label = { Text("Вставьте JSON") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.importWords(jsonInput) { success ->
                    message = if (success) "Слова добавлены!" else "Ошибка импорта"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Импортировать")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Назад")
        }

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

