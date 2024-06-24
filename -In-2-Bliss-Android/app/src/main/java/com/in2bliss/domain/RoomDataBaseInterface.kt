package com.in2bliss.domain

import androidx.room.Database
import com.in2bliss.data.roomDataBase.OfflineAudioEntity




interface RoomDataBaseInterface {

    /**
     * Inserting data in room data base
     * @param data
     * */
    suspend fun insert(data: OfflineAudioEntity)

    /**
     * Deleting the data from the room data base
     * @param data
     * */
    suspend fun delete(data: OfflineAudioEntity): Int

    /**
     * Getting download file list
     * */
    suspend fun getList(): List<OfflineAudioEntity>
}