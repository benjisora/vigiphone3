package com.cstb.vigiphone3.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cstb.vigiphone3.data.database.MyApplication;

/**
 * Service listening for the Cell Infos, and notifying the ServiceManger when needed
 */
public class SensorService extends Service implements SensorEventListener {

    private static float[] accelerometerMax = {1000, 1000, 1000};
    private static float[] accelerometerMin = {1000, 1000, 1000};
    private static float[] gyroscopeMax = {1000, 1000, 1000};
    private static float[] gyroscopeMin = {1000, 1000, 1000};
    private static float[] magneticFieldMax = {1000, 1000, 1000};
    private static float[] magneticFieldMin = {1000, 1000, 1000};
    private SensorManager sensorManager;
    private SharedPreferences SP;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("SensorService", "Service started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listenToAllSensors();
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return Service.START_STICKY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets the value from the sensors, calibrates it if needed,
     * and sends the value to ServiceManager.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        boolean calibrationModeActivated = SP.getBoolean("modeCalibrate", false);
        boolean hasAlreadyBeenCalibrated = SP.getBoolean("hasCalibrated", false);

        if (calibrationModeActivated) {
            if (!hasAlreadyBeenCalibrated) {
                getCalibratedData(sensorEvent);
            }
            sendCalibratedData(sensorEvent);
        } else {
            sendMessageToActivity(sensorEvent.sensor.getType(), sensorEvent.values);
        }
    }

