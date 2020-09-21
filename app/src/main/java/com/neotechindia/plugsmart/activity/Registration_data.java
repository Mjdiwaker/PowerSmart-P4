package com.neotechindia.plugsmart.activity;

import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.model.DeviceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration_data extends AppCompatActivity {
    private EditText et_serialno, et_licenceno, et_phone, et_name, et_email;
    private Button submit_button;
    private Toolbar toolbar;
    private Location location = null;
    private LocationManager locationManager = null;
    private LocationManager lm;
    private AssistStructure.ViewNode emailValidate;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_data);




        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.persnl_info));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        initViews();
        check();

        et_name.addTextChangedListener(logintextwatcher);
        et_phone.addTextChangedListener(logintextwatcher);
        et_email.addTextChangedListener(logintextwatcher);


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    ///////////////////////// Edge Registration////////////////////////
                    SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    ArrayList<SubmitDetailsRequest> requests = new ArrayList<>();

                    SubmitDetailsRequest request = new SubmitDetailsRequest();
                    request.setName(et_name.getText().toString());
                    request.setEmail(et_email.getText().toString());
                    request.setPhone(et_phone.getText().toString());
                    request.setUuid(firmwareSharedPref.getString(ConstantUtil.uuid1, ""));

                    ArrayList<DeviceBean> arrayListnew = MainActivity.arrayList;
                    int size = arrayListnew.size();
                    List<Map<String, Object>> data = new ArrayList<>();
                    request.setDevices(arrayListnew.size());
                    SharedPreferences getserialpref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    for (DeviceBean deviceBean : arrayListnew) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", deviceBean.getName());
                        map.put("mac_id", deviceBean.getMacId());
                        map.put("status", deviceBean.getSource());
                        String macKey = "Macid" + deviceBean.getMacId();


//
//                        String latestMac = "";
//                        String macKey = "Macid" + deviceBean.getMacId();
//                        String newMac[] = macKey.split(":");
//                        for (int i = 0; i < newMac.length; i++) {
//                            latestMac = latestMac + newMac[i];
//                        }

                        String ser_lic = getserialpref.getString(macKey, "");
                        String licNo = ser_lic.substring(0, ser_lic.indexOf("_") - 1);
                        String serial = ser_lic.substring(ser_lic.indexOf("_") + 1, ser_lic.length() - 1);
                        map.put("serial", serial);
                        map.put("licNo", licNo);

                        data.add(map);
                    }
                    request.setData(data);


                    requests.add(request);

                    Call<GeneralResponse> call = RetrofitRequest.submitDetails(request);
                    call.enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

