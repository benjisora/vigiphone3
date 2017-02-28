package com.cstb.vigiphone3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        if (SP.getBoolean("isRecording", false)) {
            SP.edit().putBoolean("isRecording", false).apply();
        }
    }
}
