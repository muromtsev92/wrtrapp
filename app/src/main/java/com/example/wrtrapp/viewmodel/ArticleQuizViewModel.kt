package com.example.wrtrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wrtrapp.data.DatabaseProvider
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.data.repository.NounRepository
import com.example.wrtrapp.ui.state.ArticleUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleQuizViewModel(application: Application) : AndroidViewModel(application) {

    // --- Репозиторий для работы с базой данных
    private val repository: NounRepository =
        NounRepository(DatabaseProvider.getDatabase(application).nounDao())

    // --- StateFlow для UI: храним текущее состояние игры
    private val _uiState = MutableStateFlow(ArticleUiState())
    val uiState: StateFlow<ArticleUiState> = _uiState

    // --- Список всех слов из базы и идентификаторы уже использованных слов
    private var allWords: List<NounEntity> = emptyList()
    private val usedWordIds = mutableSetOf<Long>()

    // --- Job для управления временем показа сообщений
    private var clearMessageJob: Job? = null

    /**
     * Метод для включения/выключения режима "до первой ошибки".
     * Вызывается при выборе режима на экране игры или главном экране.
     */
    fun setStopOnError(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            stopOnError = enabled
        )
        startNewGame()
    }


    /**
     * Загружаем все слова из базы и показываем первое случайное
     */
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

    /**
     * Проверка ответа пользователя
     *
     * - При правильном ответе:
     *      • увеличиваем счёт правильных
     *      • сразу показываем новое слово
     *      • никаких сообщений
     *
     * - При неправильном ответе:
     *      • если stopOnError включён — завершаем игру (currentWord = null, выводим счёт)
     *      • иначе показываем сообщение "Неправильно" на 2 секунды
     *      • при повторной ошибке таймер обнуляется и стартует заново
     *      • новое слово показываем сразу
     */
    fun checkAnswer(choice: String) {
        val currentWord = _uiState.value.currentWord ?: return
        val correct = choice == currentWord.article

        val translationPart = currentWord.translation?.let { " – $it" } ?: ""

        val feedback = if (correct) {
            "Правильно! ${currentWord.article} ${currentWord.word}$translationPart"
        } else {
            "Неправильно. Было: ${currentWord.article} ${currentWord.word}$translationPart"
        }

        _uiState.value = _uiState.value.copy(
            resultText = feedback,
            correctCount = _uiState.value.correctCount + if (correct) 1 else 0,
            incorrectCount = _uiState.value.incorrectCount + if (correct) 0 else 1,
            mistakes = if (correct) _uiState.value.mistakes else _uiState.value.mistakes + currentWord
        )

        // --- Проверка режима "до первой ошибки"
        if (_uiState.value.stopOnError && !correct) {
            _uiState.value = _uiState.value.copy(showMistakesScreen = true)
            return
        }

        nextWord()

        clearMessageJob?.cancel()
        clearMessageJob = viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(resultText = "")
        }
    }


    /**
     * Переход к следующему слову
     * - Выбираем случайное слово из ещё не использованных
     * - Если все слова использованы — начинаем новый цикл
     */
    fun nextWord() {
        val unused = allWords.filterNot { usedWordIds.contains(it.id) }

        if (unused.isNotEmpty()) {
            // Берём случайное слово из неиспользованных
            val next = unused.random()
            usedWordIds.add(next.id)
            _uiState.value = _uiState.value.copy(currentWord = next)
        } else {
            // --- ВСЕ слова пройдены
            _uiState.value = _uiState.value.copy(
                currentWord = null,
                resultText = "🎉 Все слова угаданы! Нажмите Сброс, чтобы начать заново."
            )
        }
    }


    /**
     * Сброс статистики игры (правильные/неправильные ответы)
     * и переход к следующему слову
     */
    fun resetStats() {
        // Показываем экран ошибок, если они есть
        _uiState.value = _uiState.value.copy(
            showMistakesScreen = true
        )
    }

    fun startNewGame() {
        usedWordIds.clear()
        _uiState.value = _uiState.value.copy(
            correctCount = 0,
            incorrectCount = 0,
            mistakes = emptyList(),
            resultText = "",
            showMistakesScreen = false
        )
        loadWords()
    }
}
