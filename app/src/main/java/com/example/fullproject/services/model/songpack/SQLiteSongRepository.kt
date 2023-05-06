package com.example.fullproject.services.model.songpack

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Parcel
import android.util.Log
import androidx.core.content.contentValuesOf
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import com.example.fullproject.services.model.sqlite.AppSQLiteContract.SongTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SQLiteSongRepository(
    private val db: SQLiteDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : SongsRepository{

    override suspend fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>> {
        return flow{
            //querySongs(onlyActive)
            emit(querySongs(onlyActive))
        }.flowOn(ioDispatcher)
    }

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
            addToStackPlaying = cursor.getInt(cursor.getColumnIndexOrThrow(SongTable.COLUMN_AUTOPLAY_MARKER)) != 0
        )
    }

    override suspend fun createSongObject(uri: String,
                                          name: String?,
                                          author: String?,
                                          description: String?,
                                          addToStackPlaying: Boolean){
        db.insertWithOnConflict(
            SongTable.TABLE_NAME,
        null,
            contentValuesOf(
                SongTable.COLUMN_URI to uri,
                SongTable.TABLE_NAME to name,
                SongTable.COLUMN_AUTHOR to author,
                SongTable.COLUMN_DESCRIPTION to description,
                SongTable.COLUMN_AUTOPLAY_MARKER to addToStackPlaying
            ),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    override suspend fun updateSongUserName(id: Long, newSongName: String) {
        updateValueOfElementInTable(id, newSongName, null, null, null)
    }

    override suspend fun updateFlagItem(id: Long, activeState: Boolean) {
        updateValueOfElementInTable(id, null, null, null, activeState)
    }

    override suspend fun deleteSongObject(id: Long): Int = withContext(ioDispatcher){
        db.delete(SongTable.TABLE_NAME, "${SongTable.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    private fun updateValueOfElementInTable(id: Long,
                                            name: String?,
                                            author: String?,
                                            description: String?,
                                            addToStackPlaying: Boolean?){

        val mapWithUpdatedElement = mutableMapOf<String, Any>()
        if (name != null) mapWithUpdatedElement[SongTable.COLUMN_NAME] = name
        if (author != null) mapWithUpdatedElement[SongTable.COLUMN_AUTHOR] = author
        if (description != null) mapWithUpdatedElement[SongTable.COLUMN_DESCRIPTION] = description
        if (addToStackPlaying != null) mapWithUpdatedElement[SongTable.COLUMN_AUTOPLAY_MARKER] = if (addToStackPlaying) 1 else 0

        val parcel = Parcel.obtain()
        parcel.writeMap(mapWithUpdatedElement)
        parcel.setDataPosition(0)
        val content = ContentValues.CREATOR.createFromParcel(parcel)

        db.updateWithOnConflict(
            SongTable.TABLE_NAME,
            content,
            "${SongTable.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    override suspend fun findSongIdByURI(uri: String): Long {
        var id: Long = -1
        val sql = "SELECT *" +
                "FROM ${SongTable.TABLE_NAME}" + " WHERE ${SongTable.COLUMN_URI} = ?"
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
                    " FROM ${SongTable.TABLE_NAME}" + " WHERE ${SongTable.COLUMN_AUTOPLAY_MARKER} = 1"

            db.rawQuery(sql, null)
        }else{
            val sql = "SELECT * FROM ${SongTable.TABLE_NAME}"

            db.rawQuery(sql, null)
        }
    }
}