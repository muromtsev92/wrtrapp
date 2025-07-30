package com.example.wrtrapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wrtrapp.data.entities.NounEntity
import com.example.wrtrapp.data.repository.NounRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ImportWordsViewModel(
    private val repository: NounRepository
) : ViewModel() {

    fun importWords(json: String, onResult: (Boolean) -> Unit) {
        try {
            val gson = Gson()
            val nouns = gson.fromJson(json, Array<NounEntity>::class.java).toList()

            viewModelScope.launch {
                repository.insertAll(nouns)
                onResult(true)
            }
        } catch (e: Exception) {
            onResult(false)
        }
    }
}
