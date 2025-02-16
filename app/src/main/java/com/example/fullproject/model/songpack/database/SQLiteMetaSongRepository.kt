package com.example.fullproject.model.songpack.database

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.example.fullproject.di.IoDispatcher
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.model.database.AppSQLiteContract.SongTable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SQLiteMetaSongRepository @Inject constructor(
    private val db: SQLiteDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DbMetaSongsRepository {
    private val reconstructFlow = MutableSharedFlow<Unit>(replay = 1).also { it.tryEmit(Unit) }

    override fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>> {
        return flow{
            emit(querySongs(onlyActive))
        }.flowOn(ioDispatcher)
    }

//    override fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>> {
//        return combine(reconstructFlow){ _ ->
//            querySongs(onlyActive)
//        }.flowOn(ioDispatcher)
//    }

    private fun querySongs(onlyActive: Boolean): List<MetaDataSong>{
        val cursor = querySongsOfCursor(onlyActive)

        return cursor.use {
            val list = mutableListOf<MetaDataSong>()
            while (cursor.moveToNext()){
                list.add(parseSongs(it))
            }
            return@use list
        }
    }

    private fun parseSongs(cursor: Cursor): MetaDataSong {
        return MetaDataSong(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(SongTable.COLUMN_ID)),
            uri = cursor.getString(cursor.getColumnIndexOrThrow(SongTable.COLUMN_URI)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(SongTable.COLUMN_NAME)),
            author = cursor.getString(cursor.getColumnIndexOrThrow(SongTable.COLUMN_AUTHOR)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(SongTable.COLUMN_DESCRIPTION)),
            addToStackPlaying = cursor.getInt(cursor.getColumnIndexOrThrow(SongTable.COLUMN_PLAY_MARKER)) != 0
        )
    }

    override suspend fun createSongObject(
        uri: String,
        name: String?,
        author: String?,
        description: String?,
        autoPlayFlag: Boolean)
    {
        db.insert(
            SongTable.TABLE_NAME,
        null,
            contentValuesOf(
                SongTable.COLUMN_URI to uri,
                SongTable.COLUMN_NAME to name,
                SongTable.COLUMN_AUTHOR to author,
                SongTable.COLUMN_DESCRIPTION to description,
                SongTable.COLUMN_PLAY_MARKER to autoPlayFlag
            )
        )
        //updateTableInDb()
    }

    override suspend fun updateSongUserName(id: Long, newSongName: String): Unit = withContext(ioDispatcher) {
        updateValueOfElementInTable(
            id = id,
            name = newSongName,
            author = null,
            description = null,
            addToStackPlaying = null)
        //updateTableInDb()
    }

    override suspend fun setAutoPlayFlag(id: Long, autoPlayFlag: Boolean) {
        updateValueOfElementInTable(
            id = id,
            name = null,
            author = null,
            description = null,
            addToStackPlaying = autoPlayFlag
        )
        //updateTableInDb()
    }

    override suspend fun deleteSongObject(id: Long) {
        db.delete(
            SongTable.TABLE_NAME,
            "${SongTable.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        //updateTableInDb()
    }

    private fun updateValueOfElementInTable(
        id: Long,
        name: String?,
        author: String?,
        description: String?,
        addToStackPlaying: Boolean?){
        val mapWithUpdatedElement = mutableMapOf<String, Any>()
        if (name != null) mapWithUpdatedElement[SongTable.COLUMN_NAME] = name
        if (author != null) mapWithUpdatedElement[SongTable.COLUMN_AUTHOR] = author
        if (description != null) mapWithUpdatedElement[SongTable.COLUMN_DESCRIPTION] = description
        if (addToStackPlaying != null) mapWithUpdatedElement[SongTable.COLUMN_PLAY_MARKER] = if (addToStackPlaying) 1 else 0

        val content = ContentValues(mapWithUpdatedElement.size)
        for (key in mapWithUpdatedElement.keys) {
            if (key == SongTable.COLUMN_PLAY_MARKER)
                content.put(key, (mapWithUpdatedElement[key] as Int))
            else
                content.put(key, mapWithUpdatedElement[key].toString())
        }
        db.updateWithOnConflict(
            SongTable.TABLE_NAME,
            content,
            "${SongTable.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            SQLiteDatabase.CONFLICT_IGNORE
        )
        //updateTableInDb()
    }

    override suspend fun findSongIdByURI(uri: String): Long {
        var id: Long = -1
        val sql = "SELECT *" +
                "FROM ${SongTable.TABLE_NAME}" +
                " WHERE ${SongTable.COLUMN_URI} = ?"
        db.rawQuery(sql, arrayOf(uri)).use {
            while (it.moveToNext()){
                id = parseSongs(it).id
            }
        }
        return id
    }


    private fun querySongsOfCursor(onlyActive: Boolean): Cursor {
        return if (onlyActive){
            val sql = "SELECT *" +
                    " FROM ${SongTable.TABLE_NAME}" +
                    " WHERE ${SongTable.COLUMN_PLAY_MARKER} = 1"
            db.rawQuery(sql, null)
        }else{
            val sql = "SELECT * FROM ${SongTable.TABLE_NAME}"
            db.rawQuery(sql, null)
        }
    }

    private fun updateTableInDb(){
        reconstructFlow.tryEmit(Unit)
    }
}