package com.campusexchange.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Query("SELECT * FROM steps WHERE id = 1")
    fun getSteps(): Flow<StepEntity?>

    @Query("SELECT * FROM steps WHERE id = 1")
    suspend fun getStepsOnce(): StepEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(step: StepEntity)

    @Query("UPDATE steps SET stepsCount = :count WHERE id = 1")
    suspend fun updateStepCount(count: Int)

    @Query("UPDATE steps SET sensorBaseline = :baseline WHERE id = 1")
    suspend fun updateBaseline(baseline: Int)

    @Query("UPDATE steps SET lastSyncedAt = :timestamp WHERE id = 1")
    suspend fun updateLastSynced(timestamp: Long)
}
