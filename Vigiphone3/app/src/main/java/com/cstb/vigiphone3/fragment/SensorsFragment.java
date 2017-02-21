package com.cstb.vigiphone3.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cstb.vigiphone3.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SensorsFragment extends Fragment {

    //region view bindings

    @BindView(R.id.cidlac_text)
    TextView cidLacText;

    @BindView(R.id.mccmnc_text)
    TextView mccMncText;

    @BindView(R.id.op_name_text)
    TextView opNameText;

    @BindView(R.id.type_text)
    TextView typeText;

    @BindView(R.id.strengh_text)
    TextView strenghText;

    @BindView(R.id.neighbours_text)
    TextView neighboursText;

    @BindView(R.id.latitude_text)
    TextView latitudeText;

    @BindView(R.id.longitude_text)
    TextView longitudeText;

    @BindView(R.id.accelerometer_text)
    TextView accelerometerText;

    @BindView(R.id.gyroscope_text)
    TextView gyroscopeText;

    @BindView(R.id.magneticField_text)
    TextView magneticFieldText;

    @BindView(R.id.light_text)
    TextView lightText;

    @BindView(R.id.proximity_text)
    TextView proximityText;

    //endregion

    private int cidTapCount = 0;
    private int Cid = 0;
    private int Lac = 0;

    private BroadcastReceiver mSensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = (int) intent.getExtras().get("type");
            float[] value = (float[]) intent.getExtras().get("value");
            updateAccordingView(type, value);
        }
    };
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location value = (Location) intent.getExtras().get("value");
            updateAccordingView(value);
        }
    };
    private BroadcastReceiver mSignalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int Cid = (int) intent.getExtras().get("cid");
            int Lac = (int) intent.getExtras().get("lac");
            int Mcc = (int) intent.getExtras().get("mcc");
            int Mnc = (int) intent.getExtras().get("mnc");
            String networkName = (String) intent.getExtras().get("name");
            String networkType = (String) intent.getExtras().get("type");
            String neighbours = (String) intent.getExtras().get("neighbours");
            int Strength = (int) intent.getExtras().get("strength");
            updateAccordingView(Cid, Lac, Mcc, Mnc, networkName, networkType, neighbours, Strength);
        }
    };

    public SensorsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        ButterKnife.bind(this, view);

        registerReceivers();

        return view;
    }


    public void updateAccordingView(int type, float[] value) {
        String text;
        switch (type) {

            case Sensor.TYPE_ACCELEROMETER:
                text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", value[0], value[1], value[2]);
                accelerometerText.setText(text);
                break;

            case Sensor.TYPE_GYROSCOPE:
                text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", value[0], value[1], value[2]);
                gyroscopeText.setText(text);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", value[0], value[1], value[2]);
                magneticFieldText.setText(text);
                break;

            case Sensor.TYPE_LIGHT:
                text = String.valueOf(value[0]);
                lightText.setText(text);
                break;

            case Sensor.TYPE_PROXIMITY:
                text = String.valueOf(value[0]);
                proximityText.setText(text);
                break;
        }
    }

    public void updateAccordingView(Location value) {
        String text = String.format(Locale.ENGLISH, "%.6f", value.getLatitude());
        latitudeText.setText(text);
        text = String.format(Locale.ENGLISH, "%.6f", value.getLongitude());
        longitudeText.setText(text);
    }

    public void updateAccordingView(int CID, int LAC, int MCC, int MNC, String networkName, String networkType, String neighbours, int Strength) {
        Cid = CID;
        Lac = LAC;
        changeCidDisplay();
        String text;
        text = String.valueOf(MCC) + " / " + String.valueOf(MNC);
        mccMncText.setText(text);
        opNameText.setText(networkName);
        typeText.setText(networkType);
        neighboursText.setText(neighbours);
        text = String.valueOf(Strength);
        strenghText.setText(text);
    }

    @OnClick(R.id.cidlac_label)
    public void changeDisplay() {
        cidTapCount++;
        changeCidDisplay();
    }

    private void changeCidDisplay() {
        switch (cidTapCount % 3) {
            case 0:
                cidLacText.setText(String.valueOf(Cid) + "/" + String.valueOf(Lac));
                break;
            case 1:
                cidLacText.setText(String.valueOf(Cid / 65536) + "x2\u00B9\u2076+" + String.valueOf(Cid % 65535) + "/" + String.valueOf(Lac));
                break;
            case 2:
                cidLacText.setText("0x" + String.valueOf(Integer.toString(Cid, 16)) + "/" + String.valueOf(Lac));
                break;
        }
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocationReceiver, new IntentFilter("LocationChanged"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSensorReceiver, new IntentFilter("SensorChanged"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSignalReceiver, new IntentFilter("SignalChanged"));
    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocationReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSensorReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSignalReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }


}
