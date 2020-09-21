package com.neotechindia.plugsmart.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;


public class TimerActivity extends AppCompatActivity implements IUpdateUiListener {
    private Button btn_cancel, btn_save;
    private NumberPicker numberPickerHour, numberPickerMin, numberPickerSec;
    int hour, min, sec, time;
    boolean isTimerSet = true, isCancelledTimer = false;
    Toolbar toolbar;
    DeviceBean deviceBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);
        deviceBean = new DeviceBean();
        ConstantUtil.updateListener = this;
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        initView();
        viewsListener();
        setViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.Timer));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setMaxValue(59);
        numberPickerMin.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerSec.setMinValue(0);
        numberPickerSec.setMaxValue(59);
        numberPickerSec.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isPacketReceived = false;
        timerResponseDialog();
        if (ConstantUtil.isForAPModeOnly) {
            MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
        } else {
            Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), TimerActivity.this);
        }
        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
        btn_save.setEnabled(true);
        btn_cancel.setEnabled(false);
        numberPickerHour.setEnabled(true);
        numberPickerMin.setEnabled(true);
        numberPickerSec.setEnabled(true);
    }

    private void schedulesDisabledPopUp(String msg) {
        String msg2 = getString(R.string.schedulesDisabled);

        CustomDialog.getInstance().showYesNoAlert(TimerActivity.this, msg, msg2, "Yes", "No", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {

                time = ((numberPickerHour.getValue() * 60) + numberPickerMin.getValue()) * 60 + numberPickerSec.getValue();
                hour = numberPickerHour.getValue();
                min = numberPickerMin.getValue();
                sec = numberPickerSec.getValue();
                Util.sendPacketStationMode(deviceBean, PacketsUrl.setTimer(String.valueOf(time)), TimerActivity.this);

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                isPacketReceived = false;
                timerResponseDialog();
            }

            @Override
            public void onButtonTwoClick() {

                finish();

            }

            @Override
            public void onButtonThreeClick(String password) {

            }
        });
    }

    private void viewsListener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConstantUtil.isForAPModeOnly == true) {
                    hour = numberPickerHour.getValue();
                    min = numberPickerMin.getValue();
                    sec = numberPickerSec.getValue();
                    time = ((numberPickerHour.getValue() * 60) + numberPickerMin.getValue()) * 60 + numberPickerSec.getValue();
                    MyWifiReceiver.write(PacketsUrl.setTimer(String.valueOf(time)), CommandUtil.cmd_set_timer);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    isPacketReceived = false;
                    timerResponseDialog();
                } else {
                    if (!isTimerSet) {
                        schedulesDisabledPopUp("Alert!!!");
                    } else {
                        hour = numberPickerHour.getValue();
                        min = numberPickerMin.getValue();
                        sec = numberPickerSec.getValue();
                        time = ((numberPickerHour.getValue() * 60) + numberPickerMin.getValue()) * 60 + numberPickerSec.getValue();
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.setTimer(String.valueOf(time)), TimerActivity.this);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        isPacketReceived = false;
                        timerResponseDialog();
                    }

                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = 0;
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.setTimer(String.valueOf(time)), CommandUtil.cmd_set_timer);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.setTimer(String.valueOf(time)), TimerActivity.this);


                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                isPacketReceived = false;
                timerResponseDialog();

            }
        });

    }

    private CountDownTimer countDownTimer;
    private boolean isPacketReceived = false;

    private void timerResponseDialog() {
        Util.showLoader(TimerActivity.this);
        countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Util.hideLoader();
                        if (!isPacketReceived) {
                            Toast.makeText(TimerActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }

                    }
                });
                countDownTimer = null;
            }


        }.start();
    }

    private void initView() {
        numberPickerHour = findViewById(R.id.numberPickerHour);
        numberPickerMin = findViewById(R.id.numberPickerMin);
        numberPickerSec = findViewById(R.id.numberPickerSec);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
    }

    @Override
    public void onPacketReceived(String packets, String mac) {

        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(true);
                        btn_cancel.setEnabled(false);
                        numberPickerHour.setEnabled(true);
                        numberPickerMin.setEnabled(true);
                        numberPickerSec.setEnabled(true);
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isTimerSet = true;
                        btn_save.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_cancel.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(false);
                        btn_cancel.setEnabled(true);
                        numberPickerHour.setEnabled(false);
                        numberPickerMin.setEnabled(false);
                        numberPickerSec.setEnabled(false);

                        remainingTimer(data);
                    }
                });


            }


        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_timer)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equalsIgnoreCase("0")) {
                        Toast.makeText(TimerActivity.this, "Please Enter Some Value ", Toast.LENGTH_LONG).show();
                        if (countDownTimer1 != null) {
                            countDownTimer1.cancel();
                            countDownTimer1 = null;
                        }
                        isCancelledTimer = true;
                        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(true);
                        btn_cancel.setEnabled(false);
                        numberPickerHour.setEnabled(true);
                        numberPickerMin.setEnabled(true);
                        numberPickerSec.setEnabled(true);
                        numberPickerHour.setValue(0);
                        numberPickerMin.setValue(0);
                        numberPickerSec.setValue(0);
                    } else {
                        Toast.makeText(TimerActivity.this, "Set Time is " + hour + " hours and " + min + " minutes", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(true);
                        btn_cancel.setEnabled(false);
                        numberPickerHour.setEnabled(true);
                        numberPickerMin.setEnabled(true);
                        numberPickerSec.setEnabled(true);
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isTimerSet = true;
                        btn_save.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_cancel.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(false);
                        btn_cancel.setEnabled(true);
                        numberPickerHour.setEnabled(false);
                        numberPickerMin.setEnabled(false);
                        numberPickerSec.setEnabled(false);

                        remainingTimer(data);
                    }
                });


            }


        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_timer)) {
            isPacketReceived = true;
            Util.hideLoader();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equalsIgnoreCase("0")) {
                        Toast.makeText(TimerActivity.this, "Please Enter Some Value", Toast.LENGTH_LONG).show();
                        if (countDownTimer1 != null) {
                            countDownTimer1.cancel();
                            countDownTimer1 = null;
                        }
                        isCancelledTimer = true;
                        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(true);
                        btn_cancel.setEnabled(false);
                        numberPickerHour.setEnabled(true);
                        numberPickerMin.setEnabled(true);
                        numberPickerSec.setEnabled(true);
                        numberPickerHour.setValue(0);
                        numberPickerMin.setValue(0);
                        numberPickerSec.setValue(0);
                    } else {
                        Toast.makeText(TimerActivity.this, "Set Time is " + hour + " hours and " + min + " minutes", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            });
        }
    }

    private CountDownTimer countDownTimer1;
    int SecondsElapsed = 0;

    private void remainingTimer(final String data) {

        countDownTimer1 = new CountDownTimer((Long.parseLong(data) + 2) * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (!isCancelledTimer) {


                    SecondsElapsed++;
                    //Log.d("seconds remaining Timer", "seconds remaining Timer:" + millisUntilFinished / 1000);
//                Log.d("timer1", "seconds remaining Timer:" +Integer.parseInt(data));
                    Log.d("timer", "seconds remaining Timer:" + SecondsElapsed);
                    if (SecondsElapsed <= Integer.parseInt(data)) {
                        int mySeconds = Integer.parseInt(data) - SecondsElapsed;
                        int myHours = mySeconds / 3600; //3600 Seconds in 1 hour
                        mySeconds %= 3600;
                        int myMinutes = mySeconds / 60; //60 Seconds in a minute
                        mySeconds %= 60;
                        numberPickerHour.setValue(myHours);
                        numberPickerMin.setValue(myMinutes);
                        numberPickerSec.setValue(mySeconds);

                        // lbl_remaining_time.Text = "Time Remaining: " + myHours + ":" + myMinutes + ":" + mySeconds;
                        // update the count down timer with 1 second here

                    } else {
                        numberPickerHour.setValue(0);
                        numberPickerMin.setValue(0);
                        numberPickerSec.setValue(0);
                        isTimerSet = false;
                        btn_cancel.setBackgroundColor(Color.parseColor("#bababa"));
                        btn_save.setBackgroundColor(Color.parseColor("#1fabc7"));
                        btn_save.setEnabled(true);
                        btn_cancel.setEnabled(false);
                        numberPickerHour.setEnabled(true);
                        numberPickerMin.setEnabled(true);
                        numberPickerSec.setEnabled(true);
                    }
                }
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
                countDownTimer1 = null;

            }

        }.start();
    }

}

