package com.cstb.vigiphone3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.cstb.vigiphone3.data.model.RecordingRow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ServiceManager {

    private int cid, lac, mcc, mnc, strength;
    private String networkName, networkType, neighbours, deviceId;
    private Location location;
    private float[] accelerometer, gyroscope, magneticField;
    private float light, proximity;
    private Context context;

    private BroadcastReceiver mSensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = (int) intent.getExtras().get("type");
            float[] value = (float[]) intent.getExtras().get("value");
            switch (type) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometer = value;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroscope = value;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticField = value;
                    break;
                case Sensor.TYPE_LIGHT:
                    if (value != null) {
                        light = value[0];
                    }
                    break;
                case Sensor.TYPE_PROXIMITY:
                    if (value != null) {
                        proximity = value[0];
                    }
                    break;
            }
            sendUpdateMessage();
        }
    };

    private BroadcastReceiver mSaveRecordingRow = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            saveRecordingRow((long) intent.getExtras().get("time"));
        }
    };

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            location = (Location) intent.getExtras().get("value");
            sendUpdateMessage();
        }
    };

    private BroadcastReceiver mSignalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceId = (String) intent.getExtras().get("deviceId");
            cid = (int) intent.getExtras().get("cid");
            lac = (int) intent.getExtras().get("lac");
            mcc = (int) intent.getExtras().get("mcc");
            mnc = (int) intent.getExtras().get("mnc");
            networkName = (String) intent.getExtras().get("name");
            networkType = (String) intent.getExtras().get("type");
            neighbours = (String) intent.getExtras().get("neighbours");
            strength = (int) intent.getExtras().get("strength");
            sendUpdateMessage();
        }
    };

    public ServiceManager(Context activityContext) {
        context = activityContext;
        cid = lac = mcc = mnc = strength = 0;
        networkName = networkType = neighbours = deviceId = "";
        location = null;
        accelerometer = gyroscope = magneticField = new float[]{0, 0, 0};
        light = proximity = 0;

    }

    private void initializeRecordingRow(RecordingRow recordingRow) {

        recordingRow.setCID(cid);
        recordingRow.setLAC(lac);
        recordingRow.setMCC(mcc);
        recordingRow.setMNC(mnc);
        recordingRow.setName(networkName);
        recordingRow.setType(networkType);
        recordingRow.setStrength(strength);
        recordingRow.setNeighbours(neighbours);
        if (location != null) {
            recordingRow.setLatitude(location.getLatitude());
            recordingRow.setLongitude(location.getLongitude());
        }
        recordingRow.setAccelerometerX(accelerometer[0]);
        recordingRow.setAccelerometerY(accelerometer[1]);
        recordingRow.setAccelerometerZ(accelerometer[2]);
        recordingRow.setGyroscopeX(gyroscope[0]);
        recordingRow.setGyroscopeY(gyroscope[1]);
        recordingRow.setGyroscopeZ(gyroscope[2]);
        recordingRow.setMagneticFieldX(magneticField[0]);
        recordingRow.setMagneticFieldY(magneticField[1]);
        recordingRow.setMagneticFieldZ(magneticField[2]);
        recordingRow.setLight(light);
        recordingRow.setProximity(proximity);

    }

    private void saveRecordingRow(Long time) {

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date(time);

        RecordingRow rec = new RecordingRow();
        initializeRecordingRow(rec);

        rec.setImei(deviceId);
        rec.setDate(dateformat.format(date));
        rec.setModel(Build.MODEL);
        rec.save();
    }

    public void startServices() {
        context.startService(new Intent(context, SensorService.class));
        context.startService(new Intent(context, SignalService.class));
        context.startService(new Intent(context, LocationService.class));
    }

    public void stopServices() {
        context.stopService(new Intent(context, SensorService.class));
        context.stopService(new Intent(context, SignalService.class));
        context.stopService(new Intent(context, LocationService.class));
    }

    public void registerReceivers() {
        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("LocationChanged"));
        LocalBroadcastManager.getInstance(context).registerReceiver(mSensorReceiver, new IntentFilter("SensorChanged"));
        LocalBroadcastManager.getInstance(context).registerReceiver(mSignalReceiver, new IntentFilter("SignalChanged"));
        LocalBroadcastManager.getInstance(context).registerReceiver(mSaveRecordingRow, new IntentFilter("saveRecordingRow"));

    }

    public void unregisterReceivers() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocationReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSensorReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSignalReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSaveRecordingRow);
    }

    private void sendUpdateMessage() {
        RecordingRow rec = new RecordingRow();
        initializeRecordingRow(rec);

        Intent intent = new Intent("UpdateView");
        intent.putExtra("row", rec);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
