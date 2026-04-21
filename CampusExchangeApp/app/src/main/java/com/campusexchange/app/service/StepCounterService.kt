package com.campusexchange.app.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.campusexchange.app.R
import com.campusexchange.app.data.repository.StepRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var stepRepository: StepRepository

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var sensorBaseline: Int = -1
    private var currentSteps: Int = 0
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val NOTIFICATION_ID = 1001
        fun startService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.startForegroundService(intent)
        }
        fun stopService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        startForeground(NOTIFICATION_ID, buildNotification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        // Load the last saved step count and baseline
        serviceScope.launch {
            val saved = stepRepository.getLocalStepsOnce()
            if (saved != null) {
                currentSteps = saved.stepsCount
                sensorBaseline = saved.sensorBaseline
            }
        }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val sensorValue = event.values[0].toInt()

            if (sensorBaseline == -1) {
                // First reading: set baseline
                sensorBaseline = sensorValue
                serviceScope.launch {
                    stepRepository.setBaseline(sensorBaseline)
                }
            }

            currentSteps = sensorValue - sensorBaseline
            if (currentSteps < 0) currentSteps = 0

            updateNotification(currentSteps)
            serviceScope.launch {
                stepRepository.updateLocalStepCount(currentSteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(steps: Int): Notification {
        return NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setContentTitle(getString(R.string.step_counter_notification_title))
            .setContentText("${steps.formatSteps()} steps today → ${steps / 10} coins")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(steps))
    }

    private fun Int.formatSteps(): String =
        if (this >= 1000) "${this / 1000},${String.format("%03d", this % 1000)}"
        else toString()
}
