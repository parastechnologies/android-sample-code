package com.in2bliss.data.roomDataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OfflineAudioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audio: OfflineAudioEntity)

    @Delete
    suspend fun delete(audio: OfflineAudioEntity): Int

    @Query("select * from offline_audio ORDER BY Id DESC ")
    suspend fun getAudioList(): List<OfflineAudioEntity>
}