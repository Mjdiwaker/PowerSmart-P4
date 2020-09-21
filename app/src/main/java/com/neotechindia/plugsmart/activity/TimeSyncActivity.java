package com.neotechindia.plugsmart.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class TimeSyncActivity extends AppCompatActivity implements IUpdateUiListener {
    private TextView tv_device_time, tv_plug_Time;
    private Button btn_sync_time;
    private DeviceBean deviceBean;
    private Toolbar toolbar;
    private TimeSyncActivity mTimeSyncActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sync);
        deviceBean = new DeviceBean();
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        ConstantUtil.updateListener = this;
        mTimeSyncActivity = TimeSyncActivity.this;
        initViews();
        viewsListener();
        getTimeFromPlug();
    }

    private void getTimeFromPlug() {
        String packet = PacketsUrl.getTimeSyncGetter();
        if (ConstantUtil.isForAPModeOnly) {
            MyWifiReceiver.write(packet, CommandUtil.cmd_get_device_Time);
        } else {
            Util.sendPacketStationMode(deviceBean, packet, mTimeSyncActivity);
        }
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

    private void viewsListener() {
        btn_sync_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerResponseDialog();
                String packet = PacketsUrl.setTimeSyncSetter(ConstantUtil.getDateByFormatWeek(ConstantUtil.getDateByFormat("dd-MM-yy,HH:mm:ss")));
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(packet, CommandUtil.cmd_set_time_sync);
                } else {
                    Util.sendPacketStationMode(deviceBean, packet, mTimeSyncActivity);
                }
                isPacketReceived = false;
            }
        });
    }

    private CountDownTimer countDownTimer;
    private boolean isPacketReceived = false;

    private void timerResponseDialog() {
        Util.showLoader(mTimeSyncActivity);
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
                            Toast.makeText(mTimeSyncActivity, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                countDownTimer = null;
            }

        }.start();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.timeSync));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tv_device_time = findViewById(R.id.tv_device_time);
        tv_plug_Time = findViewById(R.id.tv_plug_Time);
        btn_sync_time = findViewById(R.id.btn_sync_time);
        tv_device_time.setText(ConstantUtil.getDateByFormatWeek(ConstantUtil.getDateByFormat("dd-MM-yy , HH:mm:ss")));
    }

    private void setUiData(String dates) {
        String temp = dates;
        if (dates.length() > 4) {
            temp = dates.substring(0, dates.length() - 9) + " , " + (dates.substring(dates.length() - 8, dates.length())).replace('-', ':');
        }
        tv_device_time.setText(ConstantUtil.getDateByFormatWeek(ConstantUtil.getDateByFormat("dd-MM-yy , HH:mm:ss")));
        tv_plug_Time.setText(temp);
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        showNextScreen(packets, mac);
    }

    @Override
    public void onPacketReceived(String packets) {
        showNextScreen(packets, null);
    }

    private void showNextScreen(String packets, String mac) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_device_Time)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Util.hideLoader();
                    try {
                        JSONObject jj = new JSONObject(data);
                        setUiData(jj.getString("01"));
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_time_sync)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isPacketReceived = true;
                    if (data.equalsIgnoreCase("0")) {
                        getTimeFromPlug();
                        Util.hideLoader();

                        Toast.makeText(mTimeSyncActivity, "Time Sync Successfully ", Toast.LENGTH_SHORT).show();
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                    } else {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        Util.hideLoader();
                        Toast.makeText(mTimeSyncActivity, "Time Sync Failed", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        }
    }

}
