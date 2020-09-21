package com.neotechindia.plugsmart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.adapter.PlugSmartDeviceAdapter;
import com.neotechindia.plugsmart.adapter.SchedulesAdapter;
import com.neotechindia.plugsmart.adapter.SettingsAdapter;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.ConnectionListener;
import com.neotechindia.plugsmart.listeners.IAuthentcUser;
import com.neotechindia.plugsmart.listeners.IDeviceInfo;
import com.neotechindia.plugsmart.listeners.IPlugOnOff;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.listeners.TimerListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements IUpdateUiListener, ConnectionListener, NavigationView.OnNavigationItemSelectedListener, TimerListener, IAuthentcUser, IDeviceInfo, IPlugOnOff {
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mtoggle;
    private RecyclerView rv_device_list;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout btn_home, btn_schedules, btn_system;
    private PlugSmartDeviceAdapter deviceRecyclerAdapter;
    private SchedulesAdapter schedulesAdapter;
    private SettingsAdapter settingsAdapter;
    public static ArrayList<DeviceBean> arrayList;
    private Toolbar toolbar;
    private int mode = 0;
    public int set_0777 = 0;
    private ImageView iv_home, iv_setting, iv_schedule;
    private TextView tv_home, tv_setting, tv_schedule;
    private boolean isFromAddGadget = false;
    private String macID = "";
    public String serial_no = "";
    public String licence_no = "";

    private CountDownTimer countDownTimer;
    private boolean isPacketReceived = false;
    private DeviceBean deviceBean = new DeviceBean();
    public static String sendReqCmd = "";
    public static String sendReqCmd2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstantUtil.connectionListener = this;
        ConstantUtil.updateListener = this;
        ConstantUtil.timerListener = this;
        ConstantUtil.authentcUser = this;
        ConstantUtil.deviceInfo = this;
        ConstantUtil.iPlugOnOff = this;
        initViews();
        listeners();
        Drawer();


        //   user_profile_name.setText("Hello," + name);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            showListForHome();
        } else if (id == R.id.schedule) {
            showListForSchedules();
        }
