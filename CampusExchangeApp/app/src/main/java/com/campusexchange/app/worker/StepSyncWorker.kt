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
            val count = localSteps?.stepsCount ?: 0

            when (val syncResult = stepRepository.syncDailyStepsToBackend(count)) {
                is com.campusexchange.app.data.repository.Result.Success -> {
                    // Reset baseline after successful daily sync (new day = new baseline)
                    stepRepository.setBaseline(-1)
                    stepRepository.updateLocalStepCount(0)
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
