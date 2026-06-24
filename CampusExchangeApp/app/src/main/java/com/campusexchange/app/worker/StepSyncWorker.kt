package com.campusexchange.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.campusexchange.app.data.repository.Result
import com.campusexchange.app.data.repository.StepRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StepSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val stepRepository: StepRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val localSteps = stepRepository.getLocalStepsOnce()
            val todayStepCount = localSteps?.todayStepCount ?: 0
            val syncCount = localSteps?.syncCount ?: 0
            val unconvertedSteps = localSteps?.unconvertedSteps ?: 0

            val newSteps = todayStepCount - syncCount
            val updatedUnconvertedSteps = unconvertedSteps + newSteps

            when (val syncResult = stepRepository.syncDailyStepsToBackend(todayStepCount)) {
                is com.campusexchange.app.data.repository.Result.Success -> {
                    // Sync the accumulated unconvertedSteps to the backend
                    stepRepository.syncStepsToBackend(updatedUnconvertedSteps)

                    // Update local DB
                    stepRepository.updateUnconvertedSteps(updatedUnconvertedSteps)
                    stepRepository.updateTodayStepCount(0)
                    stepRepository.updateSyncCount(0)
                    stepRepository.setBaseline(-1)
                    Result.success()
                }
                is com.campusexchange.app.data.repository.Result.Error -> {
                    if (runAttemptCount < 3) Result.retry() else Result.failure()
                }
                else -> Result.failure()
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