    /**
     * Samples the data to reduce the noise when the phone is still.
     *
     * @param sensorEvent the sensor object gotten from the Listener.
     */
    private void getCalibratedData(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:

                if (accelerometerMax[0] == 1000 && accelerometerMin[0] == 1000) {
                    accelerometerMax[0] = sensorEvent.values[0];
                    accelerometerMin[0] = sensorEvent.values[0];
                }

                if (accelerometerMax[1] == 1000 && accelerometerMin[1] == 1000) {
                    accelerometerMax[1] = sensorEvent.values[1];
                    accelerometerMin[1] = sensorEvent.values[1];
                }

                if (accelerometerMax[2] == 1000 && accelerometerMin[2] == 1000) {
                    accelerometerMax[2] = sensorEvent.values[2];
                    accelerometerMin[2] = sensorEvent.values[2];
                }

                if (accelerometerMax[0] != 1000 && accelerometerMin[0] != 1000 &&
                        accelerometerMax[1] != 1000 && accelerometerMin[1] != 1000 &&
                        accelerometerMax[2] != 1000 && accelerometerMin[2] != 1000) {

                    accelerometerMax[0] = Math.max(sensorEvent.values[0], accelerometerMax[0]);
                    accelerometerMin[0] = Math.min(sensorEvent.values[0], accelerometerMin[0]);

                    accelerometerMax[1] = Math.max(sensorEvent.values[1], accelerometerMax[1]);
                    accelerometerMin[1] = Math.min(sensorEvent.values[1], accelerometerMin[1]);

                    accelerometerMax[2] = Math.max(sensorEvent.values[2], accelerometerMax[2]);
                    accelerometerMin[2] = Math.min(sensorEvent.values[2], accelerometerMin[2]);

                }
                break;

            case Sensor.TYPE_GYROSCOPE:

                if (gyroscopeMax[0] == 1000 && gyroscopeMin[0] == 1000) {
                    gyroscopeMax[0] = sensorEvent.values[0];
                    gyroscopeMin[0] = sensorEvent.values[0];
                }

                if (gyroscopeMax[1] == 1000 && gyroscopeMin[1] == 1000) {
                    gyroscopeMax[1] = sensorEvent.values[1];
                    gyroscopeMin[1] = sensorEvent.values[1];
                }

                if (gyroscopeMax[2] == 1000 && gyroscopeMin[2] == 1000) {
                    gyroscopeMax[2] = sensorEvent.values[2];
                    gyroscopeMin[2] = sensorEvent.values[2];
                }

                if (gyroscopeMax[0] != 1000 && gyroscopeMin[0] != 1000 &&
                        gyroscopeMax[1] != 1000 && gyroscopeMin[1] != 1000 &&
                        gyroscopeMax[2] != 1000 && gyroscopeMin[2] != 1000) {

                    gyroscopeMax[0] = Math.max(sensorEvent.values[0], gyroscopeMax[0]);
                    gyroscopeMin[0] = Math.min(sensorEvent.values[0], gyroscopeMin[0]);

                    gyroscopeMax[1] = Math.max(sensorEvent.values[1], gyroscopeMax[1]);
                    gyroscopeMin[1] = Math.min(sensorEvent.values[1], gyroscopeMin[1]);

                    gyroscopeMax[2] = Math.max(sensorEvent.values[2], gyroscopeMax[2]);
                    gyroscopeMin[2] = Math.min(sensorEvent.values[2], gyroscopeMin[2]);

                }
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:

                if (magneticFieldMax[0] == 1000 && magneticFieldMin[0] == 1000) {
                    magneticFieldMax[0] = sensorEvent.values[0];
                    magneticFieldMin[0] = sensorEvent.values[0];
                }

                if (magneticFieldMax[1] == 1000 && magneticFieldMin[1] == 1000) {
                    magneticFieldMax[1] = sensorEvent.values[1];
                    magneticFieldMin[1] = sensorEvent.values[1];
                }

                if (magneticFieldMax[2] == 1000 && magneticFieldMin[2] == 1000) {
                    magneticFieldMax[2] = sensorEvent.values[2];
                    magneticFieldMin[2] = sensorEvent.values[2];
                }

                if (magneticFieldMax[0] != 1000 && magneticFieldMin[0] != 1000 &&
                        magneticFieldMax[1] != 1000 && magneticFieldMin[1] != 1000 &&
                        magneticFieldMax[2] != 1000 && magneticFieldMin[2] != 1000) {

                    magneticFieldMax[0] = Math.max(sensorEvent.values[0], magneticFieldMax[0]);
                    magneticFieldMin[0] = Math.min(sensorEvent.values[0], magneticFieldMin[0]);

                    magneticFieldMax[1] = Math.max(sensorEvent.values[1], magneticFieldMax[1]);
                    magneticFieldMin[1] = Math.min(sensorEvent.values[1], magneticFieldMin[1]);

                    magneticFieldMax[2] = Math.max(sensorEvent.values[2], magneticFieldMax[2]);
                    magneticFieldMin[2] = Math.min(sensorEvent.values[2], magneticFieldMin[2]);

                }
                break;
        }
    }

    /**
     * Calibrates the sensor value.
     */
    public static float calibrate(float f) {
        float result;
        boolean b = false;

        int i = (int) f;

        if (i > 0) {
            double d = f;
            f = (float) Math.round(d);
            i = (int) f;
            int y = String.valueOf(i).length() - 1;
            int l = (int) Math.pow(10, y);
            int x = Integer.valueOf(String.valueOf(String.valueOf(i).charAt(0)));

            result = x * l;
        } else {

            String str = String.valueOf(f);
            int in = -1;

            while (!b && in < str.length() - 1) {
                in = in + 1;
                if (str.charAt(in) != '0' && str.charAt(in) != '.') {
                    b = true;
                }
            }

            int x = in;

            if (str.length() > (in + 1))
                str = str.substring(0, x + 2);

            if (Integer.valueOf(String.valueOf(str.charAt(in))) >= 5) {
                int n = Integer.valueOf(String.valueOf(str.charAt(in))) + 1;
                String str2 = str.substring(0, x) + String.valueOf(n);
                result = Float.valueOf(str2);
            } else {
                str = str.substring(0, x + 1);
                result = Float.valueOf(str);
            }
        }

        return result;
    }

    /**
     * Calculates the calibrating delta change.
     *
     * @return the delta change of the Accelerometer.
     */
    public static float getDa() {
        float i = accelerometerMax[0] - accelerometerMin[0];
        float j = accelerometerMax[1] - accelerometerMin[1];
        float k = accelerometerMax[2] - accelerometerMin[2];
        float da = Math.max(i, j);
        da = Math.max(k, da);
        return da;
    }

    /**
     * Calculates the calibrating delta change.
     *
     * @return the delta change of the Gyroscope.
     */
    public static float getDg() {
        float i = gyroscopeMax[0] - gyroscopeMin[0];
        float j = gyroscopeMax[1] - gyroscopeMin[1];
        float k = gyroscopeMax[2] - gyroscopeMin[2];
        float dg = Math.max(i, j);
        dg = Math.max(k, dg);
        return dg;
    }

    /**
     * Calculates the calibrating delta change.
     *
     * @return the delta change of the Magnetic Field.
     */
    public static float getDm() {
        float i = magneticFieldMax[0] - magneticFieldMin[0];
        float j = magneticFieldMax[1] - magneticFieldMin[1];
        float k = magneticFieldMax[2] - magneticFieldMin[2];
        float dm = Math.max(i, j);
        dm = Math.max(k, dm);
        return dm;
    }

    /**
     * Sets and sends the calibrated data if the mode is enabled.
     *
     * @param sensorEvent the sensor object gotten from the Listener.
     */
    private void sendCalibratedData(SensorEvent sensorEvent) {

        float calibratedDa = SP.getFloat("calibratedDa", 1);
        float calibratedDg = SP.getFloat("calibratedDg", 1);
        float calibratedDm = SP.getFloat("calibratedDm", 1);

        float[] value;
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                value = new float[]{Math.round(sensorEvent.values[0] / calibratedDa) * calibratedDa,
                        Math.round(sensorEvent.values[1] / calibratedDa) * calibratedDa,
                        Math.round(sensorEvent.values[2] / calibratedDa) * calibratedDa};
                sendMessageToActivity(sensorEvent.sensor.getType(), value);
                break;
            case Sensor.TYPE_GYROSCOPE:
                value = new float[]{Math.round(sensorEvent.values[0] / calibratedDg) * calibratedDg,
                        Math.round(sensorEvent.values[1] / calibratedDg) * calibratedDg,
                        Math.round(sensorEvent.values[2] / calibratedDg) * calibratedDg};
                sendMessageToActivity(sensorEvent.sensor.getType(), value);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                value = new float[]{Math.round(sensorEvent.values[0] / calibratedDm) * calibratedDm,
                        Math.round(sensorEvent.values[1] / calibratedDm) * calibratedDm,
                        Math.round(sensorEvent.values[2] / calibratedDm) * calibratedDm};
                sendMessageToActivity(sensorEvent.sensor.getType(), value);
                break;
            default:
                sendMessageToActivity(sensorEvent.sensor.getType(), sensorEvent.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy() {
        Log.d("SensorService", "Service stopped");
        sensorManager.unregisterListener(this);
    }

    /**
     * Registers to the different kinds of sensors
     */
    public void listenToAllSensors() {
        listenToSensorIfChecked(Sensor.TYPE_ACCELEROMETER);
        listenToSensorIfChecked(Sensor.TYPE_GYROSCOPE);
        listenToSensorIfChecked(Sensor.TYPE_MAGNETIC_FIELD);
        listenToSensorIfChecked(Sensor.TYPE_LIGHT);
        listenToSensorIfChecked(Sensor.TYPE_PROXIMITY);
    }

    /**
     * Registers to a sensor if it's checked from the Settings screen
     *
     * @param sensor The type of sensor to register
     */
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

    /**
     * Sends the new sensor change to SensorFragment,
     *
     * @param type  The type of sensor that changed
     * @param value The sensor values
     */
    public void sendMessageToActivity(int type, float[] value) {
        Intent intent = new Intent(MyApplication.sensorChangedFromSensorService);
        intent.putExtra("type", type);
        intent.putExtra("value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
