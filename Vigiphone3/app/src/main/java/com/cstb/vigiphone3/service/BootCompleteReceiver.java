package com.cstb.vigiphone3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cstb.vigiphone3.data.database.MyApplication;

/**
 * BootCompleteReceiver class, used to check if the phone went off while recording
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     * Sets the recording to false if it was recording before shutdown
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MyApplication.isRecording()) {
            MyApplication.setRecording(false);
        }
    }
}
