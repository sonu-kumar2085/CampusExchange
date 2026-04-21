package com.campusexchange.app.data.repository

import com.campusexchange.app.data.local.StepDao
import com.campusexchange.app.data.local.StepEntity
import com.campusexchange.app.data.remote.ApiService
import com.campusexchange.app.data.remote.dto.StepsDto
import com.campusexchange.app.data.remote.dto.UpdateStepsRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepository @Inject constructor(
    private val api: ApiService,
    private val stepDao: StepDao
) {
    // Local step operations
    fun getLocalSteps(): Flow<StepEntity?> = stepDao.getSteps()

    suspend fun getLocalStepsOnce(): StepEntity? = stepDao.getStepsOnce()

    suspend fun updateLocalStepCount(count: Int) {
        val existing = stepDao.getStepsOnce()
        if (existing == null) {
            stepDao.insertOrReplace(StepEntity(id = 1, stepsCount = count))
        } else {
            stepDao.updateStepCount(count)
        }
    }

    suspend fun setBaseline(baseline: Int) {
        val existing = stepDao.getStepsOnce()
        if (existing == null) {
            stepDao.insertOrReplace(StepEntity(id = 1, sensorBaseline = baseline))
        } else {
            stepDao.updateBaseline(baseline)
        }
    }

    suspend fun markSynced() {
        stepDao.updateLastSynced(System.currentTimeMillis())
    }

    // Remote step operations - get from server
    suspend fun getRemoteSteps(): Result<StepsDto> {
        return try {
            val response = api.getSteps()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                Result.Error("Failed to load steps", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    // Sync local steps to backend
    suspend fun syncStepsToBackend(stepsCount: Int): Result<StepsDto> {
        return try {
            val response = api.updateSteps(UpdateStepsRequest(stepsCount))
            if (response.isSuccessful) {
                markSynced()
                Result.Success(response.body()!!.data)
            } else {
                val msg = response.errorBody()?.string() ?: "Sync failed"
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}