//        else if (id == R.id.timer) {
//            showListForSchedules();
//        }
        else if (id == R.id.system) {
            showListForSettings();
        } else if (id == R.id.about) {

            Intent notiIntent = new Intent(this, AboutActivity.class);
            startActivity(notiIntent);

        } else if (id == R.id.custmer) {
            Intent notiIntent = new Intent(this, Contact_Us.class);
            startActivity(notiIntent);
        } else if (id == R.id.edge_registration) {

            if (!ConstantUtil.isForAPModeOnly) {
                if (id == R.id.edge_registration) {
                    Intent notiIntent = new Intent(this, Registration_data.class);
                    startActivity(notiIntent);
                }
            }
            if (ConstantUtil.isForAPModeOnly) {
                if (id == R.id.edge_registration) {
                    Toast.makeText(this, "Please Connect to Station Mode..", Toast.LENGTH_SHORT).show();
                }
            }
        }

         else if (id == R.id.weather_service) {

            if (ConstantUtil.isForAPModeOnly) {
                if (id == R.id.weather_service) {
                    Intent notiIntent = new Intent(this, Weather.class);
                    startActivity(notiIntent);
                }
            }
            if (!ConstantUtil.isForAPModeOnly) {
                if (id == R.id.weather_service) {
                    Toast.makeText(this, "Please Connect to Edge Mode Mode..", Toast.LENGTH_SHORT).show();
                }
            }


           // startActivity(new Intent(MainActivity.this,Weather.class));

        }
        else if (id==R.id.UpdateLic){

            startActivity(new Intent(MainActivity.this,UpdateLic.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Drawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        String name = (firmwareSharedPref.getString(ConstantUtil.clientName, ""));

        View headerview = navigationView.getHeaderView(0);
        TextView usernameTextview = headerview.findViewById(R.id.user_profile_name);
       usernameTextview.setText("Hello " + name);
      //  usernameTextview.setText("Hello User");
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_main));

        isFromAddGadget = getIntent().getBooleanExtra(ConstantUtil.isFromAddGadget, false);
        macID = getIntent().getStringExtra(ConstantUtil.AddGadgetMac);
        serial_no = getIntent().getStringExtra(ConstantUtil.serialNumber);
        licence_no = getIntent().getStringExtra(ConstantUtil.liscenceNumber);
        setSupportActionBar(toolbar);
        iv_home = findViewById(R.id.iv_home);
        iv_schedule = findViewById(R.id.iv_schedule);
        iv_setting = findViewById(R.id.iv_setting);
        tv_home = findViewById(R.id.tv_home);
        tv_schedule = findViewById(R.id.tv_schedule);
        //user_profile_name = findViewById(R.id.user_profile_name);
        tv_setting = findViewById(R.id.tv_setting);
        btn_home = findViewById(R.id.btn_home);
        btn_schedules = findViewById(R.id.btn_schedules);
        btn_system = findViewById(R.id.btn_system);
        arrayList = new ArrayList<>();
        rv_device_list = findViewById(R.id.rv_device_list);
        layoutManager = new LinearLayoutManager(this);
        rv_device_list.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_device_list.getContext(),
                DividerItemDecoration.VERTICAL);
        rv_device_list.addItemDecoration(dividerItemDecoration);

        if (ConstantUtil.isForAPModeOnly) {
            if (!Logger.isPlugSmart) {
                String packet = PacketsUrl.setTimeSyncSetter(ConstantUtil.getDateByFormatWeek(ConstantUtil.getDateByFormat("dd-MM-yy,HH:mm:ss")));
                MyWifiReceiver.write(packet, CommandUtil.cmd_set_time_sync);
            } else {
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                MyWifiReceiver.write(PacketsUrl.getPowerStatus(firmwareSharedPref.getString(ConstantUtil.gadgetName, "")), CommandUtil.cmd_get_power_status);
            }
        } else {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = firmwareSharedPref.getString(ConstantUtil.arrayList, "");
            Type type = new TypeToken<ArrayList<DeviceBean>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
            if (arrayList != null) {
                if (arrayList.size() > 0) {
                    Collections.sort(arrayList, new Comparator<DeviceBean>() {
                        @Override
                        public int compare(DeviceBean deviceBean, DeviceBean t1) {
                            return deviceBean.name.toLowerCase().compareTo(t1.name.toLowerCase());
                        }


                    });
                }
            }
            if (!Logger.isPlugSmart) {
                String packet = PacketsUrl.setTimeSyncSetter(ConstantUtil.getDateByFormatWeek(ConstantUtil.getDateByFormat("dd-MM-yy,HH:mm:ss")));
                Util.sendPacketStationMode(deviceBean, packet, MainActivity.this);
            }
            //Following code: for enabling registration process in station mode
            if (ConstantUtil.isfromMainActivity) {
                if (MyWifiReceiver.isWifiConnected(getApplicationContext())) {
                }
                timerResponseDialog();

            } else {
                if (isFromAddGadget) {
                    SharedPreferences firmwareSharedPref1 = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);


                    //et_serialno_client_registration.setText(sharedPreferences.getString(ConstantUtil.serialNumber, ""));
                    //et_licenceno.setText(sharedPreferences.getString(ConstantUtil.liscenceNumber, ""));
                    ArrayList<DeviceBean> arrayListNew = new ArrayList<>();
                    for (DeviceBean d : arrayList
                    ) {
                        if (d.macId.equalsIgnoreCase(macID)) {
                            d.setName(firmwareSharedPref1.getString(ConstantUtil.gadgetName, ""));
                            d.setIconType(firmwareSharedPref1.getString(ConstantUtil.gadgetPos, "0"));
                            d.setSerial_no(sharedPreferences.getString(ConstantUtil.serialNumber, ""));
                            d.setLicense_no(sharedPreferences.getString(ConstantUtil.liscenceNumber, ""));
                        }


                        arrayListNew.add(d);
                    }
                    arrayList.clear();
                    arrayList = new ArrayList<>();
                    arrayList = arrayListNew;
                    SharedPreferences firmwareSharedPref2 = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = firmwareSharedPref2.edit();
                    Gson gson1 = new Gson();
                    String json1 = gson1.toJson(arrayList);
                    editor.putString(ConstantUtil.arrayList, json1);
                    editor.commit();
                    showListForSettings();

                } else {
                    showListForHome();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {

        MyWifiReceiver.isWifiConnected(this);
        timerResponseDialog();

        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void listeners() {
        btn_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListForSettings();
            }
        });
        btn_schedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListForSchedules();
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstantUtil.updateListener = MainActivity.this;
                if (isFromAddGadget) {
                    isFromAddGadget = false;
                }
                if (ConstantUtil.isForAPModeOnly) {
                    SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    MyWifiReceiver.write(PacketsUrl.getPowerStatus(firmwareSharedPref.getString(ConstantUtil.gadgetName, "")), CommandUtil.cmd_get_power_status);
                    isPacketReceived = false;
                    timerResponseDialog();
                }
                showListForHome();
            }
        });

    }

    Timer timer;
    private int count = 0;
    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            // timer = null;
        }
    }

    private void timerResponseDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Util.showLoader(MainActivity.this);
            }
        });
        if (timer != null) {
            timer.cancel();
            timer = null;
            count = 0;
        }
        timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 1) {
                    //if (isPacketReceived) {
                    Util.hideLoader();
                    Util.hideLoader();
                    //}
                }
                if (count > 18) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Util.hideLoader();
                            if (!isPacketReceived) {
                                Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                            }
                            if (isForOnOff) {

                                //  Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();

                                // *//*deviceRecyclerAdapter = new PlugSmartDeviceAdapter(MainActivity.this, arrayList);
                                //  rv_device_list.setAdapter(deviceRecyclerAdapter);*//*

                            }
                        }
                    });

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        count = 0;
                    }

                }
                System.out.println(count);
            }
        };
        timer.scheduleAtFixedRate(t, 1000, 1000);

        //   myCountDownTimer = new MyCountDownTimer(1000, 20000);
        //  myCountDownTimer.start();
        /*countDownTimer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                count++;
                if (count > 5) {
                    if (isPacketReceived) {
                        Util.hideLoader();
                    }
                }
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.hideLoader();
                        if (!isPacketReceived) {
                            Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }
                        if (isForOnOff) {

                            Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();

                            *//*deviceRecyclerAdapter = new PlugSmartDeviceAdapter(MainActivity.this, arrayList);
                            rv_device_list.setAdapter(deviceRecyclerAdapter);*//*

                        }
                    }
                });
                countDownTimer = null;
            }

        }.start();*/
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("", "seconds remaining class: " + millisUntilFinished / 1000);
            int progress = (int) (millisUntilFinished / 1000);
            if (progress == 14) {
                cancel();
                Util.hideLoader();
            }
        }

        @Override
        public void onFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Util.hideLoader();
                    if (!isPacketReceived) {
                        Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                    }
                    if (isForOnOff) {

                        Toast.makeText(MainActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                            /*deviceRecyclerAdapter = new PlugSmartDeviceAdapter(MainActivity.this, arrayList);
                            rv_device_list.setAdapter(deviceRecyclerAdapter);*/
                    }
                }
            });
            cancel();
        }
    }

    private void showListForHome() {
        mode = 0;
        iv_home.setBackground(getResources().getDrawable(R.drawable.home_blue));
        iv_setting.setBackground(getResources().getDrawable(R.drawable.setting_black));
        iv_schedule.setBackground(getResources().getDrawable(R.drawable.schudele_black));
        tv_home.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_schedule.setTextColor(getResources().getColor(R.color.grey));
        tv_setting.setTextColor(getResources().getColor(R.color.grey));
        deviceRecyclerAdapter = new PlugSmartDeviceAdapter(getApplicationContext(), arrayList);
//        deviceRecyclerAdapter.notifyDataSetChanged();
//        deviceRecyclerAdapter.notify();
        rv_device_list.setAdapter(deviceRecyclerAdapter);
    }

    private void showListForSchedules() {
        mode = 1;
        iv_home.setBackground(getResources().getDrawable(R.drawable.home_black));
        iv_setting.setBackground(getResources().getDrawable(R.drawable.setting_black));
        iv_schedule.setBackground(getResources().getDrawable(R.drawable.schudele_blue));
        tv_home.setTextColor(getResources().getColor(R.color.grey));
        tv_schedule.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_setting.setTextColor(getResources().getColor(R.color.grey));
        schedulesAdapter = new SchedulesAdapter(getApplicationContext(), arrayList);
        rv_device_list.setAdapter(schedulesAdapter);
    }

    private void showListForSettings() {
        mode = 2;
        iv_home.setBackground(getResources().getDrawable(R.drawable.home_black));
        iv_setting.setBackground(getResources().getDrawable(R.drawable.setting_blue));
        iv_schedule.setBackground(getResources().getDrawable(R.drawable.schudele_black));
        tv_home.setTextColor(getResources().getColor(R.color.grey));
        tv_schedule.setTextColor(getResources().getColor(R.color.grey));
        tv_setting.setTextColor(getResources().getColor(R.color.colorAccent));
        settingsAdapter = new SettingsAdapter(getApplicationContext(), arrayList);
        rv_device_list.setAdapter(settingsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//
//        if (mtoggle.onOptionsItemSelected(item)) {
//            return true;
//
//        }
//        if (id == R.id.action_info) {
//            Intent notiIntent = new Intent(this, AboutActivity.class);
//            startActivity(notiIntent);
//        }
        if (id == R.id.action_refresh) {
            MyWifiReceiver.isWifiConnected(this);
            timerResponseDialog();
        }
//        if (!ConstantUtil.isForAPModeOnly) {
//            if (id == R.id.info_logo) {
//                Intent notiIntent = new Intent(this, Registration_data.class);
//                startActivity(notiIntent);
//            }
//        }
//        if (ConstantUtil.isForAPModeOnly) {
//            if (id == R.id.info_logo) {
//                Toast.makeText(this, "Please Connect to Station Mode..", Toast.LENGTH_SHORT).show();
//            }
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWifiConnectStatus(boolean isConnected) {
        if (isConnected) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);

            MyWifiReceiver.write(PacketsUrl.getPowerStatus(firmwareSharedPref.getString(ConstantUtil.gadgetName, "")), CommandUtil.cmd_get_power_status);
        }
    }

    @Override
    public void onWifiConnectStatus(boolean isConnected, final ArrayList<DeviceBean> arrayList) {
        if (isConnected) {

            this.arrayList = new ArrayList<>();
            this.arrayList = arrayList;
            if (this.arrayList != null) {
                if (this.arrayList.size() > 0) {
                    Collections.sort(this.arrayList, new Comparator<DeviceBean>() {
                        @Override
                        public int compare(DeviceBean deviceBean, DeviceBean t1) {
                            return deviceBean.name.toLowerCase().compareTo(t1.name.toLowerCase());
                        }


                    });
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 /*   if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }*/
                    if (timer != null) {
                        timer.cancel();
                        // timer = null;
                    }
                    //  Util.hideLoader();
                    switch (mode) {
                        case 0: {
                            isPacketReceived = true;
                            showListForHome();
                            break;
                        }
                        case 1: {
                            showListForSchedules();
                            break;
                        }
                        case 2: {
                            showListForSettings();
                            break;
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onPacketReceived(String packets, String mac) {                                      //station mode
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);
        //Log.i("tester","got 1 "+data);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_power_status)) {
            // sendReqCmd2="";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   /* if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }*/

                    // Util.hideLoader();
                    isPacketReceived = true;
                }
            });
            if (data.equalsIgnoreCase("0")) {
                for (DeviceBean d : arrayList) {
                    if (d.macId.equalsIgnoreCase(mac)) {
                        d.switchs = true;
                        d.source = "on";
                    }
                }
//                deviceRecyclerAdapter = new PlugSmartDeviceAdapter(this, arrayList);
//                deviceRecyclerAdapter.notifyDataSetChanged();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        rv_device_list.setAdapter(deviceRecyclerAdapter);
//                    }
//                });

            }
            if (data.equalsIgnoreCase("1")) {
                for (DeviceBean d : arrayList) {
                    if (d.macId.equalsIgnoreCase(mac)) {
                        d.switchs = false;
                        d.source = "off";
                    }
                }
//                deviceRecyclerAdapter = new PlugSmartDeviceAdapter(this, arrayList);
//                deviceRecyclerAdapter.notifyDataSetChanged();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        rv_device_list.setAdapter(deviceRecyclerAdapter);
//                    }
//                });
            }


            //else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_power_status ) && !sendReqCmd2.equalsIgnoreCase("7777"))
        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_power_status)) {
            //broadcast station
            Log.i("test", "0777");
            final String name = JsonUtil.getJsonDataByField("01", data);
            final String status = JsonUtil.getJsonDataByField("02", data);

            //  if (status.equals())
            if (MyWifiReceiver.checkbtn_press == false) {                                        //shubham
                if (isFromAddGadget) {
                    arrayList = new ArrayList<>();
                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.setName(name);
                    if (status.equalsIgnoreCase("1")) {                                  //change
                        deviceBean.setSwitchs(false);
                        deviceBean.setSource("off");
                    } else {
                        deviceBean.setSwitchs(true);
                        deviceBean.setSource("on");
                    }
                    arrayList.add(deviceBean);
                    showListForSettings();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showListForHome(name, status);
                        }
                    });

                }
            } else {
                MyWifiReceiver.checkbtn_press = false;                                               //shubham
            }
        } else if (cmd.equals(CommandUtil.authentication)) {
            String dataFieldFirst = JsonUtil.getJsonDataByField("01", data);
            String modeOFIotDevice = JsonUtil.getJsonDataByField("02", data);

            if (modeOFIotDevice.equalsIgnoreCase("1")) {
                if (dataFieldFirst.equalsIgnoreCase("1")) {
                    ConstantUtil.isfromMainActivity = true;
                    // Client Registration
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, ClientRegistrationActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.putExtra(ConstantUtil.ID, "1");
                            startActivity(i);
                            finish();
                        }
                    });

                } else if (dataFieldFirst.equalsIgnoreCase("2")) {
                    // Buyer's Registration
                    ConstantUtil.isfromMainActivity = true;
                    Intent i = new Intent(MainActivity.this, BuyerRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList, deviceBean);
                    startActivity(i);
                    finish();
                } else if (dataFieldFirst.equalsIgnoreCase("3")) {
                    // Client Registration
                    ConstantUtil.isfromMainActivity = true;
                    Intent i = new Intent(MainActivity.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList, deviceBean);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);
                    finish();
                }
            }
        } else if (cmd.equals(CommandUtil.cmd_get_device_Temperature) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.getInstance().showDeviceInfo(MainActivity.this, JsonUtil.getJsonDataByField("01", data), JsonUtil.getJsonDataByField("01", data), new AquasmartDialogListener() {


                        @Override
                        public void onButtonOneClick() {

                        }

                        @Override
                        public void onButtonTwoClick() {

                        }

                        @Override
                        public void onButtonThreeClick(String password) {

                        }
                    });
                }
            });

        }
    }

    @Override
    public void onPacketReceived(String packets) {

        String cmd = JsonUtil.getJsonDataByField("cmd", packets);                              //Ap mode
        final String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_power_status)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  /*  if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }*/

                    //  Util.hideLoader();
                    isPacketReceived = true;
                    if (data.equalsIgnoreCase("0")) {
                        for (DeviceBean d : arrayList) {
                            d.switchs = true;
                            d.source = "on";
                        }
//                        deviceRecyclerAdapter = new PlugSmartDeviceAdapter(MainActivity.this, arrayList);
//                        deviceRecyclerAdapter.notifyDataSetChanged();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                rv_device_list.setAdapter(deviceRecyclerAdapter);
//                            }
//                        });
                    }
                    if (data.equalsIgnoreCase("1")) {
                        for (DeviceBean d : arrayList) {
                            d.switchs = false;
                            d.source = "off";
                        }
//                        deviceRecyclerAdapter = new PlugSmartDeviceAdapter(MainActivity.this, arrayList);
//                        deviceRecyclerAdapter.notifyDataSetChanged();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                rv_device_list.setAdapter(deviceRecyclerAdapter);
//                            }
//                        });
                    }
                }
            });

        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_power_status)) {                        //broadcast Ap mode
            final String name = JsonUtil.getJsonDataByField("01", data);

            if (MyWifiReceiver.checkbtn_press == false) {                                       //shubham
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  /*  if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }*/

                        //  Util.hideLoader();
                        isPacketReceived = true;
                        if (isFromAddGadget) {
                            arrayList = new ArrayList<>();
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(name);
                            if (!Logger.isPlugSmart) {
                                String status = JsonUtil.getJsonDataByField("03", data);
                                if (status.equalsIgnoreCase("1")) {                         //change
                                    deviceBean.setSwitchs(false);
                                    deviceBean.setSource("off");
                                } else {
                                    deviceBean.setSwitchs(true);
                                    deviceBean.setSource("on");
                                }
                            } else {
                                String status = JsonUtil.getJsonDataByField("02", data);
                                if (status.equalsIgnoreCase("1")) {                        //change
                                    deviceBean.setSwitchs(false);
                                    deviceBean.setSource("off");
                                } else {
                                    deviceBean.setSwitchs(true);
                                    deviceBean.setSource("on");
                                }
                            }
                            arrayList.add(deviceBean);
                            showListForSettings();
                        } else {
                            if (!Logger.isPlugSmart) {
                                String status = JsonUtil.getJsonDataByField("03", data);
                                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                                editor.putString(ConstantUtil.gadgetPos, JsonUtil.getJsonDataByField("02", data));
                                editor.commit();
                                icontype = JsonUtil.getJsonDataByField("02", data);
                                showListForHome(name, status);
                            } else {
                                String status = JsonUtil.getJsonDataByField("02", data);
                                showListForHome(name, status);
                            }
                        }
                    }
                });
            } else {
                MyWifiReceiver.checkbtn_press = false;
            }


        } else if (cmd.equals(CommandUtil.authentication)) {
            String dataFieldFirst = JsonUtil.getJsonDataByField("01", data);
            String modeOFIotDevice = JsonUtil.getJsonDataByField("02", data);

            if (modeOFIotDevice.equalsIgnoreCase("1")) {
                if (dataFieldFirst.equalsIgnoreCase("1")) {
                    ConstantUtil.isfromMainActivity = true;
                    // Client Registration
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, ClientRegistrationActivity.class);
                            i.putExtra(ConstantUtil.stationList, deviceBean);
                            i.putExtra(ConstantUtil.ID, "1");
                            startActivity(i);
                            finish();
                        }
                    });

                } else if (dataFieldFirst.equalsIgnoreCase("2")) {
                    // Buyer's Registration
                    ConstantUtil.isfromMainActivity = true;
                    Intent i = new Intent(MainActivity.this, BuyerRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList, deviceBean);
                    startActivity(i);
                    finish();
                } else if (dataFieldFirst.equalsIgnoreCase("3")) {
                    // Client Registration
                    ConstantUtil.isfromMainActivity = true;
                    Intent i = new Intent(MainActivity.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.stationList, deviceBean);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);
                    finish();
                }
            }
        } else if (cmd.equals(CommandUtil.cmd_get_device_Temperature) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.getInstance().showDeviceInfo(MainActivity.this, JsonUtil.getJsonDataByField("01", data), JsonUtil.getJsonDataByField("01", data), new AquasmartDialogListener() {


                        @Override
                        public void onButtonOneClick() {

                        }

                        @Override
                        public void onButtonTwoClick() {

                        }

                        @Override
                        public void onButtonThreeClick(String password) {

                        }
                    });
                }
            });

        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_set_time_sync)) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            MyWifiReceiver.write(PacketsUrl.getPowerStatus(firmwareSharedPref.getString(ConstantUtil.gadgetName, "")), CommandUtil.cmd_get_power_status);
        }

    }

    String icontype = "";

    private void showListForHome(String name, String status) {

        iv_home.setBackground(getResources().getDrawable(R.drawable.home_blue));
        iv_setting.setBackground(getResources().getDrawable(R.drawable.setting_black));
        iv_schedule.setBackground(getResources().getDrawable(R.drawable.schudele_black));
        tv_home.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_schedule.setTextColor(getResources().getColor(R.color.grey));
        tv_setting.setTextColor(getResources().getColor(R.color.grey));
        if (ConstantUtil.isForAPModeOnly) {
            arrayList = new ArrayList<>();
            DeviceBean deviceBean = new DeviceBean();
            deviceBean.setName(name);
            deviceBean.iconType = icontype;
            if (status.equalsIgnoreCase("1")) {                                           //change
                deviceBean.setSwitchs(false);
                deviceBean.setSource("off");
            } else {
                deviceBean.setSwitchs(true);
                deviceBean.setSource("on");
            }
            arrayList.add(deviceBean);
        } else {
            for (DeviceBean deviceBean : arrayList) {
                if (deviceBean.name.equalsIgnoreCase(name)) {
                    if (status.equalsIgnoreCase("1")) {                                //change
                        deviceBean.setSwitchs(false);
                        deviceBean.setSource("off");
                    } else {
                        deviceBean.setSwitchs(true);
                        deviceBean.setSource("on");
                    }
                }
            }
        }
//        if (set_0777 != 1) {
        deviceRecyclerAdapter = new PlugSmartDeviceAdapter(getApplicationContext(), arrayList);
        rv_device_list.setAdapter(deviceRecyclerAdapter);
    }
    //}

    @Override
    public void showTimer(String name) {

        CustomDialog.getInstance().showDialogTimer(MainActivity.this, new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {

            }

            @Override
            public void onButtonTwoClick() {

            }

            @Override
            public void onButtonThreeClick(String password) {

            }
        });
    }

    @Override
    public void findAuthenticity(final DeviceBean deviceBean) {
        this.deviceBean = deviceBean;

        if (deviceBean.status.equalsIgnoreCase(getResources().getString(R.string.colorDeviceNotReg))) {
            if (Logger.isPlugSmart) {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Please register with " + deviceBean.name + " by changing the network settings or by pressing the reset button of device for 10 seconds . ", "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        //   showListForHome();

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            } else {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Please do registeration with " + deviceBean.name, "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        // Util.sendPacketStationMode(deviceBean, PacketsUrl.deviceAuthentication(MainActivity.this), MainActivity.this);

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            }
            return;
        }

        //////////////////////////DEVICE CONNECTED ON EDGE/////////////////////////////
        if (deviceBean.status.equalsIgnoreCase(getResources().getString(R.string.colorDevice_On_edge))) {

            String IP = "";
            String newip = deviceBean.ip;
            String[] array = newip.split("\\*");
            for (String s : array)
                IP = IP + s;


            if (Logger.isPlugSmart) {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Your Device " + deviceBean.name + " is Connected on Edge Having IP. " + IP + " Please loggin on web.", "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        //   showListForHome();

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            } else {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Please do registeration with " + deviceBean.name, "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        // Util.sendPacketStationMode(deviceBean, PacketsUrl.deviceAuthentication(MainActivity.this), MainActivity.this);

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            }
            return;
        }
        //////////////////////////////////////////////////////////////////////////////////


        /////////DEVICE  Is Supposed to connect on edge but not connected on edge/////////
        if (deviceBean.status.equalsIgnoreCase(getResources().getString(R.string.colorDevice_On_edge_notConnected))) {

            String IPs = "";
            String newip = deviceBean.ip;
            String[] array = newip.split("\\#");
            for (String s : array)
                IPs = IPs + s;


            if (Logger.isPlugSmart) {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Your Device " + deviceBean.name + " is not able to Connect on Edge Having IP. " + IPs + " Please do check your Edge working or not .", "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        //   showListForHome();

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            } else {
                CustomDialog.getInstance().showDialogOk(this, "Alert!!!", "Please do registeration with " + deviceBean.name, "OK", new AquasmartDialogListener() {


                    @Override
                    public void onButtonOneClick() {
                        // Util.sendPacketStationMode(deviceBean, PacketsUrl.deviceAuthentication(MainActivity.this), MainActivity.this);

                    }

                    @Override
                    public void onButtonTwoClick() {

                    }

                    @Override
                    public void onButtonThreeClick(String password) {

                    }
                });

            }
            return;
        }
        //////////////////////////////////////////////////////////////////////////////////

    }

    @Override
    public void deviceInfo(DeviceBean deviceBean) {
        if (ConstantUtil.isForAPModeOnly) {
            String packet = PacketsUrl.getTemperature();
            sendReqCmd = CommandUtil.cmd_get_device_Temperature;
            MyWifiReceiver.write(packet, CommandUtil.cmd_get_device_Temperature);
        } else {
            String packet = PacketsUrl.getTemperature();
            sendReqCmd = CommandUtil.cmd_get_device_Temperature;
            Util.sendPacketStationMode(deviceBean, packet, MainActivity.this);
        }

    }

    private boolean isForOnOff = false;

    @Override
    public void plugState(boolean b, DeviceBean deviceBean) {
        if (ConstantUtil.isForAPModeOnly) {
            if (b) {
                MyWifiReceiver.write(PacketsUrl.setPowerStatus("0"), CommandUtil.cmd_set_power_status);
                isForOnOff = true;
                timerResponseDialog();
            } else {
                MyWifiReceiver.write(PacketsUrl.setPowerStatus("1"), CommandUtil.cmd_set_power_status);
                isForOnOff = true;
                timerResponseDialog();
            }
        } else {
            sendReqCmd2 = "7777";
            if (b) {
                String packet = PacketsUrl.setPowerStatus("0");
                Util.sendPacketStationMode(deviceBean, packet, this);
                isForOnOff = true;
                timerResponseDialog();
            } else {
                String packet = PacketsUrl.setPowerStatus("1");
                Util.sendPacketStationMode(deviceBean, packet, this);
                isForOnOff = true;
                timerResponseDialog();
            }
        }
    }
}
