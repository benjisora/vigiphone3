package com.cstb.vigiphone3.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listenToAllSensors();
        return Service.START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sendMessageToActivity(sensorEvent.sensor.getType(), sensorEvent.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
    }

    public void listenToAllSensors() {
        listenToSensorIfChecked(Sensor.TYPE_ACCELEROMETER);
        listenToSensorIfChecked(Sensor.TYPE_GYROSCOPE);
        listenToSensorIfChecked(Sensor.TYPE_MAGNETIC_FIELD);
        listenToSensorIfChecked(Sensor.TYPE_LIGHT);
        listenToSensorIfChecked(Sensor.TYPE_PROXIMITY);
    }

    public void listenToSensorIfChecked(int sensor) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (sensor) {
            case Sensor.TYPE_ACCELEROMETER:
                if (SP.getBoolean("accelerometer", true)) {
                    Sensor accelerometerSensor = sensorManager.getDefaultSensor(sensor);
                    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                if (SP.getBoolean("gyroscope", true)) {
                    Sensor gyroscopeSensor = sensorManager.getDefaultSensor(sensor);
                    sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (SP.getBoolean("magneticField", true)) {
                    Sensor magneticFieldSensor = sensorManager.getDefaultSensor(sensor);
                    sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
            case Sensor.TYPE_LIGHT:
                if (SP.getBoolean("light", true)) {
                    Sensor lightSensor = sensorManager.getDefaultSensor(sensor);
                    sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
            case Sensor.TYPE_PROXIMITY:
                if (SP.getBoolean("proximity", true)) {
                    Sensor proximitySensor = sensorManager.getDefaultSensor(sensor);
                    sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
        }
    }

    public void sendMessageToActivity(int type, float[] value){
        Intent intent = new Intent("SensorChanged");
        intent.putExtra("type", type);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
