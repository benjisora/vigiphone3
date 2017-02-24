package com.cstb.vigiphone3.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.service.SensorService;
import com.cstb.vigiphone3.service.ServiceManager;
import com.cstb.vigiphone3.ui.MainActivity;

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

    @BindView(R.id.orientation_text)
    TextView orientationText;

    //endregion

    private int cidTapCount = 0;
    private RecordingRow recordingRow;
    private SharedPreferences SP;

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recordingRow = (RecordingRow) intent.getExtras().get("row");
            updateAccordingView();
        }
    };

    public SensorsFragment() {
    }

    @OnClick(R.id.calibrate_button)
    public void calibrateButton(){
        if(!SP.getBoolean("modeCalibrate",false)){

            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.please_wait_title)
                    .content(R.string.calibration_ongoing)
                    .progress(true, 0)
                    .show();

            final Handler showDialogAndCalibrate = new Handler();
            final Handler dismissDialogAndStopCalibration = new Handler();

            showDialogAndCalibrate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SP.edit().putBoolean("modeCalibrate", true).apply();

                    dismissDialogAndStopCalibration.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SP.edit().putBoolean("hasCalibrated", true).apply();

                            SP.edit().putFloat("calibratedDa", SensorService.calibrate(SensorService.getDa())).apply();
                            SP.edit().putFloat("calibratedDg", SensorService.calibrate(SensorService.getDg())).apply();
                            SP.edit().putFloat("calibratedDm", SensorService.calibrate(SensorService.getDm())).apply();

                            dialog.dismiss();
                        }
                    }, 10000); //ms
                }
            }, 2000); //ms

        }else{
            Toast.makeText(getActivity(),"Already calibrated", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.uncalibrate_button)
    public void uncalibrateButton(){
        if(SP.getBoolean("modeCalibrate",false)){
            SP.edit().putBoolean("modeCalibrate", false).apply();
            SP.edit().putBoolean("hasCalibrated", false).apply();
        }else{
            Toast.makeText(getActivity(),"No calibration done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        ButterKnife.bind(this, view);
        SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateReceiver, new IntentFilter("UpdateView"));
        return view;
    }

    public void updateAccordingView() {
        changeCidDisplay();
        String text;
        text = String.valueOf(recordingRow.getMCC()) + " / " + String.valueOf(recordingRow.getMNC());
        mccMncText.setText(text);
        opNameText.setText(recordingRow.getName());
        typeText.setText(recordingRow.getType());
        neighboursText.setText(recordingRow.getNeighbours());
        text = String.valueOf(recordingRow.getStrength());
        strenghText.setText(text);

        text = String.format(Locale.ENGLISH, "%.6f", recordingRow.getLatitude());
        latitudeText.setText(text);
        text = String.format(Locale.ENGLISH, "%.6f", recordingRow.getLongitude());
        longitudeText.setText(text);

        text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", recordingRow.getAccelerometerX(), recordingRow.getAccelerometerY(), recordingRow.getAccelerometerZ());
        accelerometerText.setText(text);
        text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", recordingRow.getGyroscopeX(), recordingRow.getGyroscopeY(), recordingRow.getGyroscopeZ());
        gyroscopeText.setText(text);
        text = String.format(Locale.ENGLISH, "%.3f / %.3f / %.3f", recordingRow.getMagneticFieldX(), recordingRow.getMagneticFieldY(), recordingRow.getMagneticFieldZ());
        magneticFieldText.setText(text);
        text = String.valueOf(recordingRow.getLight());
        lightText.setText(text);
        text = String.valueOf(recordingRow.getProximity());
        proximityText.setText(text);

        float[] orientation = getOrientationFromSensors();
        if(orientation!=null){
            text = String.valueOf((int) Math.toDegrees(orientation[0])) + " / "
                    + String.valueOf((int) Math.toDegrees(orientation[1])) + " / "
                    + String.valueOf((int) Math.toDegrees(orientation[2]));
            orientationText.setText(text);
        }
    }

    private float[] getOrientationFromSensors(){
        float[] accelerometer = {recordingRow.getAccelerometerX(), recordingRow.getAccelerometerY(), recordingRow.getAccelerometerZ()};
        float[] gyroscope = {recordingRow.getGyroscopeX(), recordingRow.getGyroscopeY(), recordingRow.getGyroscopeZ()};
        float[] R = new float[9];
        if(accelerometer!= null && gyroscope != null){
            float orientation[] = new float[3];
            if(SensorManager.getRotationMatrix(R, new float[3], accelerometer, gyroscope)){
                SensorManager.getOrientation(R, orientation);
                return orientation;
            }
        }
        return null;
    }

    @OnClick({R.id.cidlac_label, R.id.cidlac_text})
    public void changeDisplay() {
        cidTapCount++;
        changeCidDisplay();
    }

    private void changeCidDisplay() {
        switch (cidTapCount % 3) {
            case 0:
                cidLacText.setText(String.valueOf(recordingRow.getCID()) + "/" + String.valueOf(recordingRow.getLAC()));
                break;
            case 1:
                cidLacText.setText(String.valueOf(recordingRow.getCID() / 65536) + "x2\u00B9\u2076+" + String.valueOf(recordingRow.getCID() % 65535) + "/" + String.valueOf(recordingRow.getLAC()));
                break;
            case 2:
                cidLacText.setText("0x" + String.valueOf(Integer.toString(recordingRow.getCID(), 16)) + "/" + String.valueOf(recordingRow.getLAC()));
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("SensorFragment", "updateReceiver paused");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SensorFragment", "updateReceiver registered again");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateReceiver, new IntentFilter("UpdateView"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SensorFragment", "updateReceiver unregistered");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateReceiver);

    }
}
