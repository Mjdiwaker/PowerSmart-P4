package com.neotechindia.plugsmart.activity;

/**
 * Created by skamra on 12/27/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;




public class ChangePasswordActivity extends AppCompatActivity implements IUpdateUiListener {
    EditText et_newpassword, et_confirmpassword, et_changePassword_oldPassword;
    Button btn_changepassword_submit, btn_changepassword_cancel;
    Toolbar toolbar;
    View view;
    private RelativeLayout baseLayout;
    private boolean isValidationChangePassw = false;
    private CountDownTimer countDownTimer;
    DeviceBean deviceBean;
    public static String sendReqCmd = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceBean = new DeviceBean();
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        setContentView(R.layout.activity_change_password);
        ConstantUtil.updateListener = this;
        initViews();
        viewsListeners();
    }

    private void viewsListeners() {
        btn_changepassword_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidationChangePassw = false;
                String newpass = et_newpassword.getText().toString();
                String confpass = et_confirmpassword.getText().toString();
                String oldpass = et_changePassword_oldPassword.getText().toString();
                if (TextUtils.isEmpty(oldpass)) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "Enter Old Password");

                    isValidationChangePassw = true;
                } else if (oldpass.length() < 6) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must have 6 characters in your old password");
                    isValidationChangePassw = true;
                } else if (TextUtils.isEmpty(newpass) || newpass.length() < 6) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must have 6 characters in your new password");
                    isValidationChangePassw = true;
                } else if (newpass.startsWith(" ")) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "New password can not start with space");
                    isValidationChangePassw = true;

                } else if (newpass.endsWith(" ")) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "New password can not end with space");
                    isValidationChangePassw = true;
                } else if (TextUtils.isEmpty(confpass) || confpass.length() < 6) {
                    CustomDialog.getInstance().showSnackBar(baseLayout, "You must have 6 characters in your Confirm password");

                    isValidationChangePassw = true;
                } else if (!confpass.contentEquals(newpass) || confpass.length() != newpass.length()) {

                    CustomDialog.getInstance().showSnackBar(baseLayout, "Passwords do not Match");
                    isValidationChangePassw = true;
                }
                if (isValidationChangePassw == false) {
                    if (ConstantUtil.isForAPModeOnly) {
                        sendReqCmd=CommandUtil.cmd_change_password;
                        MyWifiReceiver.write(PacketsUrl.changeAdminPassword(oldpass, newpass), CommandUtil.cmd_change_password);
                    } else {
                        sendReqCmd=CommandUtil.cmd_change_password;
                        Util.sendPacketStationMode(deviceBean, PacketsUrl.changeAdminPassword(oldpass, newpass), ChangePasswordActivity.this);

                    }

                    timerResponseDialog();

                }
            }
        });
        btn_changepassword_cancel.setOnClickListener(new View.OnClickListener() {
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_change_password));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        et_newpassword = (EditText) findViewById(R.id.et_newpassword);
        et_confirmpassword = (EditText) findViewById(R.id.et_confirmpassword);
        et_changePassword_oldPassword = (EditText) findViewById(R.id.et_changePassword_oldPassword);
        baseLayout = (RelativeLayout) findViewById(R.id.baseLayout);
        btn_changepassword_submit = (Button) findViewById(R.id.btn_changepassword_submit);
        btn_changepassword_cancel = (Button) findViewById(R.id.btn_changepassword_cancel);
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
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_change_password) &&  sendReqCmd.equalsIgnoreCase(cmd)) {
            sendReqCmd="";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.adminPass, et_newpassword.getText().toString());
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_change_password) &&  sendReqCmd.equalsIgnoreCase(cmd)) {
            sendReqCmd="";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            if (data.equalsIgnoreCase("0")) {
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.adminPass, et_newpassword.getText().toString());
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
        //  Util.removeInstanceLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void timerResponseDialog() {

        Util.showLoader(ChangePasswordActivity.this);
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
                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }
}
