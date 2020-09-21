package com.neotechindia.plugsmart.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.neotechindia.plugsmart.Data.DatabaseHandler;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.ConnectionListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.Contact_1;
import com.neotechindia.plugsmart.model.DeviceBean;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;
import com.neotechindia.plugsmart.requestor.SocketRequester;
import com.wang.avi.AVLoadingIndicatorView;

import io.fabric.sdk.android.Fabric;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.ArrayList;

public class Splash extends AppCompatActivity implements IUpdateUiListener, ConnectionListener {
    private Animation animLeft, animRight, animToFro, animStop, animUp, animDown, animRotate, animTogether, animMove2;
    TextView tv_plug_slogan, tv_plug_name;
    ImageView iv_splash_off;
    static public final int REQUEST_LOCATION = 1;
    public static String sendReqCmd = "";
    String GadgetName, Mac_Id;
    String licno, srno;
    AVLoadingIndicatorView avi;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        avi = findViewById(R.id.avi);
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        animLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left);
        animRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right);
        tv_plug_name = findViewById(R.id.tv_plug_name);
        iv_splash_off = findViewById(R.id.iv_splash_off);
        tv_plug_slogan = findViewById(R.id.tv_plug_slogan);
        ConstantUtil.updateListener = this;
        ConstantUtil.connectionListener = this;


        requestLocationPermission();

