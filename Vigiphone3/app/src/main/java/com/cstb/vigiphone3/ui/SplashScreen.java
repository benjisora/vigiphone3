package com.cstb.vigiphone3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/** Loads the MainActivity after the blank loading time has passed. */
public class SplashScreen extends AppCompatActivity {

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));
    }

    /** Starts the activity and deletes itself from the activity stack. */
    public void startIntentAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
