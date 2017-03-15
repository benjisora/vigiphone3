package com.cstb.vigiphone3.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.text.InputType;
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
import com.cstb.vigiphone3.data.database.MyApplication;
import com.cstb.vigiphone3.data.database.Utils;
import com.cstb.vigiphone3.service.RecordService;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RecordingFragment, used to record the emitter values and the phone sensors
 */
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

    /**
     * Updates the recording time whenever the order is received
     */
    private BroadcastReceiver mUpdateTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long time = (long) intent.getExtras().get("value");
            recordSinceSumupText.setText(formatMillisecondsToString(time));
            long size = Utils.getinstance().getRecordingRowsCount();
            databaseText.setText(String.valueOf(size));
            databaseSumupText.setText(String.valueOf(size));
        }
    };

    /**
     * Updates the database count whenever this order is received
     */
    private BroadcastReceiver mDatabaseUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long size = Utils.getinstance().getRecordingRowsCount();
            databaseText.setText(String.valueOf(size));
            databaseSumupText.setText(String.valueOf(size));
        }
    };

    /**
     * Goes back to the unrecorded mode whenever the order is received
     */
    private BroadcastReceiver mEndRecordingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showRootComponents();
            recordButton.setText(R.string.start_recording);

            long size = Utils.getinstance().getRecordingRowsCount();
            databaseText.setText(String.valueOf(size));
            databaseSumupText.setText(String.valueOf(size));

        }
    };

    public RecordingFragment() {
    }

    /**
     * {@inheritDoc}
     * Initializes the view to display, and registers the listeners
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);
        ButterKnife.bind(this, view);
        SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        initalizeViews();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateTimeReceiver, new IntentFilter(MyApplication.updateTimeFromRecordService));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDatabaseUpdate, new IntentFilter(MyApplication.updateDatabaseFromRecordService));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mEndRecordingReceiver, new IntentFilter(MyApplication.endRecordingFromRecordService));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateTimeReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDatabaseUpdate);
    }

    /**
     * Shows the cardviews to display, and sets the default values to each variables
     */
    private void initalizeViews() {

        if (MyApplication.isRecording()) {
            hideRootComponents();
            recordButton.setText(getString(R.string.stop_recording));
        } else {
            recordButton.setText(getString(R.string.start_recording));
        }

        if (MyApplication.isRecordingInfinitely()) {
            timeLabel.setVisibility(View.GONE);
            timeText.setVisibility(View.GONE);
            timeButton.setVisibility(View.GONE);
            timeSumupText.setText(R.string.button_click);
        } else {
            timeLabel.setVisibility(View.VISIBLE);
            timeText.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
            timeText.setText(formatMillisecondsToString(MyApplication.getRecordTime()));
        }

        if (MyApplication.isSavingOnServer()) {
            nameButton.setVisibility(View.GONE);
            nameLabel.setVisibility(View.GONE);
            nameText.setVisibility(View.GONE);
        } else {
            nameButton.setVisibility(View.VISIBLE);
            nameLabel.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);
        }

        timeRadioGroup.check(MyApplication.isRecordingInfinitely() ? R.id.time_infinite : R.id.time_limited);
        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time_infinite:
                        MyApplication.setRecordingInfinitely(true);
                        timeLabel.setVisibility(View.GONE);
                        timeText.setVisibility(View.GONE);
                        timeButton.setVisibility(View.GONE);

                        timeSumupText.setText(R.string.button_click);

                        break;
                    case R.id.time_limited:
                        MyApplication.setRecordingInfinitely(false);
                        timeLabel.setVisibility(View.VISIBLE);
                        timeText.setVisibility(View.VISIBLE);
                        timeButton.setVisibility(View.VISIBLE);

                        timeSumupText.setText(formatMillisecondsToString(MyApplication.getRecordTime()));

                        if (!isValidTime(MyApplication.getStep(), MyApplication.getRecordTime())) {

                            MyApplication.setStep(1000);
                            MyApplication.setRecordTime(10000);

                            stepText.setText(formatMillisecondsToString(MyApplication.getStep()));
                            stepSumupText.setText(formatMillisecondsToString(MyApplication.getStep()));

                            timeText.setText(formatMillisecondsToString(MyApplication.getRecordTime()));
                            timeSumupText.setText(formatMillisecondsToString(MyApplication.getRecordTime()));

                            Toast.makeText(getActivity(), R.string.revert_default_times, Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });

        saveRadioGroup.check(MyApplication.isSavingOnServer() ? R.id.save_server : R.id.save_file);
        saveRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.save_server:
                        MyApplication.setSavingOnServer(true);
                        nameButton.setVisibility(View.GONE);
                        nameLabel.setVisibility(View.GONE);
                        nameText.setVisibility(View.GONE);
                        break;
                    case R.id.save_file:
                        MyApplication.setSavingOnServer(false);
                        nameButton.setVisibility(View.VISIBLE);
                        nameLabel.setVisibility(View.VISIBLE);
                        nameText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        stepText.setText(formatMillisecondsToString(MyApplication.getStep()));
        stepSumupText.setText(formatMillisecondsToString(MyApplication.getStep()));

        timeText.setText(formatMillisecondsToString(MyApplication.getRecordTime()));

        thresholdText.setText(String.valueOf(MyApplication.getThreshold()));
        thresholdSumupText.setText(String.valueOf(MyApplication.getThreshold()));

        long size = Utils.getinstance().getRecordingRowsCount();
        databaseText.setText(String.valueOf(size));
        databaseSumupText.setText(String.valueOf(size));

        nameText.setText(MyApplication.getFileName());

    }

    /**
     * Determinates if the step and the recording time are correct
     *
     * @param step   The step time value in milliseconds
     * @param record The recording time value in milliseconds
     * @return true if the time is correct, false otherwise
     */
    private boolean isValidTime(long step, long record) {
        if (MyApplication.isRecordingInfinitely()) {
            return true;
        } else if (step <= record) {
            return true;
        }
        return false;
    }

    /**
     * Hides the cardviews that are not supposed to be shown,
     * and goes into recording mode
     */
    private void hideRootComponents() {
        cardviewStep.setVisibility(View.GONE);
        cardviewTime.setVisibility(View.GONE);
        cardviewThreshold.setVisibility(View.GONE);
        cardviewSave.setVisibility(View.GONE);
        cardviewSumup.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the cardviews that are not supposed to be shown,
     * and goes into unrecorded mode
     */
    private void showRootComponents() {
        cardviewStep.setVisibility(View.VISIBLE);
        cardviewTime.setVisibility(View.VISIBLE);
        cardviewThreshold.setVisibility(View.VISIBLE);
        cardviewSave.setVisibility(View.VISIBLE);
        cardviewSumup.setVisibility(View.GONE);
    }

    /**
     * Displays a Dialog to select the Step time value
     */
    @OnClick(R.id.step_button)
    public void stepButton() {
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minutes, int seconds) {
                setStep(hours, minutes, seconds);
            }
        }, getHoursFromMilliseconds(SP.getInt("stepTime", 0)),
                getMinutesFromMilliseconds(SP.getInt("stepTime", 0)),
                getSecondsFromMilliseconds(SP.getInt("stepTime", 1000)),
                true);
        mTimePicker.show();
    }

    /**
     * Displays a Dialog to select the Recording time value
     */
    @OnClick(R.id.time_button)
    public void timeButton() {
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getActivity(), new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minutes, int seconds) {
                setRecordTime(hours, minutes, seconds);
            }
        }, getHoursFromMilliseconds(SP.getInt("recordDuration", 0)),
                getMinutesFromMilliseconds(SP.getInt("recordDuration", 0)),
                getSecondsFromMilliseconds(SP.getInt("recordDuration", 10000)),
                true);
        mTimePicker.show();
    }

    /**
     * Displays a Dialog to select the Threshold value
     */
    @OnClick(R.id.threshold_button)
    public void thresholdButton() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.choose_threshold)
                .inputRange(1, 5)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MyApplication.setThreshold(threshold);
                        thresholdText.setText(String.valueOf(threshold));
                        thresholdSumupText.setText(String.valueOf(threshold));
                    }
                })
                .input(String.valueOf(MyApplication.getThreshold()), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (!isFilenameValid(input.toString()) || input.length() > 5) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            threshold = Integer.parseInt(input.toString());
                        }
                    }
                }).show();
    }

    /**
     * Formats a time in milliseconds into a readable time
     *
     * @param milliseconds The time in millisecond to format
     * @return The time in readable String format
     */
    private String formatMillisecondsToString(long milliseconds) {
        return String.format(Locale.ENGLISH, "%02d", getHoursFromMilliseconds(milliseconds)) + "h"
                + String.format(Locale.ENGLISH, "%02d", getMinutesFromMilliseconds(milliseconds)) + "m"
                + String.format(Locale.ENGLISH, "%02d", getSecondsFromMilliseconds(milliseconds)) + "s";
    }

    /**
     * Gets the hours / minutes / seconds from the time in milliseconds
     *
     * @param milliseconds The time in milliseconds
     * @return The respective converted value
     */
    //region hours / minutes / seconds getters
    private int getHoursFromMilliseconds(int milliseconds) {
        return (int) getHoursFromMilliseconds((long) milliseconds);
    }

    private long getHoursFromMilliseconds(long milliseconds) {
        return ((milliseconds / (1000 * 60 * 60)) % 24);
    }

    private int getMinutesFromMilliseconds(int milliseconds) {
        return (int) getMinutesFromMilliseconds((long) milliseconds);
    }

    private long getMinutesFromMilliseconds(long milliseconds) {
        return ((milliseconds / (1000 * 60)) % 60);
    }

    private int getSecondsFromMilliseconds(int milliseconds) {
        return (int) getSecondsFromMilliseconds((long) milliseconds);
    }

    private long getSecondsFromMilliseconds(long milliseconds) {
        return (milliseconds / 1000) % 60;
    }
    //endregion

    /**
     * Sets the step from the value selected in the time pickers
     *
     * @param hours   The selected hours
     * @param minutes The selected minutes
     * @param seconds The selected seconds
     */
    private void setStep(int hours, int minutes, int seconds) {
        if (hours + minutes + seconds != 0) {
            int timeMilliseconds = seconds * 1000 + minutes * (1000 * 60) + hours * (1000 * 60 * 60);
            if (isValidTime(timeMilliseconds, MyApplication.getRecordTime())) {

                MyApplication.setStep(timeMilliseconds);

                String text = String.format(Locale.ENGLISH, "%02d", hours) + "h"
                        + String.format(Locale.ENGLISH, "%02d", minutes) + "m"
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

    /**
     * Sets the recording time from the value selected in the time pickers
     *
     * @param hours   The selected hours
     * @param minutes The selected minutes
     * @param seconds The selected seconds
     */
    private void setRecordTime(int hours, int minutes, int seconds) {
        if (hours + minutes + seconds != 0) {
            int timeMilliseconds = seconds * 1000 + minutes * (1000 * 60) + hours * (1000 * 60 * 60);
            if (isValidTime(MyApplication.getStep(), timeMilliseconds)) {

                MyApplication.setRecordTime(timeMilliseconds);

                String text = String.format(Locale.ENGLISH, "%02d", hours) + "h"
                        + String.format(Locale.ENGLISH, "%02d", minutes) + "m"
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

    /**
     * Displays a Dialog to select the filename
     */
    @OnClick(R.id.name_button)
    public void nameButton() {
        new MaterialDialog.Builder(getActivity())
                .title("Choose a file name")
                .inputRange(1, 100)
                .alwaysCallInputCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MyApplication.setFileName(filename);
                        nameText.setText(filename);
                    }
                })
                .input(MyApplication.getFileName(), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (!isFilenameValid(input.toString())) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            filename = input.toString();
                        }
                    }
                }).show();
    }

    /**
     * Checks whether or not the chosen filename is valid
     *
     * @param file The filename
     * @return True if valid, false otherwise
     */
    public boolean isFilenameValid(String file) {
        String regex = "^[\\w\\-\\. ]+$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(file).find();
    }

    /**
     * Starts the recording or stops it when pressed
     */
    @OnClick(R.id.record_button)
    public void recordButton() {

        if (!MyApplication.isRecording()) {
            hideRootComponents();
            recordButton.setText(R.string.stop_recording);
            MyApplication.setRecording(true);

            getActivity().startService(new Intent(getActivity(), RecordService.class));

        } else {
            Intent intent = new Intent(MyApplication.stopRecordingFromRecordingFragment);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }

}
