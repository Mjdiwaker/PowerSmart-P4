package com.neotechindia.plugsmart.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.barcode.BarcodeScannerActivity;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;




public class ForgotPasswordActivity extends AppCompatActivity implements IUpdateUiListener {

    EditText et_licenceno, et_new_forgetpassword, et_forgotconfirmpassword;
    Button btn_forgetpassword_submit, btn_forgetpassword_cancel;
    Toolbar toolbar;
    View view;
    private RelativeLayout baseLayout;
    private Context mForgotActivityContext;
    private CountDownTimer countDownTimer;
    private boolean isValidationForgotPassw = false;
    ImageView imageButton_bar_serial, imageButton_bar_licence;
    DeviceBean deviceBean;
    public static String sendReqCmd = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        deviceBean = new DeviceBean();
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        ConstantUtil.updateListener = this;
        initViews();
        viewsListeners();
    }

    private void viewsListeners() {
        //below comment code for right drawable click listener

       /* et_licenceno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (et_licenceno.getRight() - et_licenceno.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ForgotPasswordActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                        } else {
                            barcodeScannerCalling();
                        }

                        return true;
                    }
                }
                return false;
            }
        });*/

        imageButton_bar_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ForgotPasswordActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    barcodeScannerCalling();
                }
            }
        });
        btn_forgetpassword_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String licence = et_licenceno.getText().toString();
                String newpass = et_new_forgetpassword.getText().toString();
                String confpass = et_forgotconfirmpassword.getText().toString();
                isValidationForgotPassw = false;
                if (TextUtils.isEmpty(licence) || licence.length() < 24) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must enter a valid license number.");
                    isValidationForgotPassw = true;
                } else if (newpass.startsWith(" ")) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "New password can not start with space");
                    isValidationForgotPassw = true;
                } else if (newpass.endsWith(" ")) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "New password can not end with space");
                    isValidationForgotPassw = true;
                } else if (TextUtils.isEmpty(newpass) || newpass.length() < 6) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must enter password min upto 6 characters.");
                    isValidationForgotPassw = true;
                } else if (TextUtils.isEmpty(confpass) || confpass.length() < 6) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must have 6 characters in your Confirm password");
                    isValidationForgotPassw = true;
                } else if (!confpass.contentEquals(newpass) || confpass.length() != newpass.length()) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "Passwords do not Match");
                    isValidationForgotPassw = true;
                }
                if (isValidationForgotPassw == false) {
                    sendReqCmd=CommandUtil.cmd_forget_password;
                    if (ConstantUtil.isForAPModeOnly) {
                        MyWifiReceiver.write(PacketsUrl.forgetAdminPassword(licence, newpass), CommandUtil.cmd_forget_password);
                    } else {
                      Util.sendPacketStationMode(deviceBean,PacketsUrl.forgetAdminPassword(licence, newpass),ForgotPasswordActivity.this);

                    }

                    timerResponseDialog();
                }
            }
        });
        btn_forgetpassword_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    private void initViews() {
        mForgotActivityContext = ForgotPasswordActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.forgetpassword));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        et_licenceno = (EditText) findViewById(R.id.et_licenceno);
        et_new_forgetpassword = (EditText) findViewById(R.id.et_forgetpassword);
        et_forgotconfirmpassword = (EditText) findViewById(R.id.et_forgotconfirmpassword);
        btn_forgetpassword_submit = (Button) findViewById(R.id.btn_forgetpassword_submit);
        btn_forgetpassword_cancel = (Button) findViewById(R.id.btn_forgetpassword_cancel);
        baseLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        imageButton_bar_licence = (ImageView) findViewById(R.id.imageButton_bar_licence);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Util.hideLoader();
        //  Util.removeInstanceLoader();
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_forget_password) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd="";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                SharedPreferences firmwareSharedPref = getSharedPreferences(ConstantUtil.plugLogs, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.adminPass, et_new_forgetpassword.getText().toString());
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ForgotPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid licence number", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_forget_password) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd="";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ForgotPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid licence number", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Util.dismissDialog();
        Util.removeDialogInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void barcodeScannerCalling() {
        Intent intent = new Intent(this, BarcodeScannerActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeScannerCalling();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Call Back method  to get the Message form  BarcodeScannerActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Added by sumit,Pattern has changed now so we have added new code here
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    try {
                        String scanResult = data.getStringExtra("SCAN_RESULT").trim();
                        Log.i("ScanResult", scanResult);
                        if (scanResult.contains(",")) {
                            String[] splitSRL = scanResult.split(",");
                            if (splitSRL[0].length() == 24) {

                                // scanResult will be ==  //SL NO.PDA003100004                                                                   LIC. AC4L0440BP03Z54F6MO0B73K
                                //String[] splitSRL = scanResult.split(",");
                                //String[] serialArray = splitSRL[1].split("\\.");
                                //  String[] licenceArray = splitSRL[0].split("\\.");
                                // eSerialNo.setText(serialArray[1].trim());
                                et_licenceno.setText(splitSRL[0].trim());
                            }
                            if (splitSRL[1].length() == 24) {
                                //  String[] serialArray = splitSRL[0].split("\\.");
                                //  String[] licenceArray = splitSRL[1].split("\\.");
                                //String[] splitSRL = scanResult.split(",");
                                //   eSerialNo.setText(splitSRL[0].trim());
                                et_licenceno.setText(splitSRL[1].trim());
                            }
                        }
                        else {
                            et_licenceno.setText(scanResult);
                        }
                    } catch (Exception e) {

                    }

                }


            }
        }

    }

    private void timerResponseDialog() {


        Util.showLoader(ForgotPasswordActivity.this);
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
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }

}
