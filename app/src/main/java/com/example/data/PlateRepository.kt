package com.example.data

import kotlinx.coroutines.flow.Flow

class PlateRepository(private val plateDao: PlateDao) {
    val allPlates: Flow<List<PlateEntity>> = plateDao.getAllPlates()

    suspend fun insert(plate: PlateEntity): Long = plateDao.insertPlate(plate)

    suspend fun update(plate: PlateEntity) = plateDao.updatePlate(plate)

    suspend fun delete(plate: PlateEntity) = plateDao.deletePlate(plate)

    suspend fun deleteById(id: Long) = plateDao.deletePlateById(id)
}
