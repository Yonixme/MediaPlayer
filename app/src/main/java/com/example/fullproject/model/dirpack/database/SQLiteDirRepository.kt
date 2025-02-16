package com.example.fullproject.model.dirpack.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.example.fullproject.di.IoDispatcher
import com.example.fullproject.model.dirpack.entities.Directory
import com.example.fullproject.model.database.AppSQLiteContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SQLiteDirRepository @Inject constructor(
    private val db: SQLiteDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DbDirRepository {

    override suspend fun getDirList(onlyActive: Boolean): Flow<List<Directory>> {
        return flow{
            emit(queryDirs(onlyActive))
        }.flowOn(ioDispatcher)
    }

    private fun queryDirs(onlyActive: Boolean): List<Directory>{
        val cursor = queryDirsOfCursor(onlyActive)

        return cursor.use {
            val list = mutableListOf<Directory>()
            while (cursor.moveToNext()){
                list.add(parseDir(it))
            }
            return@use list
        }
    }

    private fun parseDir(cursor: Cursor): Directory {
        return Directory(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_ID)),
            uri = cursor.getString(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_URI)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_NAME)),
            disEnableForReading = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER)) != 0,
            isDefaultDir = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_DEFAULT_DIR_MARKER)) != 0
        )
    }

    private fun queryDirsOfCursor(onlyActive: Boolean): Cursor {
        return if (onlyActive){
            val sql = "SELECT *" +
                    " FROM ${AppSQLiteContract.DirTable.TABLE_NAME}" +
                    " WHERE ${AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER} = 1"
            db.rawQuery(sql, null)
        }else{
            val sql = "SELECT * FROM ${AppSQLiteContract.DirTable.TABLE_NAME}"
            db.rawQuery(sql, null)
        }
    }

    override suspend fun updateDirName(id: Long, newDirName: String) {
        updateValueOfElementInTable(id, newDirName, null)
    }

    override suspend fun setReadFlag(id: Long, readFlag: Boolean) {
        updateValueOfElementInTable(id, null, readFlag)
    }

    private fun updateValueOfElementInTable(
        id: Long,
        name: String?,
        addToStackPlaying: Boolean?){
        val mapWithUpdatedElement = mutableMapOf<String, Any>()

        if (name != null) mapWithUpdatedElement[AppSQLiteContract.DirTable.COLUMN_NAME] = name
        if (addToStackPlaying != null) mapWithUpdatedElement[AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER] = if (addToStackPlaying) 1 else 0

        val content = ContentValues(mapWithUpdatedElement.size)
        for (nameFromDb in mapWithUpdatedElement.keys){
            if (nameFromDb == AppSQLiteContract.DirTable.COLUMN_NAME)
                content.put(nameFromDb, mapWithUpdatedElement[nameFromDb].toString())
            if (nameFromDb == AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER)
                content.put(nameFromDb, (mapWithUpdatedElement[nameFromDb] as Int))
        }
        db.update(
            AppSQLiteContract.DirTable.TABLE_NAME,
            content,
            "${AppSQLiteContract.DirTable.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override suspend fun createDirObject(
        uri: String,
        name: String?,
        addToStackPlaying: Boolean?,
        isPrimaryDir: Boolean?
    ) {
        db.insert(
            AppSQLiteContract.DirTable.TABLE_NAME,
            null,
            contentValuesOf(
                AppSQLiteContract.DirTable.COLUMN_URI to uri,
                AppSQLiteContract.DirTable.COLUMN_NAME to name,
                AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER to addToStackPlaying,
                AppSQLiteContract.DirTable.COLUMN_DEFAULT_DIR_MARKER to isPrimaryDir
            )
        )
    }

    override suspend fun deleteDirObject(id: Long){
        db.delete(
            AppSQLiteContract.DirTable.TABLE_NAME,
            "${AppSQLiteContract.SongTable.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
}