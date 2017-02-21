package com.cstb.vigiphone3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.cstb.vigiphone3.ui.MainActivity;

import java.util.Locale;

/**
 * Created by saugues on 21/02/17.
 */

public class ServiceManager {

    private int cid, lac, mcc, mnc, strength;
    private String networkName, networkType, neighbours;
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
        }
    };

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            location = (Location) intent.getExtras().get("value");
        }
    };

    private BroadcastReceiver mSignalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cid = (int) intent.getExtras().get("cid");
            lac = (int) intent.getExtras().get("lac");
            mcc = (int) intent.getExtras().get("mcc");
            mnc = (int) intent.getExtras().get("mnc");
            networkName = (String) intent.getExtras().get("name");
            networkType = (String) intent.getExtras().get("type");
            neighbours = (String) intent.getExtras().get("neighbours");
            strength = (int) intent.getExtras().get("strength");
        }
    };

    public ServiceManager(Context activityContext){
        context = activityContext;
        cid = lac = mcc = mnc = strength = 0;
        networkName = networkType = neighbours = "";
        location = null;
        accelerometer = gyroscope = magneticField = new float[]{0,0,0};
        light = proximity = 0;
    }


    public void startServices(){
        context.startService(new Intent(context, SensorService.class));
        context.startService(new Intent(context, SignalService.class));
        context.startService(new Intent(context, LocationService.class));
    }

    public void stopServices(){
        context.stopService(new Intent(context, SensorService.class));
        context.stopService(new Intent(context, SignalService.class));
        context.stopService(new Intent(context, LocationService.class));
    }

    public void registerReceivers() {
        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("LocationChanged"));
        LocalBroadcastManager.getInstance(context).registerReceiver(mSensorReceiver, new IntentFilter("SensorChanged"));
        LocalBroadcastManager.getInstance(context).registerReceiver(mSignalReceiver, new IntentFilter("SignalChanged"));
    }

    public void unregisterReceivers() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocationReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSensorReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSignalReceiver);
    }
}
