package com.neotechindia.plugsmart.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.LicenceType;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


public class NetworkSettingsActivity extends AppCompatActivity implements IUpdateUiListener, View.OnClickListener {

    RelativeLayout baseLayout, rl_network_setting_wifi_list;
    FrameLayout ll_network_setting_hostname;
    EditText eTHostName, eTHostNamePassword, enterIp;
    Button btnSubmit, btn_refresh_wifi_list, btnSkip;
    RadioGroup rGConnectionMode;
    Spinner spin_wifi_list;
    ImageView iv_refresh_wifi_list;
    int connectivityMode, edgeStation_status, position, positionOfApModeSsid = 0, positionOfStationModeSsid = 0;
    WifiManager wifi;
    WifiScanReceiver wifiReciever;
    RotateAnimation anim;
    Toolbar toolbar;
    private CountDownTimer countDownTimer;
    public static String sendReqCmd = "";
    ArrayList<String> arraylist = new ArrayList<String>();
    String wifiSelected, ipfromclient;
    boolean isButtonClicked = false;
    private long REQUEST_TIME_OUT = 6000;
    DeviceBean deviceBean;
    private boolean isValidation = false;
    private String AP_LICENCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstantUtil.updateListener = this;
        ConstantUtil.isStopCalledUsbReceiver = false;
        setContentView(R.layout.activity_network_settings);
        deviceBean = new DeviceBean();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.network_setting);
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        baseLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        getScanResults();
        init();
        rotationAnimation();
        addListeners();
        setValuesForMode();
        timerResponseDialog();
        AP_LICENCE = getIntent().getStringExtra(ConstantUtil.liscenceNumber);
    }

    private void getNetworkSettingConfiguration() {
        MyWifiReceiver.write(PacketsUrl.getNetworkSettingsConfiguration(), CommandUtil.cmd_get_network_setting_configuration);
        sendReqCmd = CommandUtil.cmd_get_network_setting_configuration;
    }

    private void setValuesForMode() {
        // Visibility of Radio Button and EditText according to currently connected Network mode//
        //// TODO: 3/20/2017  first check which mode is activated then change accordingly
        if (ConstantUtil.CONNECTED_NETWORK_MODE == ConstantUtil.INetworkMode.CONNECTED_NETWORK_AP_MODE) {
            AppCompatRadioButton rBApMode = (AppCompatRadioButton) findViewById(R.id.rBApMode);
            rBApMode.setChecked(true);
            if (rBApMode.getText().toString().contains(getString(R.string.label_network_activated))) {

            } else {

                rBApMode.setText(rBApMode.getText() + "   " + getString(R.string.label_network_activated));
            }
            eTHostName.setEnabled(false);
            eTHostName.setVisibility(View.VISIBLE);
            eTHostNamePassword.setHint("");
            spin_wifi_list.setSelection(positionOfApModeSsid);
            spin_wifi_list.setEnabled(false);
            connectivityMode = ConstantUtil.CONNECTED_NETWORK_MODE;
            iv_refresh_wifi_list.setEnabled(false);
            iv_refresh_wifi_list.setVisibility(View.INVISIBLE);
            ll_network_setting_hostname.setVisibility(View.VISIBLE);
          /*  SharedPreferences firmwareSharedPref = getSharedPreferences("Aqua_Logs", Context.MODE_PRIVATE);
            ConstantUtil.SSID = firmwareSharedPref.getString("ApModessid", "SSID");*/
            eTHostName.setText(ConstantUtil.SSID);
            eTHostNamePassword.setText(ConstantUtil.IBuildConstant.AP_MODE_SSID_PASSWORD);
            eTHostNamePassword.setEnabled(false);
        } else if (ConstantUtil.CONNECTED_NETWORK_MODE == ConstantUtil.INetworkMode.CONNECTED_NETWORK_STATION_MODE) {
            AppCompatRadioButton rBStationMode = (AppCompatRadioButton) findViewById(R.id.rBStationMode);
            rBStationMode.setChecked(true);
            SharedPreferences networkSettingPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            if (rBStationMode.getText().toString().contains(getString(R.string.label_network_activated))) {

            } else {

                rBStationMode.setText(rBStationMode.getText() + "   " + getString(R.string.label_network_activated));
            }
            eTHostName.setText(networkSettingPref.getString(ConstantUtil.stationModeSsid, ""));
            eTHostName.setEnabled(true);
            // eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
            spin_wifi_list.setSelection(positionOfStationModeSsid);
            spin_wifi_list.setEnabled(true);
            eTHostName.setVisibility(View.VISIBLE);
            connectivityMode = ConstantUtil.CONNECTED_NETWORK_MODE;
            iv_refresh_wifi_list.setEnabled(true);
            iv_refresh_wifi_list.setVisibility(View.VISIBLE);
            ll_network_setting_hostname.setVisibility(View.VISIBLE);
            eTHostNamePassword.setText(networkSettingPref.getString(ConstantUtil.stationModePassword, ""));
            eTHostNamePassword.setEnabled(true);
        } else if (ConstantUtil.CONNECTED_NETWORK_MODE == ConstantUtil.INetworkMode.CONNECTED_NETWORK_STATION_MODE) {
            AppCompatRadioButton edgeStationMode = (AppCompatRadioButton) findViewById(R.id.edgeStationMode);
            edgeStationMode.setChecked(true);
            SharedPreferences networkSettingPref1 = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            if (edgeStationMode.getText().toString().contains(getString(R.string.label_network_activated))) {

            } else {

                edgeStationMode.setText(edgeStationMode.getText() + "   " + getString(R.string.label_network_activated));
            }
            eTHostName.setText(networkSettingPref1.getString(ConstantUtil.stationModeSsid, ""));
            eTHostName.setEnabled(true);
            // eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
            spin_wifi_list.setSelection(positionOfStationModeSsid);
            spin_wifi_list.setEnabled(true);
            eTHostName.setVisibility(View.VISIBLE);
            connectivityMode = ConstantUtil.CONNECTED_NETWORK_MODE;
            iv_refresh_wifi_list.setEnabled(true);
            iv_refresh_wifi_list.setVisibility(View.VISIBLE);
            ll_network_setting_hostname.setVisibility(View.VISIBLE);
            eTHostNamePassword.setText(networkSettingPref1.getString(ConstantUtil.stationModePassword, ""));
            eTHostNamePassword.setEnabled(true);
        }


    }

    private void rotationAnimation() {
        anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
    }

    private void getScanResults() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);


            }
        } else {
            if (Build.VERSION.SDK_INT > 22) {
                displayLocationSettingsRequest();
            } else {
                scanWifi();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    scanWifi();

                } else {
                    allowPermission(getString(R.string.alert));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void allowPermission(String msg) {
        String msg2 = getString(R.string.allowLocationPermission);
        CustomDialog.getInstance().showDialogOk(this, msg, msg2, "Ok", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {
                finish();

            }

            @Override
            public void onButtonTwoClick() {


            }

            @Override
            public void onButtonThreeClick(String password) {

            }
        });
    }

    private HashMap<String,Boolean> NetworkCapabilties = new HashMap<>();
    private void init() {
        enterIp = (EditText) findViewById(R.id.enterIp);
        enterIp.setVisibility(View.INVISIBLE);
        eTHostName = (EditText) findViewById(R.id.eTHostName);
        btnSkip = findViewById(R.id.btnSkip);
        eTHostNamePassword = (EditText) findViewById(R.id.eTHostNamePassword);
        iv_refresh_wifi_list = (ImageView) findViewById(R.id.iv_refresh_wifi_list);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rGConnectionMode = (RadioGroup) findViewById(R.id.rGConnectionMode);
        ll_network_setting_hostname = (FrameLayout) findViewById(R.id.ll_network_setting_hostname);
        spin_wifi_list = (Spinner) findViewById(R.id.spin_wifi_list);
        // spin_wifi_list= (AutoCompleteTextView) findViewById(R.id.spin_wifi_list);
        spin_wifi_list.setPopupBackgroundResource(R.color.spinnerDropdown);
        if (ConstantUtil.isForAPModeOnly) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            AP_LICENCE = firmwareSharedPref.getString(ConstantUtil.licence_type,"");
            if(AP_LICENCE!=null){
                NetworkCapabilties = LicenceType.getNetworkSetting(AP_LICENCE);
                RadioButton edge = findViewById(R.id.edgeStationMode);
                RadioButton station = findViewById(R.id.rBStationMode);
                station.setEnabled(NetworkCapabilties.get("station"));
                edge.setEnabled(NetworkCapabilties.get("edge"));
            }
            if (ConstantUtil.isfromRegistration) {
                btnSkip.setVisibility(View.VISIBLE);

            } else {
                btnSkip.setVisibility(View.GONE);
            }
        } else {
            btnSkip.setVisibility(View.GONE);
            if(deviceBean!= null ) {
                if(deviceBean.LicenceType!= null) {
                    AP_LICENCE = deviceBean.LicenceType;
                    NetworkCapabilties = LicenceType.getNetworkSetting(AP_LICENCE);
                    RadioButton edge = findViewById(R.id.edgeStationMode);
                    RadioButton station = findViewById(R.id.rBStationMode);
                    edge.setEnabled(NetworkCapabilties.get("edge"));
                    station.setEnabled(NetworkCapabilties.get("station"));
                }else{
                    Toast.makeText(this, "type is null", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "bean is nulll", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addListeners() {
        iv_refresh_wifi_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_refresh_wifi_list.startAnimation(anim);
                getScanResults();
            }
        });

        spin_wifi_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wifiSelected = adapterView.getItemAtPosition(i).toString();
                position = i;
                if (view != null) {
                    ((TextView) view).setText(null);
                }
                if (connectivityMode == ConstantUtil.CONNECTED_NETWORK_STATION_MODE) {
                    eTHostName.setText(wifiSelected);
                } else {
                    eTHostName.setText(ConstantUtil.SSID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NetworkSettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnSubmit.setOnClickListener(this);
        rGConnectionMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rBApMode:
                        enterIp.setVisibility(View.INVISIBLE);
                        connectivityMode = ConstantUtil.CONNECTED_NETWORK_AP_MODE;
                        spin_wifi_list.setSelection(positionOfApModeSsid);
                        spin_wifi_list.setEnabled(false);
                        iv_refresh_wifi_list.setEnabled(false);
                        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                        ConstantUtil.AP_MODE_SSID = firmwareSharedPref.getString(ConstantUtil.AP_MODE_SSID, "PlugSmart");
                        eTHostName.setText(ConstantUtil.AP_MODE_SSID);
                        eTHostName.setEnabled(false);
                        eTHostNamePassword.setHint("");
                        eTHostName.setVisibility(View.VISIBLE);
                        eTHostNamePassword.setText(ConstantUtil.IBuildConstant.AP_MODE_SSID_PASSWORD);
                        eTHostNamePassword.setEnabled(false);
                        iv_refresh_wifi_list.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rBStationMode:
                        enterIp.setVisibility(View.INVISIBLE);
                        connectivityMode = ConstantUtil.CONNECTED_NETWORK_STATION_MODE;
                        edgeStation_status = ConstantUtil.CONNECTED_NETWORK_Station_StationMOde;
                        SharedPreferences networkSettingPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                        iv_refresh_wifi_list.setEnabled(true);
                        iv_refresh_wifi_list.setVisibility(View.VISIBLE);
                        spin_wifi_list.setSelection(positionOfStationModeSsid);
                        spin_wifi_list.setEnabled(true);
                        if (networkSettingPref.getString(ConstantUtil.stationModeSsid, "").length() > 0) {
                            spin_wifi_list.setSelection(position);
                            eTHostName.setText(networkSettingPref.getString(ConstantUtil.stationModeSsid, ""));
                            eTHostName.setEnabled(true);
                            eTHostName.setVisibility(View.VISIBLE);
                            //  eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setText(networkSettingPref.getString(ConstantUtil.stationModePassword, ""));
                            eTHostNamePassword.setEnabled(true);


                        } else {
                            eTHostName.setText("");
                            //  eTHostName.setHint(getString(R.string.hint_title_enter_host_name));
                            eTHostName.setEnabled(true);
                            //  eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setText("");
                            // eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setEnabled(true);
                        }
                        break;


                    case R.id.edgeStationMode:
                        if(!NetworkCapabilties.get("edge")){
                            Toast.makeText(NetworkSettingsActivity.this, "Edge Unavailable", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        isValidation = false;
                        Toast.makeText(NetworkSettingsActivity.this, "Enter Your IP", Toast.LENGTH_SHORT).show();
                        enterIp.setVisibility(View.VISIBLE);

//                        enterIp.addTextChangedListener(loginTextWatcher);

                        edgeStation_status = ConstantUtil.CONNECTED_NETWORK_Station_EdgeMode;
                        connectivityMode = ConstantUtil.CONNECTED_NETWORK_STATION_MODE;
                        SharedPreferences networkSettingPref1 = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                        iv_refresh_wifi_list.setEnabled(true);
                        iv_refresh_wifi_list.setVisibility(View.VISIBLE);
                        spin_wifi_list.setSelection(positionOfStationModeSsid);
                        spin_wifi_list.setEnabled(true);
                        if (networkSettingPref1.getString(ConstantUtil.stationModeSsid, "").length() > 0) {
                            spin_wifi_list.setSelection(position);
                            eTHostName.setText(networkSettingPref1.getString(ConstantUtil.stationModeSsid, ""));
                            eTHostName.setEnabled(true);
                            eTHostName.setVisibility(View.VISIBLE);
                            //  eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setText(networkSettingPref1.getString(ConstantUtil.stationModePassword, ""));
                            eTHostNamePassword.setEnabled(true);


                        } else {
                            eTHostName.setText("");
                            //  eTHostName.setHint(getString(R.string.hint_title_enter_host_name));
                            eTHostName.setEnabled(true);
                            //  eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setText("");
                            // eTHostNamePassword.setHint(getString(R.string.hint_title_enter_password));
                            eTHostNamePassword.setEnabled(true);
                        }

                        if (TextUtils.isEmpty(enterIp.getText())) {
                            enterIp.setError("Enter Your IP..");
                            enterIp.requestFocus();

                        }


                        break;


                }
            }
        });


//        if (TextUtils.isEmpty(enterIp.getText())) {
//            enterIp.setError("Enter Your IP..");
//            enterIp.requestFocus();
//
//        }


    }
//
//    private TextWatcher loginTextWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            btnSubmit.setEnabled(false);
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            String userIP_Input = enterIp.getText().toString();
//
//            btnSubmit.setEnabled(!userIP_Input.isEmpty());
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {


        String packets, gadgetName;
        SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        gadgetName = sharedPreferences.getString(ConstantUtil.gadgetName, "");
        isButtonClicked = true;
        if (!(eTHostNamePassword.getText().toString().length() > 0)) {
            Toast.makeText(this, getString(R.string.error_msg_enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (connectivityMode == 0) {
            Toast.makeText(this, getString(R.string.error_msg_select_mode), Toast.LENGTH_SHORT).show();
            return;
        }
        ipfromclient = enterIp.getText().toString();
        packets = PacketsUrl.setNetWorkSettingPacket(eTHostName.getText().toString(), eTHostNamePassword.getText().toString(), String.valueOf(connectivityMode), ipfromclient, String.valueOf(edgeStation_status));
        sendReqCmd = CommandUtil.COMMAND_SAVE_NETWORK_SETTING;
        SharedPreferences sharedPreferences1 = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString(ConstantUtil.stationModeSsid, eTHostName.getText().toString());
        editor.putString(ConstantUtil.stationModePassword, eTHostNamePassword.getText().toString().trim());
        editor.commit();
        if (ConstantUtil.isForAPModeOnly) {
            String cmd = Util.getJsonDataByField("cmd", packets);
            MyWifiReceiver.write(packets, cmd);
        } else {
            Util.sendPacketStationMode(deviceBean, packets, NetworkSettingsActivity.this);
        }
        timerResponseDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        //   Util.hideLoader();
        //  Util.removeInstanceLoader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.dismissDialog();
        Util.removeDialogInstance();
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

    @Override
    public void onPacketReceived(String packets, String mac) {
         /* Response: successful********************************
        {"sid":"9000","sad":"x","cmd":"8019","data":"0"}
        Response: failure
        {"sid":"9000","sad":"x","cmd":"8019","data":"1"}*/
        /****************************************************/
        String cmd = Util.getJsonDataByField("cmd", packets);
        String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.COMMAND_SAVE_NETWORK_SETTING) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            isButtonClicked = false;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            //  Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                ConstantUtil.stationModeSsid = "";
                // Todo Show the Dialog here
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.getInstance().showDialogOk(NetworkSettingsActivity.this, "Alert!!!", getString(R.string.dialog_title_network_setting_saved), "OK", new AquasmartDialogListener() {


                            @Override
                            public void onButtonOneClick() {
                                finishAffinity();
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

            } else if (data.equalsIgnoreCase("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NetworkSettingsActivity.this, getString(R.string.error_msg_unable_save_network_setting), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    @Override
    public void onPacketReceived(final String packets) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(packets);
            }
        });
    }


    void updateUI(String packets) {
       /* Response: successful********************************
        {"sid":"9000","sad":"x","cmd":"8019","data":"0"}
        Response: failure
        {"sid":"9000","sad":"x","cmd":"8019","data":"1"}*/
        /****************************************************/
        String cmd = Util.getJsonDataByField("cmd", packets);
        String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.COMMAND_SAVE_NETWORK_SETTING) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            isButtonClicked = false;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            // Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                ConstantUtil.stationModeSsid = "";
                // Todo Show the Dialog here
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.getInstance().showDialogOk(NetworkSettingsActivity.this, "Alert!!!", getString(R.string.dialog_title_network_setting_saved), "OK", new AquasmartDialogListener() {


                            @Override
                            public void onButtonOneClick() {
                                finishAffinity();
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

            } else if (data.equalsIgnoreCase("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NetworkSettingsActivity.this, getString(R.string.error_msg_unable_save_network_setting), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


    private void timerResponseDialog() {


        Util.showLoader(NetworkSettingsActivity.this);
        if (isButtonClicked) {
            REQUEST_TIME_OUT = 12000;
        } else {
            REQUEST_TIME_OUT = 1000;
        }
        countDownTimer = new CountDownTimer(REQUEST_TIME_OUT, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Util.hideLoader();
                        if (isButtonClicked) {
                            Toast.makeText(NetworkSettingsActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                            isButtonClicked = false;
                        } else {
                            //getNetworkSettingConfiguration();
                            setValuesForMode();
                        }


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }

    class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            SharedPreferences networkSettingPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            List<ScanResult> wifiScanList = wifi.getScanResults();

            arraylist = new ArrayList<String>();
            for (int i = 0; i < wifiScanList.size(); i++) {
                if (wifiScanList.get(i).SSID.contains("PlugSmart")) {
                    positionOfApModeSsid = i;
                }

                if (wifiScanList.get(i).SSID.equalsIgnoreCase(networkSettingPref.getString(ConstantUtil.stationModeSsid, "")) && !networkSettingPref.getString("staionModeSsid", "").equalsIgnoreCase("")) {
                    positionOfStationModeSsid = i;
                }

                arraylist.add(((wifiScanList.get(i).SSID)));
            }
            // arraylist.add(0, "---Select Wifi---");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NetworkSettingsActivity.this, android.R.layout.simple_spinner_item, arraylist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spin_wifi_list.setAdapter(adapter);

            iv_refresh_wifi_list.setAnimation(null);

            if (networkSettingPref.getString(ConstantUtil.stationModeSsid, "").length() > 0) {
                eTHostName.setText(networkSettingPref.getString(ConstantUtil.stationModeSsid, ""));
            }
            if (ConstantUtil.CONNECTED_NETWORK_MODE == ConstantUtil.INetworkMode.CONNECTED_NETWORK_AP_MODE) {
                spin_wifi_list.setSelection(positionOfApModeSsid);
            } else if (ConstantUtil.CONNECTED_NETWORK_MODE == ConstantUtil.INetworkMode.CONNECTED_NETWORK_STATION_MODE) {

                spin_wifi_list.setSelection(positionOfStationModeSsid);
            }

            if (wifiReciever != null) {
                try {
                    unregisterReceiver(wifiReciever);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void displayLocationSettingsRequest() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        scanWifi();


                        // startMainActivity();
                        //   Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(NetworkSettingsActivity.this, 111);
                        } catch (IntentSender.SendIntentException e) {
                            //    Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                    case LocationSettingsStatusCodes.CANCELED:
                        //   Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void scanWifi() {
        try {
            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiReciever = new WifiScanReceiver();
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == -1) {
                    if (connectivityMode == ConstantUtil.CONNECTED_NETWORK_STATION_MODE) {
                        scanWifi();
                    }
                    //startMainActivity();
                } else {
                    finish();
                }

                break;
            default:
                break;
        }


    }

}