//
//        Log.d("Reading:", "Reading all contacts... ");
//        List<Contact_1> contactList = db.getAllDevices();
//
//        for (Contact_1 contact1 : contactList) {
//            String log = "ID:" + +contact1.getId()
//                    + ", NAME:" + contact1.getDevice_name()
//                    + " , Mac_ID:" + contact1.getMac_id()
//                    + " , Serial_Number:" + contact1.getSerial_number()
//                    + " , Licence_number:" + contact1.getLicence_number()
//                    + ", Status:" + contact1.getStatus();
//            Log.d("Name:", log);
//        }
//

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MyWifiReceiver.isWifiConnected(getApplicationContext())) {
        }
        timerResponseDialog();
    }

    @Override
    public void onWifiConnectStatus(boolean isConnected) {
        if (isConnected) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.arrayList, "");
            editor.commit();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_splash_off.setImageResource(R.drawable.splashon1);
                                }
                            });
                        }
                    }, 500);
                }
            });
            sendReqCmd = CommandUtil.cmd_get_serialliscence_number;
            MyWifiReceiver.write(PacketsUrl.getSerialLiscenseNumber(), sendReqCmd);
            Log.d("print", "onWifiConnectStatus: " + CommandUtil.cmd_get_serialliscence_number);
        } else {
            Toast.makeText(this, "Something Went Wrong...", Toast.LENGTH_LONG).show();
        }
    }

    private int position = 0;
    private ArrayList<DeviceBean> deviceList = new ArrayList<>();

    @Override
    public void onWifiConnectStatus(boolean isConnected, ArrayList<DeviceBean> arrayList) {
        if (true) {
            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(ConstantUtil.arrayList, json);
            editor.commit();
            this.deviceList.clear();
            this.deviceList = arrayList;
            if (arrayList != null) {
                arrayList.get(position).getIp();
                for (DeviceBean d : arrayList) {
                    String mac = d.macId;
                    new Thread(new SocketRequester(this, d.ip, 9998, mac, position)).start();
                    //new Thread(new SocketRequester(this, d.ip, 6199, mac, position)).start();
                }
            }
            isPacketReceived = true;
            Intent i = new Intent(Splash.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        String data = JsonUtil.getJsonDataByField("data", packets);


        if (cmd.equals("")) {
            if ((packets.length() >= 6 || packets.length() < 8) && (!packets.contains("{") || !packets.contains("}"))) {

            }
        } else {
            if (cmd.equals(CommandUtil.authentication)) {
                isPacketReceived = true;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                String dataFieldFirst = JsonUtil.getJsonDataByField("01", data);
                String modeOFIotDevice = JsonUtil.getJsonDataByField("02", data);


                if (dataFieldFirst == "0") {
                    ConstantUtil.isfromRegistration = false;
                    // Already registered user
                    if (GadgetName.equals("")) {
                        Intent i = new Intent(Splash.this, AddGadgetActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        ConstantUtil.isForAPModeOnly = true;
                        Intent i = new Intent(Splash.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (dataFieldFirst == "1") {
                    ConstantUtil.isfromRegistration = true;
                    // Client Registration
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Splash.this, ClientRegistrationActivity.class);
                            i.putExtra(ConstantUtil.ID, "1");
                            startActivity(i);
                            finish();
                        }
                    });

                } else if (dataFieldFirst == "2") {
                    ConstantUtil.isfromRegistration = true;
                    Intent i = new Intent(Splash.this, BuyerRegistrationActivity.class);
                    startActivity(i);
                    finish();
                    // Buyer's Registration

                } else if (dataFieldFirst == "3") {
                    ConstantUtil.isfromRegistration = true;
                    Intent i = new Intent(Splash.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);
                    finish();
                    // Client Registration
                }
            }
            if (cmd.equals(CommandUtil.cmd_get_mac_id_and_ip)) {
                GadgetName = JsonUtil.getJsonDataByField("02", data);
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.macid,JsonUtil.getJsonDataByField("01",data));
                editor.putString(ConstantUtil.gadgetName, JsonUtil.getJsonDataByField("02", data));
                editor.putString(ConstantUtil.licence_type, JsonUtil.getJsonDataByField("04", data));
                editor.commit();
                Mac_Id = JsonUtil.getJsonDataByField("01", data);
                MyWifiReceiver.write(PacketsUrl.deviceAuthentication(this), CommandUtil.authentication);

            } else if (cmd.equals(CommandUtil.cmd_get_serialliscence_number)) {
                sendReqCmd = "";
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.serialNumber, JsonUtil.getJsonDataByField("02", data));
                editor.putString(ConstantUtil.liscenceNumber, JsonUtil.getJsonDataByField("01", data));
                editor.commit();
                MyWifiReceiver.write(PacketsUrl.getMacIdOrIP(), CommandUtil.cmd_get_mac_id_and_ip);
            }
        }
    }

    @Override
    public void onPacketReceived(String packets) {
//        {"sid":"9000","sad":"x","cmd":"8271","data":{"01":"Fa24Z0F32dDAEA3YD5YI610Z","02":"SRD125000021","03":"70F11C08E822"}}

        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);
        Log.i("tester","got datum "+data);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_mac_id_and_ip)){
            final String type = JsonUtil.getJsonDataByField("04",data);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if(type!=null){



                           SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor = firmwareSharedPref.edit();
                           editor.putString(ConstantUtil.macid,JsonUtil.getJsonDataByField("01",data));
                           editor.putString(ConstantUtil.gadgetPos, JsonUtil.getJsonDataByField("03", data));
                           editor.putString(ConstantUtil.licence_type, JsonUtil.getJsonDataByField("04", data));
                           editor.commit();

                       }

                   }
               });
        }



