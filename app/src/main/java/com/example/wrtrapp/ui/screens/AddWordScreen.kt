package com.example.wrtrapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wrtrapp.viewmodel.AddWordViewModel

/**
 * Экран ручного добавления нового существительного в базу.
 * Размещается в пакете ui.screens, вызывается из MainActivity через currentScreen = "manual".
 */
@Composable
fun AddWordScreen(
    viewModel: AddWordViewModel = viewModel(),
    onBack: () -> Unit
) {
    // --- Локальные состояния для полей ввода
    var word by remember { mutableStateOf("") }
    var article by remember { mutableStateOf("der") }
    var pluralForm by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var pluralTranslation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Добавить существительное", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Поле для слова
        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("Слово") },
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопки выбора артикля
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("der", "die", "das").forEach {
                Button(onClick = { article = it }) { Text(it) }
            }
        }

        // Поле для формы множественного числа
        OutlinedTextField(
            value = pluralForm,
            onValueChange = { pluralForm = it },
            label = { Text("Форма множественного числа") },
            modifier = Modifier.fillMaxWidth()
        )

        // Поле для перевода
        OutlinedTextField(
            value = translation,
            onValueChange = { translation = it },
            label = { Text("Перевод") },
            modifier = Modifier.fillMaxWidth()
        )

        // Поле для перевода множественного числа
        OutlinedTextField(
            value = pluralTranslation,
            onValueChange = { pluralTranslation = it },
            label = { Text("Перевод множественного числа") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка сохранения слова
        Button(
            onClick = {
                if (word.isNotBlank() && pluralForm.isNotBlank()) {
                    viewModel.addNoun(
                        word = word,
                        article = article,
                        pluralForm = pluralForm,
                        translation = translation,
                        pluralTranslation = pluralTranslation
                    )
                    message = "Слово добавлено!"
                    // очищаем поля
                    word = ""
                    pluralForm = ""
                    translation = ""
                    pluralTranslation = ""
                } else {
                    message = "Заполните все обязательные поля!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка назад
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Назад")
        }

        // Сообщение об успехе или ошибке
        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