//                            GeneralResponse generalResponse = response.body();
//                            Toast.makeText(Registration_data.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                            //Toast.makeText(Registration_data.this, "Detail Submit Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {

                            Toast.makeText(Registration_data.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                ///////////////////////// Client Registration////////////////////////

                if (ConstantUtil.clientName != "" || ConstantUtil.clientPhone != "") {

                    SubmitDetailsRequest request2 = new SubmitDetailsRequest();
                    SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);

                    request2.setClientname(firmwareSharedPref.getString(ConstantUtil.clientName, ""));
                    request2.setClientphone(firmwareSharedPref.getString(ConstantUtil.clientPhone, ""));
                    request2.setClientlong(firmwareSharedPref.getString(ConstantUtil.clientlong, ""));
                    request2.setClientlat(firmwareSharedPref.getString(ConstantUtil.clientlang, ""));
                    request2.setUuid(firmwareSharedPref.getString(ConstantUtil.uuid1, ""));

                    ArrayList<DeviceBean> arrayListnew1 = MainActivity.arrayList;
                    int size1 = arrayListnew1.size();
                    List<Map<String, Object>> data1 = new ArrayList<>();
                    SharedPreferences getserialpref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    request2.setDevices(arrayListnew1.size());
                    for (DeviceBean deviceBean : arrayListnew1) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("macid", deviceBean.getMacId());
                        String macKey = "Macid" + deviceBean.getMacId();


                        String ser_lic = getserialpref.getString(macKey, "");
                        String lic_no = ser_lic.substring(0, ser_lic.indexOf("_") - 1);
                        String serial = ser_lic.substring(ser_lic.indexOf("_") + 1, ser_lic.length() - 1);
                        map.put("serial", serial);
                        map.put("lic_no", lic_no);


                        data1.add(map);
                    }
                    request2.setData(data1);


                    Call<GeneralResponse> call2 = Retrofit_request2.submitDetails(request2);
                    call2.enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call2, @NonNull Response<GeneralResponse> response) {


                            GeneralResponse generalResponse = response.body();
                            //Toast.makeText(Registration_data.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();

                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {

                            //Toast.makeText(Registration_data.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                ///////////////////////// Buyer Registration////////////////////////


                if (ConstantUtil.buyername != "") {

                    SubmitDetailsRequest request1 = new SubmitDetailsRequest();
                    SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);

                    request1.setBuyername(firmwareSharedPref.getString(ConstantUtil.buyername, ""));
                    request1.setBuyeremail(firmwareSharedPref.getString(ConstantUtil.buyeremail, ""));
                    request1.setBuyerphone(firmwareSharedPref.getString(ConstantUtil.buyerphone, ""));
                    request1.setUuid(firmwareSharedPref.getString(ConstantUtil.uuid1, ""));
                    request1.setBuyerlong(firmwareSharedPref.getString(ConstantUtil.buyerlong, ""));
                    request1.setBuyerlat(firmwareSharedPref.getString(ConstantUtil.buyerlat, ""));


                    ArrayList<DeviceBean> arrayListnew = MainActivity.arrayList;
                    int size = arrayListnew.size();
                    List<Map<String, Object>> data = new ArrayList<>();
                    request1.setDevices(arrayListnew.size());
                    SharedPreferences getserialpref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    for (DeviceBean deviceBean : arrayListnew) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("devicename", deviceBean.getName());
                        map.put("macid", deviceBean.getMacId());
                        map.put("devicestatus", deviceBean.getSource());
                        String macKey = "Macid" + deviceBean.getMacId();


                        String ser_lic = getserialpref.getString(macKey, "");
                        String lic_no = ser_lic.substring(0, ser_lic.indexOf("_") - 1);
                        String serial = ser_lic.substring(ser_lic.indexOf("_") + 1, ser_lic.length() - 1);
                        map.put("serial", serial);
                        map.put("lic_no", lic_no);

                        data.add(map);
                    }
                    request1.setData(data);


                    Call<GeneralResponse> call1 = Retrofit_request1.submitDetails(request1);
                    call1.enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call1, @NonNull Response<GeneralResponse> response) {


                            GeneralResponse generalResponse = response.body();
                            //Toast.makeText(Registration_data.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();

                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {

                            //Toast.makeText(Registration_data.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

    }

    private void check() {

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
                        Toast.makeText(Registration_data.this, "Invalid Name minimum 3 char", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        et_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_phone.getText().length() < 13) {
                        Toast.makeText(Registration_data.this, "Mobile no. should contain atleast 10 digits", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    private CountDownTimer countDownTimer;

    private void timerResponseDialog() {


        Util.showLoader(Registration_data.this);
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
                        Toast.makeText(Registration_data.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }








    private TextWatcher logintextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String username = et_name.getText().toString().trim();
            String useremail = et_email.getText().toString().trim();
            String userphone = et_phone.getText().toString().trim();

            submit_button.setEnabled(!username.isEmpty() && !useremail.isEmpty() && !userphone.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initViews() {

        et_phone = findViewById(R.id.et_phone);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        submit_button = findViewById(R.id.submit_buttonregi);
    }

    private boolean validate() {

        if(TextUtils.isEmpty(et_name.getText().toString())){
            Toast.makeText(this, "Please Enter your full name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(et_email.getText().toString())){
            Toast.makeText(this, "Please Enter your Email",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(et_phone.getText().toString())){
            Toast.makeText(this, "Please Enter your Mobile Number",Toast.LENGTH_SHORT).show();
            return false;
        }



        return true;
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


}


