package com.example.fullproject.services.model.dirpack

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Parcel
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.sqlite.AppSQLiteContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SQLiteDirRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: CoroutineDispatcher): DirRepository {

    override suspend fun getDirList(onlyActive: Boolean): Flow<List<Dir>> {
        return flow{
            //querySongs(onlyActive)
            emit(queryDirs(onlyActive))
        }.flowOn(ioDispatcher)
    }

    private fun queryDirs(onlyActive: Boolean): List<Dir>{
        val cursor = queryDirsOfCursor(onlyActive)

        return cursor.use {
            val list = mutableListOf<Dir>()
            while (cursor.moveToNext()){
                list.add(parseDir(it))
            }
            return@use list
        }
    }

    private fun parseDir(cursor: Cursor): Dir {
        return Dir(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_ID)),
            uri = cursor.getString(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_URI)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_NAME)),
            addToStackPlaying = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER)) != 0,
            isPrimaryDir = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteContract.DirTable.COLUMN_DEFAULT_DIR_MARKER)) != 0
        )
    }

    private fun queryDirsOfCursor(onlyActive: Boolean): Cursor {
        return if (onlyActive){
            val sql = "SELECT *" +
                    " FROM ${AppSQLiteContract.DirTable.TABLE_NAME}" + " WHERE ${AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER} = 1"

            db.rawQuery(sql, null)
        }else{
            val sql = "SELECT * FROM ${AppSQLiteContract.DirTable.TABLE_NAME}"

            db.rawQuery(sql, null)
        }
    }

    override suspend fun updateDirName(id: Long, newDirName: String) {
        updateValueOfElementInTable(id, newDirName, null)
    }

    override suspend fun updateFlagItem(id: Long, activeState: Boolean) {
        updateValueOfElementInTable(id, null, activeState)
    }

    private fun updateValueOfElementInTable(id: Long,
                                            name: String?,
                                            addToStackPlaying: Boolean?){

        val mapWithUpdatedElement = mutableMapOf<String, Any>()
        if (name != null) mapWithUpdatedElement[AppSQLiteContract.DirTable.COLUMN_NAME] = name
        if (addToStackPlaying != null) mapWithUpdatedElement[AppSQLiteContract.DirTable.COLUMN_ADD_IN_LIST_MARKER] = if (addToStackPlaying) 1 else 0

        val parcel = Parcel.obtain()
        parcel.writeMap(mapWithUpdatedElement)
        parcel.setDataPosition(0)
        val content = ContentValues.CREATOR.createFromParcel(parcel)

        db.updateWithOnConflict(
            AppSQLiteContract.DirTable.TABLE_NAME,
            content,
            "${AppSQLiteContract.DirTable.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    override suspend fun createDirObject(
        uri: String,
        name: String?,
        addToStackPlaying: Boolean?,
        isPrimaryDir: Boolean?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDirObject(id: Long): Int {
        TODO("Not yet implemented")
    }

}