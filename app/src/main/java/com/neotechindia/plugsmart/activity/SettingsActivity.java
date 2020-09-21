package com.neotechindia.plugsmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.adapter.SettingListAdapter;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.SettingListBean;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements IUpdateUiListener, SettingListAdapter.OnListItemClickCallBack {
    RecyclerView rv_settings_list;
    RecyclerView.LayoutManager layoutManager;
    SettingListAdapter settingListAdapter;
    ArrayList<SettingListBean> arrayList;
    Toolbar toolbar;
    DeviceBean deviceBean;
    private static String sendReqCmd = "";
    private SettingListAdapter.OnListItemClickCallBack onListItemClickCallBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceBean = new DeviceBean();
        setContentView(R.layout.activity_settings);
        ConstantUtil.updateListener = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_settings));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        onListItemClickCallBack=this;
        initViews();
        setlistForSettings();
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
    protected void onResume() {
        super.onResume();
        ConstantUtil.updateListener = this;
    }

    private void setlistForSettings() {

        settingListAdapter = new SettingListAdapter(getApplicationContext(), arrayList, deviceBean,onListItemClickCallBack);
        rv_settings_list.setAdapter(settingListAdapter);
    }

    private void initViews() {

        arrayList = new ArrayList<>();
        if (Logger.isPlugSmart) {

            SettingListBean settingListBeanForgot = new SettingListBean();
            settingListBeanForgot.setId(R.drawable.forgot_password);
            settingListBeanForgot.setName("Forgot Password");
            arrayList.add(settingListBeanForgot);

            SettingListBean settingListBeanChange = new SettingListBean();
            settingListBeanChange.setId(R.drawable.reset_password);
            settingListBeanChange.setName("Change Password");
            arrayList.add(settingListBeanChange);

            SettingListBean settingListBeanNetwork = new SettingListBean();
            settingListBeanNetwork.setId(R.drawable.network_setting);
            settingListBeanNetwork.setName("Network Settings");
            arrayList.add(settingListBeanNetwork);

            SettingListBean settingListBeanView = new SettingListBean();
            settingListBeanView.setId(R.drawable.view_registration);
            settingListBeanView.setName("View Registrations");
            arrayList.add(settingListBeanView);
        } else {
            SettingListBean settingListBeanForgot = new SettingListBean();
            settingListBeanForgot.setId(R.drawable.forgot_password);
            settingListBeanForgot.setName("Forgot Password");
            arrayList.add(settingListBeanForgot);

            SettingListBean settingListBeanChange = new SettingListBean();
            settingListBeanChange.setId(R.drawable.reset_password);
            settingListBeanChange.setName("Change Password");
            arrayList.add(settingListBeanChange);

            SettingListBean settingListBeanTime = new SettingListBean();
            settingListBeanTime.setId(R.drawable.baseline_schedule_black_24);
            settingListBeanTime.setName("Time Sync");
            arrayList.add(settingListBeanTime);

            SettingListBean settingListBeanNetwork = new SettingListBean();
            settingListBeanNetwork.setId(R.drawable.network_setting);
            settingListBeanNetwork.setName("Network Settings");
            arrayList.add(settingListBeanNetwork);

            SettingListBean settingListBeanView = new SettingListBean();
            settingListBeanView.setId(R.drawable.view_registration);
            settingListBeanView.setName("View Registrations");
            arrayList.add(settingListBeanView);

        }

        rv_settings_list = findViewById(R.id.rv_settings_list);
        layoutManager = new LinearLayoutManager(this);
        rv_settings_list.setLayoutManager(layoutManager);
    }

    private void showAlertConfirmationWithTwoButton(String msg) {
        // get prompts.xml view
        try {
            CustomDialog.getInstance().showDialogEditText(SettingsActivity.this, msg, msg, "Cancel", "Ok", new AquasmartDialogListener() {
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
    public void onPacketReceived(String packets, String mac) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_password_veryfication) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equalsIgnoreCase("0")) {
                        if(Logger.isPlugSmart) {
                            switch (position) {
                                case 2: {

                                    Intent i = new Intent(SettingsActivity.this, NetworkSettingsActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                    break;
                                }
                                case 3: {

                                    Intent i = new Intent(SettingsActivity.this, ViewRegistrationActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        else
                        {
                            switch (position) {
                                case 3: {

                                    Intent i = new Intent(SettingsActivity.this, NetworkSettingsActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;
                                }
                                case 4: {

                                    Intent i = new Intent(SettingsActivity.this, ViewRegistrationActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    } else {

                                Toast.makeText(SettingsActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                showAlertConfirmationWithTwoButton("Enter Admin Password");


                    }
                }
            });
        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_password_veryfication) && cmd.equalsIgnoreCase(sendReqCmd)) {
            sendReqCmd = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equalsIgnoreCase("0")) {
                        if(Logger.isPlugSmart) {
                            switch (position) {
                                case 2: {

                                    Intent i = new Intent(SettingsActivity.this, NetworkSettingsActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                    break;
                                }
                                case 3: {

                                    Intent i = new Intent(SettingsActivity.this, ViewRegistrationActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        else
                        {
                            switch (position) {
                                case 3: {

                                    Intent i = new Intent(SettingsActivity.this, NetworkSettingsActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);

                                    break;
                                }
                                case 4: {

                                    Intent i = new Intent(SettingsActivity.this, ViewRegistrationActivity.class);
                                    i.putExtra(ConstantUtil.stationList, deviceBean);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    } else {

                        Toast.makeText(SettingsActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        showAlertConfirmationWithTwoButton("Enter Admin Password");


                    }
                }
            });
        }
    }

    private int position = -1;

    @Override
    public void onItemClick(View view, int pos) {
        if(Logger.isPlugSmart) {
            switch (pos) {
                case 2: {
                    position = pos;
                    showAlertConfirmationWithTwoButton(getString(R.string.EnterAdminPassword));

                    break;
                }
                case 3: {
                    position = pos;
                    showAlertConfirmationWithTwoButton(getString(R.string.EnterAdminPassword));

                    break;
                }
                default:
                    break;
            }
        }
        else
        {
            switch (pos) {
                case 3: {
                    position = pos;
                    showAlertConfirmationWithTwoButton(getString(R.string.EnterAdminPassword));

                    break;
                }
                case 4: {
                    position = pos;
                    showAlertConfirmationWithTwoButton(getString(R.string.EnterAdminPassword));

                    break;
                }
                default:
                    break;
            }
        }
    }
}
