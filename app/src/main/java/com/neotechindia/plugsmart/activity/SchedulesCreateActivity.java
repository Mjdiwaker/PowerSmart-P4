package com.neotechindia.plugsmart.activity;

import android.content.Intent;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.JsonDataFieldsModel;
import com.neotechindia.plugsmart.model.RecyclerSelectorScheduleModel;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;
import com.neotechindia.plugsmart.requestor.SocketRequester;


import java.util.ArrayList;


public class SchedulesCreateActivity extends AppCompatActivity implements IUpdateUiListener , IPacketReceiveCallBack {
    private LinearLayout ll_repeat, ll_start_time, ll_end_time;
    private String dateString;
    private Toolbar toolbar;
    private TimePicker start_picker, end_picker;
    private JsonDataFieldsModel jsonDataFieldsModel;
    private RecyclerSelectorScheduleModel recyclerSelectorScheduleModel;
    int position = -1;
    private ArrayList<JsonDataFieldsModel> arrayList;
    private String ID = "2";
    private TextView tv_everyday, tv_end_time, tv_start_time;
    private Button btn_save;
    private int startTime = 0, endTime = 0;
    private int startTimeCreate = 0, endTimeCreate = 0, startTimeEdit = 0, endTimeEdit = 0;
    private DeviceBean deviceBean;
    private static String sendReqCmd = "";
    private boolean isTimerSet = true, isPacketReceived = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_create);
        ConstantUtil.updateListener = this;
        deviceBean = new DeviceBean();
        jsonDataFieldsModel = new JsonDataFieldsModel();
        MyWifiReceiver.setOnPacketReceiveCallBack(this);
        SocketRequester.setOnPacketReceiveCallBack(this);
        //  recyclerSelectorScheduleModel = new RecyclerSelectorScheduleModel();
        arrayList = new ArrayList<>();

        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        arrayList = getIntent().getParcelableArrayListExtra(ConstantUtil.scheduleList);
        position = getIntent().getIntExtra(ConstantUtil.editSchedule, -1);

        ID = getIntent().getStringExtra(ConstantUtil.ID);
        if (ID == null) {
            ID = "2";
        }
        if (ID.equalsIgnoreCase("1")) {
            jsonDataFieldsModel = arrayList.get(position);
        }
        initViews();
        setListeners();
        setViews();


    }

    private void setViews() {
        if (ID.equalsIgnoreCase("1")) {
            //  if (recyclerSelectorScheduleModel != null) {
            if (position != -1) {
                end_picker.setVisibility(View.VISIBLE);
                start_picker.setVisibility(View.VISIBLE);
                startTimeEdit = Integer.parseInt(jsonDataFieldsModel.getThree());
                endTimeEdit = Integer.parseInt(jsonDataFieldsModel.getFour());
                int startHour = Integer.parseInt(jsonDataFieldsModel.getThree()) / 60;
                int startMin = Integer.parseInt(jsonDataFieldsModel.getThree()) % 60;
                int endHour = Integer.parseInt(jsonDataFieldsModel.getFour()) / 60;
                int endMin = Integer.parseInt(jsonDataFieldsModel.getFour()) % 60;
                dateString = jsonDataFieldsModel.getFive();
                setValue(dateString);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    start_picker.setHour(startHour);
                    start_picker.setMinute(startMin);
                    end_picker.setHour(endHour);
                    end_picker.setMinute(endMin);
                } else {
                    start_picker.setCurrentHour(startHour);
                    start_picker.setCurrentMinute(startMin);
                    end_picker.setCurrentHour(endHour);
                    end_picker.setCurrentMinute(endMin);
                }
            }
        }
    }

    private void setValue(String dateString) {
        switch (dateString) {
            case "12345":
                tv_everyday.setText("Mon To Fri");
                break;
            case "1234567":
                tv_everyday.setText("Daily");
                break;
            default:
                tv_everyday.setText("Custom");
                break;
        }
    }

    private void setListeners() {
        ll_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (end_picker.getVisibility() == View.VISIBLE) {
                    tv_end_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_down_gray_27, 0);
                    //   tv_end_time.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.baseline_expand_up_gray_27),null);
                    end_picker.setVisibility(View.GONE);
                } else {
                    tv_end_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_up_gray_27, 0);
                    //tv_end_time.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.baseline_expand_down_gray_27),null);
                    end_picker.setVisibility(View.VISIBLE);
                }
            }
        });
        ll_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start_picker.getVisibility() == View.VISIBLE) {
                    tv_start_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_down_gray_27, 0);
                    // tv_start_time.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.baseline_expand_up_gray_27),null);

                    start_picker.setVisibility(View.GONE);
                } else {
                    tv_start_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_up_gray_27, 0);
                    //  tv_start_time.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.baseline_expand_up_gray_27),null);
                    start_picker.setVisibility(View.VISIBLE);
                }
            }
        });
        ll_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog.getInstance().showScheduleDialogDaily(SchedulesCreateActivity.this, dateString, new AquasmartDialogListener() {
                    @Override
                    public void onButtonOneClick() {
                        CustomDialog.getInstance().showScheduleDialog(SchedulesCreateActivity.this, dateString, new AquasmartDialogListener() {
                            @Override
                            public void onButtonOneClick() {

                            }

                            @Override
                            public void onButtonTwoClick() {

                            }

                            @Override
                            public void onButtonThreeClick(String password) {
                                dateString = password;
                                setValue(dateString);
                            }
                        });
                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {
                        dateString = password;
                        setValue(dateString);
                    }
                });

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Logger.isPlugSmart) {
                    if (isTimerSet) {
                        timerDisabledPopUp("Alert!!!");
                    } else {
                        startTime = (start_picker.getCurrentHour() * 60) + (start_picker.getCurrentMinute());
                        endTime = (end_picker.getCurrentHour() * 60) + (end_picker.getCurrentMinute());
                        String scheduleDays = dateString;
                        if (scheduleDays.length() == 0) {
                            Toast.makeText(SchedulesCreateActivity.this, "Please select days", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (startTime >= endTime) {
                            endTime = 0;
                            Toast.makeText(SchedulesCreateActivity.this, "End time must be greater than Start Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (JsonDataFieldsModel jsonDataFieldsModel1 : arrayList) {
                            startTimeCreate = Integer.parseInt(jsonDataFieldsModel1.getThree());
                            endTimeCreate = Integer.parseInt(jsonDataFieldsModel1.getFour());
                            int day = Integer.parseInt(jsonDataFieldsModel1.getFive());

                            if (ID.equalsIgnoreCase("1")) {
                                if (startTimeCreate == startTimeEdit && endTimeCreate == endTimeEdit && scheduleDays.equalsIgnoreCase(String.valueOf(day))) {

                                } else {
                                    if (startTime >= startTimeCreate && startTime < endTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                        Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                    if (startTime < startTimeCreate && endTime > startTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                        Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                }
                            } else {
                                if (startTime >= startTimeCreate && startTime < endTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                    Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                    return;

                                }
                                if (startTime < startTimeCreate && endTime > startTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                    Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                    return;

                                }
                            }

                        }
                        if (ID.equalsIgnoreCase("2")) {
                            if (ConstantUtil.isForAPModeOnly) {
                                sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                                MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);

                            } else {
                                sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                                Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);
                            }
                        } else {

                            if (ConstantUtil.isForAPModeOnly) {
                                sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                                MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);
                            } else {
                                sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                                Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);

                            }


                        }
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        isPacketReceived = false;
                        timerResponseDialog();
                    }
                } else {
                    startTime = (start_picker.getCurrentHour() * 60) + (start_picker.getCurrentMinute());
                    endTime = (end_picker.getCurrentHour() * 60) + (end_picker.getCurrentMinute());
                    String scheduleDays = dateString;
                    if (scheduleDays.length() == 0) {
                        Toast.makeText(SchedulesCreateActivity.this, "Please select days", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startTime >= endTime) {
                        endTime = 0;
                        Toast.makeText(SchedulesCreateActivity.this, "End time must be greater than Start Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (JsonDataFieldsModel jsonDataFieldsModel2 : arrayList) {
                        startTimeCreate = Integer.parseInt(jsonDataFieldsModel2.getThree());
                        endTimeCreate = Integer.parseInt(jsonDataFieldsModel2.getFour());
                        int day = Integer.parseInt(jsonDataFieldsModel2.getFive());
                        if (ID.equalsIgnoreCase("1")) {
                            if (startTimeCreate == startTimeEdit && endTimeCreate == endTimeEdit && scheduleDays.equalsIgnoreCase(String.valueOf(day))) {

                            } else {
                                if (startTime >= startTimeCreate && startTime < endTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                    Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                    return;

                                }
                                if (startTime < startTimeCreate && endTime > startTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                    Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                    return;

                                }
                            }
                        } else {
                            if (startTime >= startTimeCreate && startTime < endTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                return;

                            }
                            if (startTime < startTimeCreate && endTime > startTimeCreate && String.valueOf(day).contains(scheduleDays)) {
                                Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        }

                    }
                    if (ID.equalsIgnoreCase("2")) {
                        if (ConstantUtil.isForAPModeOnly) {
                            sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                            MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);

                        } else {
                            sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                            Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);
                        }
                    } else {

                        if (ConstantUtil.isForAPModeOnly) {
                            sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                            MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);
                        } else {
                            sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                            Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);
                        }
                    }
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    isPacketReceived = false;
                    timerResponseDialog();
                }
            }
        });
    }

    private void timerDisabledPopUp(String msg) {
        String msg2 = getString(R.string.timerDisabled);

        CustomDialog.getInstance().showYesNoAlert(SchedulesCreateActivity.this, msg, msg2, "Yes", "No", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {
                startTime = (start_picker.getCurrentHour() * 60) + (start_picker.getCurrentMinute());
                endTime = (end_picker.getCurrentHour() * 60) + (end_picker.getCurrentMinute());
                String scheduleDays = dateString;
                if (scheduleDays.length() == 0) {
                    Toast.makeText(SchedulesCreateActivity.this, "Please select days", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startTime >= endTime) {
                    endTime = 0;
                    Toast.makeText(SchedulesCreateActivity.this, "End time must be greater than Start Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (JsonDataFieldsModel jsonDataFieldsModel3 : arrayList) {
                    startTimeCreate = Integer.parseInt(jsonDataFieldsModel3.getThree());
                    endTimeCreate = Integer.parseInt(jsonDataFieldsModel3.getFour());
                    if (ID.equalsIgnoreCase("1")) {
                        if (startTimeCreate == startTimeEdit && endTimeCreate == endTimeEdit) {

                        } else {
                            if (startTime >= startTimeCreate && startTime < endTimeCreate) {
                                Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                return;

                            }
                            if (startTime < startTimeCreate && endTime > startTimeCreate) {
                                Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        }
                    } else {
                        if (startTime >= startTimeCreate && startTime < endTimeCreate) {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        if (startTime < startTimeCreate && endTime > startTimeCreate) {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Overlaps, Please delete or edit the existing schedule", Toast.LENGTH_SHORT).show();
                            return;

                        }
                    }
                }
                if (ID.equalsIgnoreCase("2")) {
                    if (ConstantUtil.isForAPModeOnly) {
                        sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                        MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);

                    } else {
                        sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", "", "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);

                    }


                } else {

                    if (ConstantUtil.isForAPModeOnly) {
                        sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                        MyWifiReceiver.write(PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), CommandUtil.cmd_create_or_save_schedule);
                    } else {
                        sendReqCmd = CommandUtil.cmd_create_or_save_schedule;
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.createOrSaveSchedule("1", jsonDataFieldsModel.getOne(), "1", "0", "" + startTime, "" + endTime, scheduleDays), SchedulesCreateActivity.this);

                    }


                }
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

    private CountDownTimer countDownTimer;

    private void timerResponseDialog() {
        Util.showLoader(SchedulesCreateActivity.this);
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
                            Toast.makeText(SchedulesCreateActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }

                    }
                });
                countDownTimer = null;
            }


        }.start();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_schedules_create));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dateString = "123";
        ll_repeat = findViewById(R.id.ll_repeat);
        ll_start_time = findViewById(R.id.ll_start_time);
        ll_end_time = findViewById(R.id.ll_end_time);
        start_picker = findViewById(R.id.start_picker);
        end_picker = findViewById(R.id.end_picker);
        tv_everyday = findViewById(R.id.tv_everyday);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        btn_save = findViewById(R.id.btn_save);
        setValue(dateString);
        getTimerDetails();
    }

    private void getTimerDetails() {
        if (ConstantUtil.isForAPModeOnly) {
            MyWifiReceiver.write(PacketsUrl.getTimer(), CommandUtil.cmd_get_timer);
        } else {
            Util.sendPacketStationMode(deviceBean, PacketsUrl.getTimer(), SchedulesCreateActivity.this);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(SchedulesCreateActivity.this, SchedulesViewActivity.class);
                i.putExtra(ConstantUtil.stationList, deviceBean);
                startActivity(i);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SchedulesCreateActivity.this, SchedulesViewActivity.class);
        i.putExtra(ConstantUtil.stationList, deviceBean);
        startActivity(i);
        finish();
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_create_or_save_schedule)) {
            Util.hideLoader();
            String status = JsonUtil.getJsonDataByField("02", data);
            isPacketReceived = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (status.equalsIgnoreCase("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ID.equalsIgnoreCase("2")) {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Created Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Edited  Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                Intent i = new Intent(SchedulesCreateActivity.this, SchedulesViewActivity.class);
                i.putExtra(ConstantUtil.stationList, deviceBean);
                startActivity(i);
                finish();

            }
        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
            } else {
                isTimerSet = true;
            }
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_create_or_save_schedule)) {
            Util.hideLoader();
            String status = JsonUtil.getJsonDataByField("02", data);
            isPacketReceived = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (status.equalsIgnoreCase("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ID.equalsIgnoreCase("2")) {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Created Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SchedulesCreateActivity.this, "Schedule Edited  Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                Intent i = new Intent(SchedulesCreateActivity.this, SchedulesViewActivity.class);
                i.putExtra(ConstantUtil.stationList, deviceBean);
                startActivity(i);
                finish();

            }
        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_timer)) {
            if (data.equalsIgnoreCase("00000")) {
                isTimerSet = false;
            } else {
                isTimerSet = true;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.hideLoader();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.hideLoader();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onPacketReceiveSuccess(String response, int modifiedPosition) {

    }

    @Override
    public void onPacketReceiveError(String errorMessage, int modifiedPosition) {

    }
}
