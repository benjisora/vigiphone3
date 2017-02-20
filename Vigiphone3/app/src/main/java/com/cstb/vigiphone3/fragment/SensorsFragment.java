package com.cstb.vigiphone3.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.service.SensorService;

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

    public SensorsFragment() {
    }

    private BroadcastReceiver mSensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = (int) intent.getExtras().get("type");
            float[] value = (float[]) intent.getExtras().get("value");
            updateAccordingView(type, value);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        ButterKnife.bind(this, view);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSensorReceiver, new IntentFilter("SensorChanged"));
        getActivity().startService(new Intent(getActivity(), SensorService.class));

        return view;
    }


    public void updateAccordingView(int type, float[] value) {
        String text;
        switch (type) {

            case Sensor.TYPE_ACCELEROMETER:
                text = String.valueOf(value[0]) + " / "
                        + String.valueOf(value[1]) + " / "
                        + String.valueOf(value[2]);
                accelerometerText.setText(text);
                break;

            case Sensor.TYPE_GYROSCOPE:
                text = String.valueOf(value[0]) + " / "
                        + String.valueOf(value[1]) + " / "
                        + String.valueOf(value[2]);
                gyroscopeText.setText(text);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                text = String.valueOf(value[0]) + " / "
                        + String.valueOf(value[1]) + " / "
                        + String.valueOf(value[2]);
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

    @OnClick(R.id.cidlac_label)
    public void changeDisplay() {

    }

}
