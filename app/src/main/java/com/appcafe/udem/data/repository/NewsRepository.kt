package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.NewsDao
import com.appcafe.udem.data.local.entities.NoticiaEvento

class NewsRepository(private val newsDao: NewsDao) {

    fun getAllNoticias(): LiveData<List<NoticiaEvento>> =
        newsDao.getAllNoticias()

    fun getNoticiaById(id: Int): LiveData<NoticiaEvento> =
        newsDao.getNoticiaById(id)

    suspend fun agregarNoticia(noticiaEvento: NoticiaEvento) {
        newsDao.insertNoticia(noticiaEvento)
    }

    suspend fun guardarNoticias(noticias: List<NoticiaEvento>) {
        newsDao.insertAll(noticias)
    }

    suspend fun eliminarNoticia(noticiaEvento: NoticiaEvento) {
        newsDao.deleteNoticia(noticiaEvento)
    }
}
