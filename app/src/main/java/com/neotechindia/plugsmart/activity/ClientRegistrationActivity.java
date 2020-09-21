package com.neotechindia.plugsmart.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.LocationListener;
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

import static android.location.LocationManager.GPS_PROVIDER;


public class ClientRegistrationActivity extends AppCompatActivity implements IUpdateUiListener {
    private View mRoot;
    private String name, mob;
    private String nextScreenRequest, serialNumber, licenseNumber;
    private boolean isValidationClient = false;
    private static String sendReqCmd = "";
    private RelativeLayout baseLayout;
    private CountDownTimer countDownTimer;
    private boolean isFromButtonSubmit = false;
    private Toolbar toolbar;
    private EditText et_serialno_client_registration, et_license_client_registration, et_name_client_registration;
    private Button submit_button;
    private DeviceBean deviceBean;
    private TextView tv_forgot_password;
    public static double clientlatitude;
    public static double clientlongitude;
    private LocationManager lm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);
        deviceBean = new DeviceBean();
        ConstantUtil.updateListener = this;
        Bundle args = getIntent().getExtras();
        nextScreenRequest = args.getString(ConstantUtil.ID);
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        initViews();
        viewsListeners();
        setValues();
        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = lm.getLastKnownLocation(GPS_PROVIDER);
            double clientlongitude = location.getLongitude();
            double clientlatitude = location.getLatitude();

            Log.d("long", "onLocationChanged: " + clientlongitude);
            Log.d("lat", "onLocationChanged: " + clientlatitude);

            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.clientlong, String.valueOf(clientlongitude));
            editor.putString(ConstantUtil.clientlang, String.valueOf(clientlatitude));
            editor.commit();
            lm.requestLocationUpdates(GPS_PROVIDER, 2000, 10, (android.location.LocationListener) locationListener);
        } catch (Exception e) {

            Log.d("exception", "onCreate: ",e);
        }

    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            clientlongitude = location.getLongitude();
            clientlatitude = location.getLatitude();


            Log.d("long", "onLocationChanged: " + clientlongitude);
            Log.d("lat", "onLocationChanged: " + clientlatitude);
        }
    };

    private void setValues() {
        if (ConstantUtil.isfromMainActivity) {
            Util.sendPacketStationMode(deviceBean, PacketsUrl.getSerialLiscenseNumber(), ClientRegistrationActivity.this);

        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            et_serialno_client_registration.setText(sharedPreferences.getString(ConstantUtil.serialNumber, ""));
            if (sharedPreferences.getString(ConstantUtil.liscenceNumber, "") != "") {
                et_license_client_registration.setText(ConstantUtil.getAutoFillStrings(sharedPreferences.getString(ConstantUtil.liscenceNumber, "").substring(sharedPreferences.getString(ConstantUtil.liscenceNumber, "").length() - 4), 20, "X"));
            }
            if (nextScreenRequest.equalsIgnoreCase("2")) {
                et_name_client_registration.setText(sharedPreferences.getString(ConstantUtil.buyerphone, ""));
            }
        }

    }

    private void viewsListeners() {
        et_name_client_registration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_name_client_registration.getText().length() < 3) {
                        Toast.makeText(ClientRegistrationActivity.this, "Invalid Name minimum 3 char", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromButtonSubmit = true;
                showAlertConfirmationWithTwoButton("Enter Admin Password");

            }
        });
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClientRegistrationActivity.this, ForgotPasswordActivity.class);
                i.putExtra(ConstantUtil.stationList, deviceBean);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_client_registration));
        setSupportActionBar(toolbar);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        baseLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        et_serialno_client_registration = findViewById(R.id.et_serialno_client_registration);
        et_license_client_registration = findViewById(R.id.et_license_client_registration);
        et_name_client_registration = findViewById(R.id.et_name_client_registration);
        submit_button = findViewById(R.id.submit_button);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPacketReceived(final String packets, String mac) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNextScreen(packets);
            }
        });
    }

    @Override
    public void onPacketReceived(final String packets) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNextScreen(packets);
            }
        });
    }

    private void showNextScreen(final String packets) {

        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.client_registration) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = firmwareSharedPref.edit();
                        editor.putString(ConstantUtil.clientName, name);
                        editor.putString(ConstantUtil.clientPhone, mob);
                        editor.putString(ConstantUtil.clientRegistrationTime, ConstantUtil.getDateByFormat("yyyy-MM-dd HH:mm:ss"));
                        editor.commit();

                        Toast.makeText(ClientRegistrationActivity.this, "Client Registered Successfully", Toast.LENGTH_SHORT).show();
                        if (firmwareSharedPref.getString(ConstantUtil.gadgetName, "").equals("")) {
                            Intent i = new Intent(ClientRegistrationActivity.this, AddGadgetActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            ConstantUtil.isForAPModeOnly = true;
                            Intent i = new Intent(ClientRegistrationActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
            } else if (data.equalsIgnoreCase("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NomoreClientPopup(getString(R.string.NoMoreClientCanRegister));

                    }
                });

            } else if (data.equalsIgnoreCase("2")) {
                Toast.makeText(ClientRegistrationActivity.this, "User name already exist", Toast.LENGTH_LONG).show();
            }
        } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_password_veryfication) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equalsIgnoreCase("0")) {
                        Util.hideKeyboard(ClientRegistrationActivity.this);
                        if (isFromButtonSubmit) {
                            isValidationClient = false;
                            name = et_name_client_registration.getText().toString().trim();
                            //  mob = ((EditText) mRoot.findViewById(R.id.et_mobile_buyer_registration)).getText().toString().trim();
                            if ((name.length() < 3)) {
                                if (name.length() == 0) {
                                    CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Mobile Number or Name");
                                    isValidationClient = true;
                                } else {
                                    CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid Mobile Number or Name minimum 3 char");
                                    isValidationClient = true;
                                }
                            }
                            if (isValidationClient == false) {
                                sendReqCmd = CommandUtil.client_registration;
                                if (ConstantUtil.isfromMainActivity) {
                                    Util.sendPacketStationMode(deviceBean, PacketsUrl.clientRegistration(name, name, nextScreenRequest, ClientRegistrationActivity.this), ClientRegistrationActivity.this);
                                } else {
                                    MyWifiReceiver.write(PacketsUrl.clientRegistration(name, name, nextScreenRequest, ClientRegistrationActivity.this), sendReqCmd);
                                }
                                timerResponseDialog();
                            }
                        } else {
                            Intent clientRegIntent = new Intent(ClientRegistrationActivity.this, ViewRegistrationActivity.class);
                            clientRegIntent.putExtra("key", ConstantUtil.ClientRegKey);
                            clientRegIntent.putExtra(ConstantUtil.stationList, deviceBean);
                            startActivity(clientRegIntent);
                        }

                    } else {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid Password");
                        Toast.makeText(ClientRegistrationActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        showAlertConfirmationWithTwoButton("Enter Admin Password");
                    }
                }
            });
        } else if (cmd.equals(CommandUtil.cmd_get_serialliscence_number) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd="";
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.serialNumber, JsonUtil.getJsonDataByField("02", data));
            editor.putString(ConstantUtil.liscenceNumber, JsonUtil.getJsonDataByField("01", data));
            et_serialno_client_registration.setText(JsonUtil.getJsonDataByField("02", data));
            et_license_client_registration.setText(JsonUtil.getJsonDataByField("01", data));

            String mac_Id = JsonUtil.getJsonDataByField("03", data);
            String srno = JsonUtil.getJsonDataByField("02", data);
            String licno=JsonUtil.getJsonDataByField("01",data);
