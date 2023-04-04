package com.example.fullproject.businesslogic.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {
    private lateinit var applicationContext: Context

    private val database: SQLiteDatabase by lazy<SQLiteDatabase>{
        AppSQLiteHelper(applicationContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    fun init(context: Context){
        applicationContext = context
    }
}