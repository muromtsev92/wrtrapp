package com.example.wrtrapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wrtrapp.data.DatabaseProvider
import com.example.wrtrapp.data.repository.NounRepository

class ImportWordsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = NounRepository(DatabaseProvider.getDatabase(context).nounDao())
        return ImportWordsViewModel(repository) as T
    }
}