//
//            ConstantUtil.macAndLicenseNoMap.put(mac_Id, licno);
//            ConstantUtil.macAndSerialNoMap.put(mac_Id, srno);
//
//            Mac_Id = JsonUtil.getJsonDataByField("03", data);
//            srno = JsonUtil.getJsonDataByField("02", data);
//            licno=JsonUtil.getJsonDataByField("01",data);

            if(!TextUtils.isEmpty(mac_Id) && !TextUtils.isEmpty(licno) && !TextUtils.isEmpty(srno)){
                ConstantUtil.macAndLicenseNoMap.put(mac_Id, licno);
                ConstantUtil.macAndSerialNoMap.put(mac_Id, srno);

                SharedPreferences getserialpref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = getserialpref .edit();
                editor1.putString(mac_Id,licno+"_"+srno);
                editor1.commit();

            }

            if (nextScreenRequest.equalsIgnoreCase("2")) {
                et_name_client_registration.setText(firmwareSharedPref.getString(ConstantUtil.buyerphone, ""));
            }
            editor.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    sendReqCmd = CommandUtil.client_registration;
                    if (ConstantUtil.isfromMainActivity) {
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.clientRegistration(name, mob, nextScreenRequest, ClientRegistrationActivity.this), ClientRegistrationActivity.this);

                    } else {
                        MyWifiReceiver.write(PacketsUrl.clientRegistration(name, mob, nextScreenRequest, ClientRegistrationActivity.this), sendReqCmd);
                    }
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
        String msg2 = getString(R.string.allowPermission);
        CustomDialog.getInstance().showDialogOk(ClientRegistrationActivity.this, msg, msg2, "Ok", new AquasmartDialogListener() {
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

    private void NomoreClientPopup(String msg) {
        String msg2 = getString(R.string.DoyouWantToDeleteRegisterDevice);
        isFromButtonSubmit = false;
        CustomDialog.getInstance().showYesNoAlert(ClientRegistrationActivity.this, msg, msg2, "Yes", "No", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {
                showAlertConfirmationWithTwoButton(getString(R.string.EnterAdminPassword));
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

    private void showAlertConfirmationWithTwoButton(String msg) {
        // get prompts.xml view
        try {
            CustomDialog.getInstance().showDialogEditText(ClientRegistrationActivity.this, msg, msg, "Cancel", "Ok", new AquasmartDialogListener() {
                @Override
                public void onButtonOneClick() {
                    sendReqCmd = CommandUtil.cmd_password_veryfication;
                }

                @Override
                public void onButtonTwoClick() {

                }

                @Override
                public void onButtonThreeClick(String password) {

                }
            }, deviceBean);
        } catch (Exception e) {
            Log.d("", " alertDialog  " + e.toString());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Util.hideLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void timerResponseDialog() {
        Util.showLoader(ClientRegistrationActivity.this);
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
                        Toast.makeText(ClientRegistrationActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                    }
                });
                countDownTimer = null;
            }
        }.start();
    }
}
