package com.example.wrtrapp.data.repository

import com.example.wrtrapp.data.dao.NounDao
import com.example.wrtrapp.data.entities.NounEntity

class NounRepository(private val nounDao: NounDao) {

    suspend fun getAllNouns(): List<NounEntity> {
        return nounDao.getAll()
    }

    suspend fun insertNoun(noun: NounEntity) {
        nounDao.insert(noun)
    }

    suspend fun insertAll(nouns: List<NounEntity>) {
        nouns.forEach { nounDao.insert(it) }
    }

    suspend fun findByWord(word: String): NounEntity? {
        return nounDao.findByWord(word)
    }

    suspend fun deleteAll() {
        nounDao.clear()
    }
}
