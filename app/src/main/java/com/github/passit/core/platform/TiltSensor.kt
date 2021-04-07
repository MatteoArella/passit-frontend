package com.github.passit.core.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface TiltListener {
    fun onTilt(pitchRollRad: Pair<Double, Double>)
}

class TiltSensor(context: Context): SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accSensor: Sensor
    private val magneticSensor: Sensor
    private val listeners = mutableListOf<TiltListener>()
    private val rotationMatrix = FloatArray(9)
    private val accelerometerValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private val orientationAngles = FloatArray(3)

    init {
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun addOnTiltListener(listener: TiltListener) {
        listeners.add(listener)
    }

    fun addOnTiltListener(listener: (Pair<Double, Double>) -> Unit) {
        listeners.add(object : TiltListener {
            override fun onTilt(pitchRollRad: Pair<Double, Double>) {
                listener(pitchRollRad)
            }
        })
    }

    fun register() {
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unregister() {
        listeners.clear()
        sensorManager.unregisterListener(this, accSensor)
        sensorManager.unregisterListener(this, magneticSensor)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, magneticValues.size)
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val pitchInRad = orientationAngles[1].toDouble()
        val rollInRad = orientationAngles[2].toDouble()

        val pair = Pair(pitchInRad, rollInRad)
        listeners.forEach {
            it.onTilt(pair)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}