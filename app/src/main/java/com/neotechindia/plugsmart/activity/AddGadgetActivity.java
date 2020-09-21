package com.neotechindia.plugsmart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.listeners.IimagePicker;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.model.ImagePickBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.util.ArrayList;


public class AddGadgetActivity extends AppCompatActivity implements IUpdateUiListener, IimagePicker, AdapterView.OnItemSelectedListener {
    private EditText et_gadget_name;
    private Button btn_addGadget_submit;
    private Toolbar toolbar;
    private DeviceBean deviceBean;
    private int imgPosition = 00;
    private FrameLayout fl_img;
    private ImageView iv_ing, iv_select;
    private LinearLayout ll_img;
    public static String sendReqCmd = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gadget);
        ConstantUtil.iimagePick = this;
        deviceBean = new DeviceBean();
        Bundle bundle = getIntent().getExtras();
        deviceBean = getIntent().getParcelableExtra(ConstantUtil.stationList);
        ConstantUtil.updateListener = this;
        initViews();
        viewsListeners();
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

    private void viewsListeners() {

        btn_addGadget_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_gadget_name.getText().length() < 3) {
                    Toast.makeText(AddGadgetActivity.this, "Please Enter Min. 3 Character", Toast.LENGTH_LONG).show();

                } else {
                    timerResponseDialog();
                    if (ConstantUtil.isForAPModeOnly) {
                        if (Logger.isPlugSmart) {
                            sendReqCmd = CommandUtil.cmd_edit_gadget_name;
                            MyWifiReceiver.write(PacketsUrl.editGadgetName(et_gadget_name.getText().toString(),imgPosition), CommandUtil.cmd_edit_gadget_name);
                          //  MyWifiReceiver.write(PacketsUrl.editGadgetName(et_gadget_name.getText().toString()), CommandUtil.cmd_edit_gadget_name);
                        } else {
                            sendReqCmd = CommandUtil.cmd_edit_gadget_name;
                            MyWifiReceiver.write(PacketsUrl.editGadgetName(et_gadget_name.getText().toString(), imgPosition), CommandUtil.cmd_edit_gadget_name);
                          //  MyWifiReceiver.write(PacketsUrl.editGadgetName(et_gadget_name.getText().toString()), CommandUtil.cmd_edit_gadget_name);
                        }

                    } else {

                        if (!Logger.isPlugSmart) {
                            sendReqCmd = CommandUtil.cmd_edit_gadget_name;
                            String packet = PacketsUrl.editGadgetName(et_gadget_name.getText().toString());
                            Util.sendPacketStationMode(deviceBean, packet, AddGadgetActivity.this);
                        } else {
                            sendReqCmd = CommandUtil.cmd_edit_gadget_name;
                            String packet = PacketsUrl.editGadgetName(et_gadget_name.getText().toString(), imgPosition);
                            Util.sendPacketStationMode(deviceBean, packet, AddGadgetActivity.this);
                        }
                    }
                }
            }
        });
    }

    final ArrayList<ImagePickBean> arrayList = new ArrayList<>();
    int count = 0;
    Spinner spinner;

    private void initViews() {
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter =ArrayAdapter.createFromResource(this,R.array.Gadgets,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setPrompt("Select Gadget type");
        spinner.setOnItemSelectedListener(this);

        fl_img = findViewById(R.id.fl_img);
        iv_select = findViewById(R.id.iv_select);
        iv_ing = findViewById(R.id.iv_ing);
        setImages();
        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddGadgetActivity.this, ImagePick.class);
                i.putExtra("picArraylist", arrayList);
                startActivity(i);
            }
        });
        et_gadget_name = findViewById(R.id.et_gadget_name);
        btn_addGadget_submit = findViewById(R.id.btn_addGadget_submit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (ConstantUtil.isFromSettings) {
            toolbar.setTitle(getString(R.string.editGadget));
        } else {
            toolbar.setTitle(getString(R.string.addGadget));
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (ConstantUtil.isForAPModeOnly) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            et_gadget_name.setText(firmwareSharedPref.getString(ConstantUtil.gadgetName, ""));
        } else {
            et_gadget_name.setText(deviceBean.name);
        }
        et_gadget_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (count == 0) {
                    if (b) {
                        count++;
                        Util.hideKeyboard(AddGadgetActivity.this);
                    }
                }
            }
        });
    }

    private void setImages() {
        try {


            if (Logger.isPlugSmart) {
                fl_img.setVisibility(View.VISIBLE);
            } else {
                fl_img.setVisibility(View.GONE);
            }
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
               switch (firmwareSharedPref.getString(ConstantUtil.gadgetPos, "")){
          //  switch (deviceBean.iconType) {
                case "0": {
                    iv_ing.setImageResource(R.drawable.ac_black);
                    imgPosition = 00;
                    break;
                }
                case "1": {
                    iv_ing.setImageResource(R.drawable.tv_black);
                    imgPosition = 01;
                    break;
                }
                case "2": {
                    iv_ing.setImageResource(R.drawable.fan_black);
                    imgPosition = 02;
                    break;
                }
                case "3": {
                    iv_ing.setImageResource(R.drawable.fridge_black);
                    imgPosition = 03;
                    break;
                }
                case "4": {
                    iv_ing.setImageResource(R.drawable.geyser_black);
                    imgPosition = 04;
                    break;
                }
                case "5": {
                    iv_ing.setImageResource(R.drawable.washing_machine_black);
                    imgPosition = 05;
                    break;
                }
                case "6": {
                    iv_ing.setImageResource(R.drawable.lugo);
                    imgPosition = 06;
                    break;
                }
                default: {
                    iv_ing.setImageResource(R.drawable.lugo);
                    imgPosition = 06;
                    break;
                }
            }
            ImagePickBean imagePickBeanAc = new ImagePickBean();
            imagePickBeanAc.setPicId(R.drawable.ac_black);
            imagePickBeanAc.setPicPosition(00);
            arrayList.add(imagePickBeanAc);
            ImagePickBean imagePickBeanTv = new ImagePickBean();
            imagePickBeanTv.setPicId(R.drawable.tv_black);
            imagePickBeanTv.setPicPosition(01);
            arrayList.add(imagePickBeanTv);
            ImagePickBean imagePickBeanFan = new ImagePickBean();
            imagePickBeanFan.setPicId(R.drawable.fan_black);
            imagePickBeanFan.setPicPosition(02);
            arrayList.add(imagePickBeanFan);
            ImagePickBean imagePickBeanFridge = new ImagePickBean();
            imagePickBeanFridge.setPicId(R.drawable.fridge_black);
            imagePickBeanFridge.setPicPosition(03);
            arrayList.add(imagePickBeanFridge);
            ImagePickBean imagePickBeanGeyser = new ImagePickBean();
            imagePickBeanGeyser.setPicId(R.drawable.geyser_black);
            imagePickBeanGeyser.setPicPosition(04);
            arrayList.add(imagePickBeanGeyser);
            ImagePickBean imagePickBeanWm = new ImagePickBean();
            imagePickBeanWm.setPicId(R.drawable.washing_machine_black);
            imagePickBeanWm.setPicPosition(05);
            arrayList.add(imagePickBeanWm);
            ImagePickBean imagePickBeanLogo = new ImagePickBean();
            imagePickBeanLogo.setPicId(R.drawable.lugo);
            imagePickBeanLogo.setPicPosition(06);
            arrayList.add(imagePickBeanLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private CountDownTimer countDownTimer;

    private void timerResponseDialog() {


        Util.showLoader(AddGadgetActivity.this);
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
                        Toast.makeText(AddGadgetActivity.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();


                    }
                });
                countDownTimer = null;

            }

        }.start();
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equals(CommandUtil.cmd_edit_gadget_name) && sendReqCmd.equalsIgnoreCase(cmd)) {
            sendReqCmd = "";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            String pos = JsonUtil.getJsonDataByField("02", data);
            String dataField1 = JsonUtil.getJsonDataByField("01", data);
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.gadgetName, dataField1);
            editor.putString(ConstantUtil.gadgetPos, pos);
            editor.commit();
            if (ConstantUtil.isfromRegistration) {
                //ConstantUtil.isForAPModeOnly = true;
                Intent i = new Intent(AddGadgetActivity.this, NetworkSettingsActivity.class);
                startActivity(i);
                finish();
            } else {
                //ConstantUtil.isForAPModeOnly = true;
                Intent i = new Intent(AddGadgetActivity.this, MainActivity.class);
                i.putExtra(ConstantUtil.isFromAddGadget, true);
                i.putExtra(ConstantUtil.AddGadgetMac, deviceBean.macId);
                startActivity(i);
                finish();
            }
            ConstantUtil.isFromSettings = false;

        }
    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);
        if (cmd.equals(CommandUtil.cmd_edit_gadget_name) && sendReqCmd.equalsIgnoreCase(cmd)) {
            sendReqCmd = "";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            Util.hideLoader();
            String pos = JsonUtil.getJsonDataByField("02", data);
            String dataField1 = JsonUtil.getJsonDataByField("01", data);
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.gadgetName, dataField1);
            editor.putString(ConstantUtil.gadgetPos, pos);
            editor.commit();

            if (ConstantUtil.isfromRegistration) {
                ConstantUtil.isForAPModeOnly = true;
                Intent i = new Intent(AddGadgetActivity.this, NetworkSettingsActivity.class);
                startActivity(i);
                finish();
            } else {
                ConstantUtil.isForAPModeOnly = true;
                Intent i = new Intent(AddGadgetActivity.this, MainActivity.class);
                i.putExtra(ConstantUtil.isFromAddGadget, true);
                // i.putExtra(ConstantUtil.AddGadgetMac,deviceBean.macId);
                startActivity(i);
                finish();
            }
            ConstantUtil.isFromSettings = false;


        }
    }


    @Override
    public void image(int i, int id) {
        this.imgPosition = i;
        iv_ing.setVisibility(View.VISIBLE);
        iv_ing.setImageResource(id);
        spinner.setSelection(i,true);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     String text=parent.getItemAtPosition(position).toString();
   //  Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
        ImagePickBean bean = arrayList.get(position);
        image(bean.getPicPosition(), bean.getPicId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