//        if(type!=null){
//
//
//
//            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = firmwareSharedPref.edit();
//            editor.putString(ConstantUtil.licence_type, JsonUtil.getJsonDataByField("04", data));
//            editor.putString(ConstantUtil.iconType, JsonUtil.getJsonDataByField("03", data));
//            editor.commit();
//        }
        if (cmd.equals("")) {
            if ((packets.length() >= 6 || packets.length() < 8) && (!packets.contains("{") || !packets.contains("}"))) {

            }
        } else {
            if (cmd.equals(CommandUtil.authentication) && sendReqCmd.equalsIgnoreCase(cmd)) {
                sendReqCmd = "";
                isPacketReceived = true;
                String dataFieldFirst = JsonUtil.getJsonDataByField("01", data);
                String modeOFIotDevice = JsonUtil.getJsonDataByField("02", data);
                if (dataFieldFirst.equalsIgnoreCase("0")) {
                    ConstantUtil.isfromRegistration = false;
                    // Already registered user
                    if (GadgetName.equals("")) {
                        Intent i = new Intent(Splash.this, AddGadgetActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        ConstantUtil.isForAPModeOnly = true;
                        Intent i = new Intent(Splash.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (dataFieldFirst.equalsIgnoreCase("1")) {
                    // Client Registration
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Splash.this, ClientRegistrationActivity.class);
                            i.putExtra(ConstantUtil.ID, "1");
                            startActivity(i);
                            finish();
                        }
                    });

                } else if (dataFieldFirst.equalsIgnoreCase("2")) {
                    Intent i = new Intent(Splash.this, BuyerRegistrationActivity.class);
                    startActivity(i);
                    finish();
                    // Buyer's Registration

                } else if (dataFieldFirst.equalsIgnoreCase("3")) {
                    // Client Registration
                    Intent i = new Intent(Splash.this, ClientRegistrationActivity.class);
                    i.putExtra(ConstantUtil.ID, "2");
                    startActivity(i);
                    finish();
                }
            }
            if (cmd.equals(CommandUtil.cmd_get_mac_id_and_ip) && sendReqCmd.equalsIgnoreCase(cmd)) {
                sendReqCmd = "";
                GadgetName = JsonUtil.getJsonDataByField("02", data);
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.gadgetName, JsonUtil.getJsonDataByField("02", data));
                editor.putString(ConstantUtil.licence_type,JsonUtil.getJsonDataByField("03",data));
                editor.commit();
                Mac_Id = JsonUtil.getJsonDataByField("03", data);
                sendReqCmd = CommandUtil.authentication;
                MyWifiReceiver.write(PacketsUrl.deviceAuthentication(this), CommandUtil.authentication);

            } else if (cmd.equals(CommandUtil.cmd_get_serialliscence_number) && sendReqCmd.equalsIgnoreCase(cmd)) {
                sendReqCmd = "";
                SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.serialNumber, JsonUtil.getJsonDataByField("02", data));
                editor.putString(ConstantUtil.liscenceNumber, JsonUtil.getJsonDataByField("01", data));
                editor.commit();

                Mac_Id = JsonUtil.getJsonDataByField("03", data);
                srno = JsonUtil.getJsonDataByField("02", data);
                licno = JsonUtil.getJsonDataByField("01", data);

                String name = "plug";
                String status = "0";
                Contact_1 contact1 = new Contact_1(name, Mac_Id, srno, licno, status);
                db.AddUserInfo(contact1);
                Log.d("device_detail", "onPacketReceived: "+contact1
                );


//                String name="plug";
//                Contact_1 contact1=new Contact(name,Mac_Id,srno,licno,);
//                db.AddUserInfo(contact1);


                if (!TextUtils.isEmpty(Mac_Id) && !TextUtils.isEmpty(licno) && !TextUtils.isEmpty(srno)) {
                    ConstantUtil.macAndLicenseNoMap.put(Mac_Id, licno);
                    ConstantUtil.macAndSerialNoMap.put(Mac_Id, srno);

                    SharedPreferences getserialpref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = getserialpref.edit();
                    String macKey = "Macid" + Mac_Id;
                    editor1.putString(macKey, licno + "_" + srno);
                    editor1.commit();

                }
                sendReqCmd = CommandUtil.cmd_get_mac_id_and_ip;
                MyWifiReceiver.write(PacketsUrl.getMacIdOrIP(), CommandUtil.cmd_get_mac_id_and_ip);
            }
        }
    }

    private CountDownTimer countDownTimer;
    private boolean isPacketReceived = false;

    private void timerResponseDialog() {
        // Util.showLoader(Splash.this);
        avi.show();
        countDownTimer = new CountDownTimer(16000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avi.hide();
                        // Util.hideLoader();
                        if (!isPacketReceived) {
                            if (!ConstantUtil.isForAPModeOnly) {
                                Toast.makeText(Splash.this, getString(R.string.somethingWentWrongStation), Toast.LENGTH_LONG).show();
                                Toast.makeText(Splash.this, getString(R.string.pleaseTryAgain), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Splash.this, getString(R.string.somethingWentWrong), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                countDownTimer = null;
            }

        }.start();
    }


}
