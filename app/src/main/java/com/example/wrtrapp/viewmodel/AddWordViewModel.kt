package com.example.wrtrapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wrtrapp.data.DatabaseProvider
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.data.repository.NounRepository
import kotlinx.coroutines.launch

class AddWordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NounRepository(DatabaseProvider.getDatabase(application).nounDao())

    fun addNoun(word: String, article: String, pluralForm: String, translation: String?, pluralTranslation: String?) {
        viewModelScope.launch {
            repository.insertAll(
                listOf(
                    NounEntity(
                        word = word,
                        article = article,
                        pluralForm = pluralForm,
                        translation = translation,
                        pluralTranslation = pluralTranslation
                    )
                )
            )
        }
    }
}
