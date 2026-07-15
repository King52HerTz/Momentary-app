package com.eliauk.momentary.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        MomentEntity::class,
        ChatMessageEntity::class,
        PersonaEntity::class,
        DiarySessionEntity::class,
        DiarySessionSourceEntity::class,
        DiaryDraftEntity::class,
        JournalEntity::class,
        FavoriteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class MomentaryDatabase : RoomDatabase() {
    abstract fun momentDao(): MomentDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun personaDao(): PersonaDao
    abstract fun diaryDao(): DiaryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "momentary.db"

        fun build(context: Context): MomentaryDatabase = Room.databaseBuilder(
            context.applicationContext,
            MomentaryDatabase::class.java,
            DATABASE_NAME,
        ).build()
    }
}
