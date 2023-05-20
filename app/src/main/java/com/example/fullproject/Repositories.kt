package com.example.fullproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.fullproject.model.sqlite.dirpack.DirRepository
import com.example.fullproject.model.sqlite.dirpack.SQLiteDirRepository
import com.example.fullproject.model.sqlite.songpack.SQLiteMetaSongRepository
import com.example.fullproject.model.sqlite.songpack.MetaSongsRepository
import com.example.fullproject.model.sqlite.AppSQLiteHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {
    private lateinit var applicationContext: Context

    private val database: SQLiteDatabase by lazy<SQLiteDatabase>{
        AppSQLiteHelper(applicationContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    val metaSongsRepository: MetaSongsRepository by lazy {
        SQLiteMetaSongRepository(database, ioDispatcher)
    }

    val dirRepository: DirRepository by lazy {
        SQLiteDirRepository(database, ioDispatcher)
    }

    fun init(context: Context){
        applicationContext = context
    }
}
