package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min

data class ParallaxOffset(
    val roll: Float = 0f,  // -1f (tilted left) to 1f (tilted right)
    val pitch: Float = 0f, // -1f (tilted up) to 1f (tilted down)
    val rawX: Float = 0f,
    val rawY: Float = 0f
)

class GyroscopeManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _parallaxOffset = MutableStateFlow(ParallaxOffset())
    val parallaxOffset: StateFlow<ParallaxOffset> = _parallaxOffset.asStateFlow()

    private var currentRoll = 0f
    private var currentPitch = 0f
    private val smoothingFactor = 0.12f

    fun startListening() {
        rotationSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                // orientation[2] is roll (-pi to pi), orientation[1] is pitch (-pi/2 to pi/2)
                val targetRoll = (orientation[2] / (Math.PI.toFloat() / 3f)).coerceIn(-1.5f, 1.5f)
                val targetPitch = ((orientation[1] - 0.7f) / (Math.PI.toFloat() / 3f)).coerceIn(-1.5f, 1.5f)

                currentRoll += (targetRoll - currentRoll) * smoothingFactor
                currentPitch += (targetPitch - currentPitch) * smoothingFactor

                _parallaxOffset.value = ParallaxOffset(
                    roll = currentRoll.coerceIn(-1f, 1f),
                    pitch = currentPitch.coerceIn(-1f, 1f),
                    rawX = targetRoll,
                    rawY = targetPitch
                )
            }
            Sensor.TYPE_GRAVITY, Sensor.TYPE_ACCELEROMETER -> {
                val targetRoll = (-event.values[0] / 5f).coerceIn(-1.5f, 1.5f)
                val targetPitch = ((event.values[1] - 5f) / 5f).coerceIn(-1.5f, 1.5f)

                currentRoll += (targetRoll - currentRoll) * smoothingFactor
                currentPitch += (targetPitch - currentPitch) * smoothingFactor

                _parallaxOffset.value = ParallaxOffset(
                    roll = currentRoll.coerceIn(-1f, 1f),
                    pitch = currentPitch.coerceIn(-1f, 1f),
                    rawX = targetRoll,
                    rawY = targetPitch
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    fun updateManualOffset(roll: Float, pitch: Float) {
        currentRoll = roll.coerceIn(-1f, 1f)
        currentPitch = pitch.coerceIn(-1f, 1f)
        _parallaxOffset.value = ParallaxOffset(
            roll = currentRoll,
            pitch = currentPitch,
            rawX = roll,
            rawY = pitch
        )
    }
}
