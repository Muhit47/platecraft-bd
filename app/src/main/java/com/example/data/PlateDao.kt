package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlateDao {
    @Query("SELECT * FROM saved_plates ORDER BY createdAt DESC")
    fun getAllPlates(): Flow<List<PlateEntity>>

    @Query("SELECT * FROM saved_plates WHERE id = :id LIMIT 1")
    suspend fun getPlateById(id: Long): PlateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlate(plate: PlateEntity): Long

    @Update
    suspend fun updatePlate(plate: PlateEntity)

    @Delete
    suspend fun deletePlate(plate: PlateEntity)

    @Query("DELETE FROM saved_plates WHERE id = :id")
    suspend fun deletePlateById(id: Long)
}
