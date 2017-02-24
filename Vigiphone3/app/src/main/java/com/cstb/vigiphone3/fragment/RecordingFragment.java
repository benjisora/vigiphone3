package com.cstb.vigiphone3.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.database.Utils;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.service.ServiceManager;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordingFragment extends Fragment {

    //region databinding

    @BindView(R.id.step_text)
    TextView stepText;

    @BindView(R.id.time_text)
    TextView timeText;

    @BindView(R.id.threshold_text)
    TextView thresholdText;

    @BindView(R.id.database_text)
    TextView databaseText;

    @BindView(R.id.time_radiogroup)
    RadioGroup timeRadioGroup;

    @BindView(R.id.save_radiogroup)
    RadioGroup saveRadioGroup;

    @BindView(R.id.time_label)
    TextView timeLabel;

    @BindView(R.id.time_button)
    Button timeButton;

    @BindView(R.id.name_button)
    Button nameButton;

    @BindView(R.id.name_label)
    TextView nameLabel;

    @BindView(R.id.name_text)
    TextView nameText;

    @BindView(R.id.cardview_step)
    CardView cardviewStep;

    @BindView(R.id.cardview_time)
    CardView cardviewTime;

    @BindView(R.id.cardview_threshold)
    CardView cardviewThreshold;

    @BindView(R.id.cardview_save)
    CardView cardviewSave;

    @BindView(R.id.cardview_sumup)
    CardView cardviewSumup;

    @BindView(R.id.recordsince_text_sumup)
    TextView recordSinceSumupText;

    @BindView(R.id.time_text_sumup)
    TextView timeSumupText;

    @BindView(R.id.step_text_sumup)
    TextView stepSumupText;

    @BindView(R.id.threshold_text_sumup)
    TextView thresholdSumupText;

    @BindView(R.id.database_text_sumup)
    TextView databaseSumupText;

    @BindView(R.id.record_button)
    Button recordButton;

    //endregion

    private SharedPreferences SP;
    private String filename;
    private int threshold;

    private BroadcastReceiver mUpdateTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long time = (long) intent.getExtras().get("time");
            recordSinceSumupText.setText(formatMillisecondsToString(time));
            long size = Utils.getinstance().getRecordingRowsCount();
            databaseText.setText(String.valueOf(size));
            databaseSumupText.setText(String.valueOf(size));
        }
    };

    public RecordingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);
        ButterKnife.bind(this, view);
        SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        initalizeViews();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateTimeReceiver, new IntentFilter("updateTime"));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateTimeReceiver);
    }

    private void initalizeViews() {

        boolean isAlreadyRecording = SP.getBoolean("isRecording", false);

        if (isAlreadyRecording) {
            hideRootComponents();
        }

        boolean recordingInfinitely = SP.getBoolean("recordingInfinitely", true);
        boolean saveOnServer = SP.getBoolean("saveOnServer", true);

        if(recordingInfinitely){
            timeLabel.setVisibility(View.GONE);
            timeText.setVisibility(View.GONE);
            timeButton.setVisibility(View.GONE);
            timeSumupText.setText(R.string.button_click);
        } else {
            timeLabel.setVisibility(View.VISIBLE);
            timeText.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
            int value = SP.getInt("recordTime", 10000);
            timeSumupText.setText(formatMillisecondsToString(value));
        }

        if(saveOnServer){
            nameButton.setVisibility(View.GONE);
            nameLabel.setVisibility(View.GONE);
            nameText.setVisibility(View.GONE);
        } else {
            nameButton.setVisibility(View.VISIBLE);
            nameLabel.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);
        }

        timeRadioGroup.check(recordingInfinitely ? R.id.time_infinite : R.id.time_limited);
        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time_infinite:
                        SP.edit().putBoolean("recordingInfinitely", true).apply();
                        timeLabel.setVisibility(View.GONE);
                        timeText.setVisibility(View.GONE);
                        timeButton.setVisibility(View.GONE);
                        break;
                    case R.id.time_limited:
                        SP.edit().putBoolean("recordingInfinitely", false).apply();
                        timeLabel.setVisibility(View.VISIBLE);
                        timeText.setVisibility(View.VISIBLE);
                        timeButton.setVisibility(View.VISIBLE);
                        if(!isvalidTime(SP.getInt("stepTime", 1000), SP.getInt("recordTime", 10000))) {
                            SP.edit().putInt("stepTime", 1000).putInt("recordTime", 10000).apply();

                            int value = SP.getInt("stepTime", 1000);
                            stepText.setText(formatMillisecondsToString(value));
                            stepSumupText.setText(formatMillisecondsToString(value));

                            value = SP.getInt("recordTime", 10000);
                            timeText.setText(formatMillisecondsToString(value));

                            Toast.makeText(getActivity(), R.string.revert_default_times, Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });

        saveRadioGroup.check(saveOnServer ? R.id.save_server : R.id.save_file);
        saveRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.save_server:
                        SP.edit().putBoolean("saveOnServer", true).apply();
                        nameButton.setVisibility(View.GONE);
                        nameLabel.setVisibility(View.GONE);
                        nameText.setVisibility(View.GONE);
                        break;
                    case R.id.save_file:
                        SP.edit().putBoolean("saveOnServer", false).apply();
                        nameButton.setVisibility(View.VISIBLE);
                        nameLabel.setVisibility(View.VISIBLE);
                        nameText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        int value = SP.getInt("stepTime", 1000);
        stepText.setText(formatMillisecondsToString(value));
        stepSumupText.setText(formatMillisecondsToString(value));

        value = SP.getInt("recordTime", 10000);
        timeText.setText(formatMillisecondsToString(value));


        value = SP.getInt("threshold", 100);
        thresholdText.setText(String.valueOf(value));
        thresholdSumupText.setText(String.valueOf(value));

        long size = Utils.getinstance().getRecordingRowsCount();
        databaseText.setText(String.valueOf(size));
        databaseSumupText.setText(String.valueOf(size));

        String name = SP.getString("fileName", "default");
        nameText.setText(name);
    }

    private boolean isvalidTime(int step, int record){
        if(SP.getBoolean("recordingInfinitely", true)){
            return true;
        }else if(step <= record){
            return true;
        }
        return false;
    }

    private void hideRootComponents() {
        cardviewStep.setVisibility(View.GONE);
        cardviewTime.setVisibility(View.GONE);
        cardviewThreshold.setVisibility(View.GONE);
        cardviewSave.setVisibility(View.GONE);
        cardviewSumup.setVisibility(View.VISIBLE);
    }

    private void showRootComponents() {
        cardviewStep.setVisibility(View.VISIBLE);
        cardviewTime.setVisibility(View.VISIBLE);
        cardviewThreshold.setVisibility(View.VISIBLE);
        cardviewSave.setVisibility(View.VISIBLE);
        cardviewSumup.setVisibility(View.GONE);
    }

    @OnClick(R.id.step_button)
    public void stepButton() {
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                setStep(hourOfDay, minute, seconds);
            }
        }, getHoursFromMilliseconds(SP.getInt("stepTime", 0)),
                getMinutesFromMilliseconds(SP.getInt("stepTime", 0)),
                getSecondsFromMilliseconds(SP.getInt("stepTime", 1)),
                true);
        mTimePicker.show();
    }

    @OnClick(R.id.time_button)
    public void timeButton() {
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                setRecordTime(hourOfDay, minute, seconds);
            }
        }, getHoursFromMilliseconds(SP.getInt("recordDuration", 0)),
                getMinutesFromMilliseconds(SP.getInt("recordDuration", 0)),
                getSecondsFromMilliseconds(SP.getInt("recordDuration", 1)),
                true);
        mTimePicker.show();
    }

    @OnClick(R.id.threshold_button)
    public void thresholdButton() {
        new MaterialDialog.Builder(getActivity())
                .title("Choose a transfer threshold")
                .inputRange(1, 5)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SP.edit().putInt("threshold", threshold).apply();
                        thresholdText.setText(String.valueOf(threshold));
                        thresholdSumupText.setText(String.valueOf(threshold));
                    }
                })
                .input(String.valueOf(SP.getInt("threshold", 100)), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!isFilenameValid(input.toString())){
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else{
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            threshold = Integer.parseInt(input.toString());
                        }
                    }
                }).show();
    }

    private String formatMillisecondsToString(int milliseconds) {
        return String.format(Locale.ENGLISH, "%02d", getHoursFromMilliseconds(milliseconds)) + "h"
                + String.format(Locale.ENGLISH, "%02d", getMinutesFromMilliseconds(milliseconds)) + "m"
                + String.format(Locale.ENGLISH, "%02d", getSecondsFromMilliseconds(milliseconds)) + "s";
    }

    private String formatMillisecondsToString(long milliseconds) {
        return String.format(Locale.ENGLISH, "%02d", getHoursFromMilliseconds(milliseconds)) + "h"
                + String.format(Locale.ENGLISH, "%02d", getMinutesFromMilliseconds(milliseconds)) + "m"
                + String.format(Locale.ENGLISH, "%02d", getSecondsFromMilliseconds(milliseconds)) + "s";
    }

    private int getHoursFromMilliseconds(int milliseconds) {
        return ((milliseconds / (1000 * 60 * 60)) % 24);
    }

    private long getHoursFromMilliseconds(long milliseconds) {
        return ((milliseconds / (1000 * 60 * 60)) % 24);
    }

    private int getMinutesFromMilliseconds(int milliseconds) {
        return ((milliseconds / (1000 * 60)) % 60);
    }

    private long getMinutesFromMilliseconds(long milliseconds) {
        return ((milliseconds / (1000 * 60)) % 60);
    }

    private int getSecondsFromMilliseconds(int milliseconds) {
        return (milliseconds / 1000) % 60;
    }

    private long getSecondsFromMilliseconds(long milliseconds) {
        return (milliseconds / 1000) % 60;
    }

    private void setStep(int hourOfDay, int minute, int seconds) {
        if (hourOfDay + minute + seconds != 0) {
            int timeMilliseconds = seconds * 1000 + minute * (1000 * 60) + hourOfDay * (1000 * 60 * 60);

            if(isvalidTime(timeMilliseconds, SP.getInt("recordTime", 10000))) {
                SP.edit().putInt("stepTime", timeMilliseconds).apply();
                String text = String.format(Locale.ENGLISH, "%02d", hourOfDay) + "h"
                        + String.format(Locale.ENGLISH, "%02d", minute) + "m"
                        + String.format(Locale.ENGLISH, "%02d", seconds) + "s";
                stepText.setText(text);
                stepSumupText.setText(text);
            } else {
                Toast.makeText(getActivity(), R.string.duration_smaller_step, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.time_null, Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecordTime(int hourOfDay, int minute, int seconds) {
        if (hourOfDay + minute + seconds != 0) {
            int timeMilliseconds = seconds * 1000 + minute * (1000 * 60) + hourOfDay * (1000 * 60 * 60);

            if(isvalidTime(SP.getInt("stepTime", 1000), timeMilliseconds)){
                SP.edit().putInt("recordTime", timeMilliseconds).apply();
                String text = String.format(Locale.ENGLISH, "%02d", hourOfDay) + "h"
                        + String.format(Locale.ENGLISH, "%02d", minute) + "m"
                        + String.format(Locale.ENGLISH, "%02d", seconds) + "s";
                timeText.setText(text);
                timeSumupText.setText(text);
            } else {
                Toast.makeText(getActivity(), R.string.duration_smaller_step, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), R.string.time_null, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.name_button)
    public void nameButton() {
        new MaterialDialog.Builder(getActivity())
                .title("Choose a file name")
                .inputRange(1, 100)
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SP.edit().putString("fileName", filename).apply();
                        nameText.setText(filename);
                    }
                })
                .input(SP.getString("fileName", "default"), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!isFilenameValid(input.toString())){
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else{
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            filename = input.toString();
                        }
                    }
                }).show();
    }

    public boolean isFilenameValid(String file) {
        String regex = "^[\\w\\-\\. ]+$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(file).find();
    }

    @OnClick(R.id.record_button)
    public void recordButton() {

        boolean isAlreadyRecording = SP.getBoolean("isRecording", false);

        if (!isAlreadyRecording) {
            hideRootComponents();
            recordButton.setText(R.string.stop_recording);
            SP.edit().putBoolean("isRecording", true).apply();

            Intent intent = new Intent("startRecording");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        } else {
            //TODO: stop recording
            SP.edit().putBoolean("isRecording", false).apply();
            showRootComponents();
            recordButton.setText(R.string.start_recording);

            Intent intent = new Intent("stopRecording");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            long size = Utils.getinstance().getRecordingRowsCount();
            databaseText.setText(String.valueOf(size));
            databaseSumupText.setText(String.valueOf(size));

            List<RecordingRow> list = Utils.getinstance().getAllRecordingRows();

            for(int i = 0; i < list.size(); i++){
                Log.d("Liste", "ligne " + i + ": " + list.get(i).getDate());
            }

        }
    }


}
