package com.in2bliss.data.roomDataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OfflineAudioEntity::class], version = 2)
abstract class OfflineAudioDataBase : RoomDatabase() {
    abstract fun dataBaseDao(): OfflineAudioDao
}