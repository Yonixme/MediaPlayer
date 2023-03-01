package com.example.fullproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.fullproject.businesslogic.sqlite.AppSQLiteHelper
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