package com.example.eventify;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 3f; // You can adjust this value based on your requirements
    private static final int SHAKE_TIME_INTERVAL = 500; // Minimum time between two shakes in milliseconds
    private static final int SHAKE_COUNT_THRESHOLD = 5;
    private long lastShakeTime;
    private int shakeCount;
    private OnShakeListener onShakeListener;

    public ShakeDetector(OnShakeListener listener) {
        this.onShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastShakeTime > SHAKE_TIME_INTERVAL) {
                    lastShakeTime = currentTime;
                    shakeCount++;

                    if (shakeCount >= SHAKE_COUNT_THRESHOLD) {
                        onShakeListener.onShake();
                        shakeCount = 0; // Reset the shake count
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used, but required to implement SensorEventListener
    }

    public interface OnShakeListener {
        void onShake();
    }
}

