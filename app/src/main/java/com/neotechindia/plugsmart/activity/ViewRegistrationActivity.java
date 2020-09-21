package com.neotechindia.plugsmart.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.adapter.ViewRegistrationAdapter;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.listeners.RemoveDeviceListener;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.MultiJsonModel;
import com.neotechindia.plugsmart.model.RegisteredClientBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.util.ArrayList;
import java.util.Map;

public class ViewRegistrationActivity extends AppCompatActivity implements IUpdateUiListener, RemoveDeviceListener {
    RecyclerView rv_view_registration_list;
    RecyclerView.LayoutManager layoutManager;
    ViewRegistrationAdapter viewRegistrationAdapter;
    ArrayList<RegisteredClientBean> arrayList;
    Toolbar toolbar;
    int key;
    DeviceBean deviceBean;
    private boolean isPacketReceived = false;
    public static String sendReqCmd = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceBean = new DeviceBean();
        setContentView(R.layout.activity_view_registration);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_view_registration));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getInt("key");
        }
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        ConstantUtil.updateListener = this;
        ConstantUtil.removeListener = this;
        initViews();
        getDeviceList();
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

    private void getDeviceList() {
        sendReqCmd=CommandUtil.cmd_view_registered_devices;
        if (ConstantUtil.isForAPModeOnly) {
            MyWifiReceiver.write(PacketsUrl.getViewRegisteredDevices(), CommandUtil.cmd_view_registered_devices);
        } else {
            Util.sendPacketStationMode(deviceBean, PacketsUrl.getViewRegisteredDevices(), ViewRegistrationActivity.this);
        }
    }

    private void showListForRegisteredDevices() {
        viewRegistrationAdapter = new ViewRegistrationAdapter(getApplicationContext(), arrayList);
        rv_view_registration_list.setAdapter(viewRegistrationAdapter);
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        rv_view_registration_list = findViewById(R.id.rv_view_registration_list);
        layoutManager = new LinearLayoutManager(this);
        rv_view_registration_list.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_view_registration_list.getContext(),
                DividerItemDecoration.VERTICAL);

        rv_view_registration_list.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        //String dataAuthentication = JsonUtil.getJsonDataByField("02", data);
        if (cmd.equalsIgnoreCase(CommandUtil.authentication)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            isPacketReceived = true;
            String data01 = JsonUtil.getJsonDataByField("01", data);
            if (data01.equalsIgnoreCase("0")) {
                //Other user has deleted
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.getViewRegisteredDevices(), CommandUtil.cmd_view_registered_devices);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.getViewRegisteredDevices(), ViewRegistrationActivity.this);

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceList();
                    }
                });
            } else {
                finishAffinity();
            }
        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_view_registered_devices) ) {

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            final MultiJsonModel multiJsonModel = (MultiJsonModel) JsonUtil.toModel(packets, MultiJsonModel.class);
            if (multiJsonModel != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewRegistrationAdapter = new ViewRegistrationAdapter(ViewRegistrationActivity.this, setData(multiJsonModel.getData()));
                        rv_view_registration_list.setAdapter(viewRegistrationAdapter);
                    }
                });
            } else {
                Toast.makeText(ViewRegistrationActivity.this, "Internal error occurs please retry", Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showListForRegisteredDevices();
                }
            });
        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_remove_registered_device)) {
            if (data.equalsIgnoreCase("0")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewRegistrationActivity.this, "User unregistered Successfully", Toast.LENGTH_LONG).show();
                        timerResponseDialog();
                        Util.hideLoader();
                    }

                });

                if (key == ConstantUtil.ClientRegKey) {
                    onBackPressed();
                }
                if (ConstantUtil.isForAPModeOnly) {
                    isPacketReceived = false;
                    MyWifiReceiver.write(PacketsUrl.deviceAuthentication(this), CommandUtil.authentication);
                } else {
                    isPacketReceived = false;
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.deviceAuthentication(this), ViewRegistrationActivity.this);


                }


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewRegistrationActivity.this, "User cannot be unregistered", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    private ArrayList<RegisteredClientBean> setData(Map<String, String> data) {
        ArrayList<RegisteredClientBean> listdata = new ArrayList<>();
        for (int i = 0; i < data.size(); i += 2) {
            String index = (i + 2) < 10 ? "0" + (i + 2) : "" + (i + 2);
            listdata.add(new RegisteredClientBean(data.get(index)));
        }
        arrayList = listdata;
        return listdata;
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        // String dataAuthentication = JsonUtil.getJsonDataByField("02", data);
        if (cmd.equalsIgnoreCase(CommandUtil.authentication)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            isPacketReceived = true;
            String data01 = JsonUtil.getJsonDataByField("01", data);
            if (data01.equalsIgnoreCase("0")) {
                //Other user has deleted
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.getViewRegisteredDevices(), CommandUtil.cmd_view_registered_devices);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.getViewRegisteredDevices(), ViewRegistrationActivity.this);

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceList();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.hideLoader();
                    }
                });
                finishAffinity();
            }
        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_view_registered_devices)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            final MultiJsonModel multiJsonModel = (MultiJsonModel) JsonUtil.toModel(packets, MultiJsonModel.class);
            if (multiJsonModel != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewRegistrationAdapter = new ViewRegistrationAdapter(ViewRegistrationActivity.this, setData(multiJsonModel.getData()));
                        rv_view_registration_list.setAdapter(viewRegistrationAdapter);
                    }
                });
            } else {
                Toast.makeText(ViewRegistrationActivity.this, "Internal error occurs please retry", Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showListForRegisteredDevices();
                }
            });
        }
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_remove_registered_device)) {
            if (data.equalsIgnoreCase("0")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewRegistrationActivity.this, "User unregistered Successfully", Toast.LENGTH_LONG).show();
                        timerResponseDialog();
                        Util.hideLoader();
                    }
                });

                if (key == ConstantUtil.ClientRegKey) {
                    finish();
                }
                if (ConstantUtil.isForAPModeOnly) {
                    isPacketReceived = false;
                    MyWifiReceiver.write(PacketsUrl.deviceAuthentication(this), CommandUtil.authentication);
                } else {
                    isPacketReceived = false;
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.deviceAuthentication(this), ViewRegistrationActivity.this);


                }


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewRegistrationActivity.this, "User cannot be unregistered", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    private CountDownTimer countDownTimer;

    private void timerResponseDialog() {

        //showProgressDialog();
        Util.showLoader(ViewRegistrationActivity.this);
        countDownTimer = new CountDownTimer(10000, 1000) {


            public void onTick(long millisUntilFinished) {
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");
                Util.hideLoader();                                                                                //loader

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Util.hideLoader();
                        if (!isPacketReceived) {
                            Toast.makeText(ViewRegistrationActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                        }


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Util.hideLoader();
    }

    @Override
    public void onRemoveDevice(final String name) {
        String msg2 = getString(R.string.unregisterDevice);

        CustomDialog.getInstance().showYesNoAlert(ViewRegistrationActivity.this, "Alert!!!", msg2, "Yes", "No", new AquasmartDialogListener() {
            @Override
            public void onButtonOneClick() {
                if (ConstantUtil.isForAPModeOnly) {
                    MyWifiReceiver.write(PacketsUrl.getUrlRemoveRegisteredDevice(name), CommandUtil.cmd_remove_registered_device);
                } else {
                    Util.sendPacketStationMode(deviceBean, PacketsUrl.getUrlRemoveRegisteredDevice(name), ViewRegistrationActivity.this);
                }

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
}
