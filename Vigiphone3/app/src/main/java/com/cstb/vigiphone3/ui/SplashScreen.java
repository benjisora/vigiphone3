package com.cstb.vigiphone3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));
    }

    public void startIntentAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
