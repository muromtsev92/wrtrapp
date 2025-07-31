package com.example.wrtrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wrtrapp.data.DatabaseProvider
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.data.repository.NounRepository
import com.example.wrtrapp.ui.state.ArticleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NounRepository =
        NounRepository(DatabaseProvider.getDatabase(application).nounDao())

    private val _uiState = MutableStateFlow(ArticleUiState())
    val uiState: StateFlow<ArticleUiState> = _uiState

    private var allWords: List<NounEntity> = emptyList()
    private val usedWordIds = mutableSetOf<Long>()

    var stopOnError = true // можно будет менять из настроек режима

    fun loadWords() {
        viewModelScope.launch {
            val loaded = repository.getAllNouns()
            allWords = loaded
            if (loaded.isNotEmpty()) {
                val first = loaded.random()
                usedWordIds.add(first.id)
                _uiState.value = _uiState.value.copy(currentWord = first)
            } else {
                _uiState.value = _uiState.value.copy(resultText = "⚠️ Нет слов в базе")
            }
        }
    }

    fun checkAnswer(choice: String) {
        val word = _uiState.value.currentWord ?: return
        val correct = choice == word.article

        if (correct) {
            _uiState.value = _uiState.value.copy(
                correctCount = _uiState.value.correctCount + 1,
                resultText = "✅ Правильно! ${word.article} ${word.word}"
            )
            nextWord() // сразу новое слово
        } else {
            _uiState.value = _uiState.value.copy(
                incorrectCount = _uiState.value.incorrectCount + 1,
                resultText = "❌ Неправильно. Было: ${word.article} ${word.word}"
            )

            if (stopOnError) {
                // Сигнализируем конец игры
                _uiState.value = _uiState.value.copy(
                    currentWord = null,
                    resultText = "Игра окончена! Счёт: ${_uiState.value.correctCount}"
                )
            } else {
                nextWord()
            }
        }
    }


    fun nextWord() {
        val unused = allWords.filterNot { usedWordIds.contains(it.id) }
        if (unused.isNotEmpty()) {
            val next = unused.random()
            usedWordIds.add(next.id)
            _uiState.value = _uiState.value.copy(
                currentWord = next,
                resultText = ""
            )
        } else {
            _uiState.value = _uiState.value.copy(
                currentWord = null,
                resultText = "🎉 Вы прошли все слова!"
            )
            usedWordIds.clear()
        }
    }

    fun resetStats() {
        _uiState.value = _uiState.value.copy(
            correctCount = 0,
            incorrectCount = 0,
            resultText = "🔄 Сброшено"
        )
    }
}
