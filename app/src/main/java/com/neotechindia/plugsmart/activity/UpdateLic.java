package com.neotechindia.plugsmart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;
import com.neotechindia.plugsmart.requestor.SocketRequester;

public class UpdateLic extends AppCompatActivity implements IUpdateUiListener, IPacketReceiveCallBack {

    public static TextView mac_id,serail,lic,model;
    String macid="";
    Button scan,update;
    String LIC="",SERAIL="";
    String MACID="";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lic);
        mac_id=findViewById(R.id.getmacid);
        serail=findViewById(R.id.tv_serail);
        lic=findViewById(R.id.tv_lic);
        model=findViewById(R.id.tv_model);
        scan=findViewById(R.id.btn_scan);
        scan.setBackgroundColor(Color.parseColor("#C6E2FF"));
        update=findViewById(R.id.btn_send_to);
        update.setBackgroundColor(Color.parseColor("#C6E2FF"));
        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        macid = (firmwareSharedPref.getString(ConstantUtil.macid, ""));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);





        ConstantUtil.updateListener = this;
        MyWifiReceiver.setOnPacketReceiveCallBack(this);
        SocketRequester.setOnPacketReceiveCallBack(this);
        //YAHA SE TRY KARO
        String completeMac = "";
        String[] mac = macid.split(":");
        for (int i = 0; i < mac.length; i++) {
            if (mac[i].length() == 1) {
                mac[i] = "0" + mac[i];
            }
            completeMac += mac[i];
        }
        if (!macid.equals("")) {
            this.macid = completeMac.toUpperCase().toString().trim();
            final String a = completeMac.toUpperCase().toString().trim();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mac_id.setText("MacId:-"+a);
                    MACID=a;
                }
            });
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UpdateLic.this,ScanActivity.class));

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWifiReceiver.write(PacketsUrl.saveDeviceCredientials(LIC,SERAIL,MACID), ConstantUtil.IBuildConstantPlugSmart.IP,ConstantUtil.IBuildConstantPlugSmart.PORT);
            }
        });
    }

    @Override
    protected void onResume() {

        LIC = getIntent().getStringExtra(ConstantUtil.macid);
        SERAIL = getIntent().getStringExtra(ConstantUtil.macid);
        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        macid = (firmwareSharedPref.getString(ConstantUtil.macid, ""));
        LIC=(firmwareSharedPref.getString(ConstantUtil.licupdate,""));
        SERAIL=(firmwareSharedPref.getString(ConstantUtil.searilupdate,""));

        super.onResume();
    }

    @Override
    public void onPacketReceiveSuccess(String response, int modifiedPosition) {
        Log.i("textyaha4", "onPacketReceiveSuccess: "+response);

    }

    @Override
    public void onPacketReceiveError(String errorMessage, int modifiedPosition) {
        Log.i("textyaha3", "onPacketReceiveSuccess: "+errorMessage);

    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        Log.i("textyaha2", "onPacketReceiveSuccess: "+packets);

    }

    @Override
    public void onPacketReceived(String packets) {
        Log.i("textyaha1", "onPacketReceiveSuccess: "+packets);
        String cmd = Util.getJsonDataByField("cmd", packets);
        final String data = Util.getJsonDataByField("data", packets);
        if (cmd.equalsIgnoreCase(CommandUtil.SAVE_CREDENTIALS_CMD)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data.equals("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Save successfully", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Please Restart your Device ", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (data.equals("1")) {
                        Toast.makeText(getApplicationContext(), "could not save successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }

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
