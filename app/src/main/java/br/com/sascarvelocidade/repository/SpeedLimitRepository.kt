package br.com.sascarvelocidade.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.sascarvelocidade.dao.SpeedLimitDao
import br.com.sascarvelocidade.entity.SpeedLimit
import kotlinx.coroutines.flow.Flow

class SpeedLimitRepository(private val speedLimitDao: SpeedLimitDao) {

    val allSpeedLimits: Flow<List<SpeedLimit>> = speedLimitDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(speedLimit: SpeedLimit) {
        speedLimitDao.insert(speedLimit)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getByLocation(latitude: Double, longitude: Double) : LiveData<SpeedLimit> {
        return speedLimitDao.getByLocation(latitude, longitude)
    }
}
