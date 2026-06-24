package com.campusexchange.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,   // single-row table for current user's today steps
    val todayStepCount: Int = 0,
    val syncCount: Int = 0,
    val unconvertedSteps: Int = 0,
    val lastSyncedAt: Long = 0L,
    val sensorBaseline: Int = 0  // sensor's step count at day start
)
