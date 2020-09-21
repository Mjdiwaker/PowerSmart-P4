package com.neotechindia.plugsmart.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.location.LocationListener;
import com.neotechindia.plugsmart.Data.DatabaseHandler;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.Contact;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import org.json.JSONObject;

import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;


public class BuyerRegistrationActivity extends AppCompatActivity implements IUpdateUiListener, View.OnClickListener {

    private View mRoot;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    String reg_json, name, email, pwd, mob;
    private boolean isValidation = false;
    private String regType;
    public static String sendReqCmd = "";
    private RelativeLayout baseLayout;
    private CountDownTimer countDownTimer;
    private EditText et_serialno, et_licenceno, et_pwd, et_phone, et_name, et_email;
    private Button submit_button;
    private Toolbar toolbar;
    private LocationManager lm;
    public static double buyerlati;
    public static double buyerlong;
    DatabaseHandler db = new DatabaseHandler(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buyer_registration);
        ConstantUtil.updateListener = this;
        baseLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        initViews();
        viwsListeners();
        setValues();


        Log.d("Reading:", "Reading all contacts... ");
        List<Contact> contactList=db.getBuyerInfomations();

        for (Contact c: contactList){
            String log="ID:" + +c.getId()+  ", NAME:"+c.getBuyername() +
                    " , Phone:" +c.getBuyerphone() +
                    " , Email:" +c.getBuyeremail() +
                    " , Adminpass:" +c.getAdminpass()
                    +  ", Latitude:"+c.getBuyerlat()
                    +  ", Longitude:"+c.getBuyerlong()
                    +  ", Type:"+c.getType()
                    +  ", Uuid:"+c.getUUID();
            Log.d("Name:" , log);
        }



        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = lm.getLastKnownLocation(GPS_PROVIDER);
             double buyerlong = location.getLongitude();
             double buyerlati = location.getLatitude();
             this.buyerlong = buyerlong;
             this.buyerlati = buyerlati;
            Log.d("long", "onLocationChanged: " + buyerlong);
            Log.d("lat", "onLocationChanged: " + buyerlati);

            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.buyerlong, String.valueOf(buyerlong));
            editor.putString(ConstantUtil.buyerlat, String.valueOf(buyerlati));
            editor.commit();
            lm.requestLocationUpdates(GPS_PROVIDER, 2000, 10, (android.location.LocationListener) locationListener);
        } catch (Exception e) {
            Log.d("exception", "onCreate: "+e);

        }


    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            buyerlong = location.getLongitude();
            buyerlati = location.getLatitude();


            Log.d("long", "onLocationChanged: " + buyerlong);
            Log.d("lat", "onLocationChanged: " + buyerlati);
        }
    };

    private void setValues() {
        SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        et_serialno.setText(sharedPreferences.getString(ConstantUtil.serialNumber, ""));
        et_licenceno.setText(sharedPreferences.getString(ConstantUtil.liscenceNumber, ""));
        et_phone.setText("+91");
        Selection.setSelection(et_phone.getText(), et_phone.getText().length());

    }

    private void viwsListeners() {
        submit_button.setOnClickListener(this);

        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91")) {
                    et_phone.setText("+91");
                    Selection.setSelection(et_phone.getText(), et_phone
                            .getText().length());

                }

            }

        });

        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_name.getText().length() < 3) {
                        Toast.makeText(BuyerRegistrationActivity.this, "Invalid Name minimum 3 char", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        et_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_phone.getText().length() < 13) {
                        Toast.makeText(BuyerRegistrationActivity.this, "Mobile no. should contain atleast 10 digits", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_buyer_registration));
        setSupportActionBar(toolbar);
        et_serialno = findViewById(R.id.et_serialno);
        et_licenceno = findViewById(R.id.et_licenceno);
        et_pwd = findViewById(R.id.et_pwd);
        et_phone = findViewById(R.id.et_phone);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        submit_button = findViewById(R.id.submit_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

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
    public void onPacketReceived(String packets, String mac) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.buyer_registration) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            JSONObject reader = null;
            try {
                reader = new JSONObject(data);
                String value = reader.getString("02");
                if (value.equalsIgnoreCase("0")) {  // Already registered user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = firmwareSharedPref.edit();
                            editor.putString(ConstantUtil.buyername, name);
                            editor.putString(ConstantUtil.buyerphone, mob);
                            editor.putString(ConstantUtil.buyeremail, email);
                            editor.putString(ConstantUtil.adminPass, pwd);
                            editor.commit();

                            String type="0";
                            String uuid = firmwareSharedPref.getString(ConstantUtil.uuid1, "");
                            Contact contact=new Contact(name,mob,email,pwd,String.valueOf(buyerlong),String.valueOf(buyerlati),uuid,type);
                            db.AddBuyerInfo(contact);

                            Toast.makeText(BuyerRegistrationActivity.this, "Buyer Registered Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent i = new Intent(BuyerRegistrationActivity.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);

                } else if (value.equalsIgnoreCase("3") || value.equalsIgnoreCase("4")) {
                    Toast.makeText(BuyerRegistrationActivity.this, "Invalid password length", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }
        }
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

    private void showNextScreen(String packets) {

        String cmd = Util.getJsonDataByField("cmd", packets);
        String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.buyer_registration) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            JSONObject reader = null;
            try {
                reader = new JSONObject(data);
                String value = reader.getString("02");
                if (value.equalsIgnoreCase("0")) {  // Already registered user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = firmwareSharedPref.edit();
                            editor.putString(ConstantUtil.buyername, name);
                            editor.putString(ConstantUtil.buyerphone, mob);
                            editor.putString(ConstantUtil.buyeremail, email);
                            editor.putString(ConstantUtil.adminPass, pwd);
                            editor.commit();

                            String type="0";
                            String uuid = firmwareSharedPref.getString(ConstantUtil.uuid1, "");
                            Contact contact=new Contact(name,mob,email,pwd,String.valueOf(buyerlong),String.valueOf(buyerlati),uuid,type);

                            db.AddBuyerInfo(contact);
                            Toast.makeText(BuyerRegistrationActivity.this, "Buyer Registered Succefully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent i = new Intent(BuyerRegistrationActivity.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);

                } else if (value.equalsIgnoreCase("3") || value.equalsIgnoreCase("4")) {
                    Toast.makeText(BuyerRegistrationActivity.this, "Invalid password length", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.submit_button:
                isValidation = false;
                name = et_name.getText().toString();
                email = et_email.getText().toString();
                pwd = et_pwd.getText().toString();
                mob = et_phone.getText().toString();
                if (name.length() < 3) {
                    if (name.length() == 0) {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Buyer Name");
                        isValidation = true;
                    } else {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid Name minimum 3 char");
                        isValidation = true;
                    }
                } else if (!Util.isValidMobile(mob)) {
                    if (mob.length() == 0) {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Mobile Number");
                        isValidation = true;
                    } else {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Mobile no. should contain atleast 10 digits");
                        isValidation = true;
                    }
                } else if (!Util.isValidEmaillId(email)) {
                    if (email.length() == 0) {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Email id");
                        isValidation = true;
                    } else {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid Email");
                        isValidation = true;
                    }
                } else if (!Util.isValidatePassword(pwd)) {
                    if (pwd.length() == 0) {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Enter password");
                        isValidation = true;
                    } else {
                        CustomDialog.getInstance().showSnackBar(baseLayout, "Invalid password");
                        isValidation = true;
                    }

                }
                if (isValidation == false) {
                    sendReqCmd = CommandUtil.buyer_registration;
                    reg_json = PacketsUrl.buyerRegistration(name, mob, email, pwd);
                    MyWifiReceiver.write(reg_json, "");
                    timerResponseDialog();
                }

                break;
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
        // Util.removeInstanceLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void timerResponseDialog() {
        Util.showLoader(BuyerRegistrationActivity.this);
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
                        Toast.makeText(BuyerRegistrationActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();


                    }
                });
                countDownTimer = null;
            }


        }.start();
    }


}
