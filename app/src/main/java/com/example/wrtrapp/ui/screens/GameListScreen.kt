package com.example.wrtrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameListScreen(
    onPlayClick: () -> Unit,
    onAddWordsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Выберите игру", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onPlayClick, modifier = Modifier.fillMaxWidth()) {
            Text("Угадай артикль")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onAddWordsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Добавить слова")
        }
    }
}
