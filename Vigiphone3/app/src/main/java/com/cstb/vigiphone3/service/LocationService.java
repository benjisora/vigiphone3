package com.cstb.vigiphone3.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cstb.vigiphone3.data.database.MyApplication;

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private Location previousLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("LocationService", "Service started");
    }

    /**
     * {@inheritDoc}
     * Initializes the location if permission is given
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            previousLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        if (previousLocation != null) {
            sendMessageToActivity(previousLocation);
        }
        return Service.START_STICKY;
    }

    /**
     * {@inheritDoc}
     * Sends the location to the ServiceManager if it's better than the last one
     */
    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, previousLocation)) {
            previousLocation = location;
            sendMessageToActivity(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onDestroy() {
        Log.d("LocationService", "Service stopped");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Checks whether or not the new location is better than the previous one
     *
     * @param location            The new location
     * @param currentBestLocation The last best location registered
     * @return True if it is, false otherwise
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 120000; //two minutes = 1000 * 60 * 2
        boolean isSignificantlyOlder = timeDelta < -120000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than 2 minutes since the current location, use the new one
        if (isSignificantlyNewer) {
            return true;
            //If the new location is more than 2 minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if both locations are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality by combining timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the two locations come from the same provider
     *
     * @param provider1 The first location
     * @param provider2 The second location
     * @return True if they do, false otherwise
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Sends the location to the ServiceManager
     * @param location The location to send
     */
    public void sendMessageToActivity(Location location) {
        Intent intent = new Intent(MyApplication.locationChangedFromLocationService);
        intent.putExtra("value", location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
