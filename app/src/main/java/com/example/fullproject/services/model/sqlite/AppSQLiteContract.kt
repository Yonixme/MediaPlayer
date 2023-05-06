package com.example.fullproject.services.model.sqlite

object AppSQLiteContract {
    object SongTable{
        const val TABLE_NAME = "songs"
        const val COLUMN_ID = "id"
        const val COLUMN_URI = "uri"
        const val COLUMN_NAME = "name"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_AUTOPLAY_MARKER = "is_autoplay_marker"
    }

    object DirTable{
        const val TABLE_NAME = "directories"
        const val COLUMN_ID = "id"
        const val COLUMN_URI = "uri"
        const val COLUMN_NAME = "name_dir"
        const val COLUMN_ADD_IN_LIST_MARKER = "is_add_in_list_marker"
        const val COLUMN_DEFAULT_DIR_MARKER = "is_default_dir"
    }
}