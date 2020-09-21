package com.neotechindia.plugsmart.activity;

import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kyleduo.switchbutton.SwitchButton;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;
import com.neotechindia.plugsmart.requestor.SocketRequester;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Weather extends AppCompatActivity implements IUpdateUiListener, IPacketReceiveCallBack {
    SwitchButton aSwitch;
    TextView editText;
    Button button;
    String macid,serial_no,lic_no;
    private boolean user_switch = false;
    String url= "http://192.168.6.105:8080/KnowNGrowPro/rest/checkSub";
    String ipWheather="";
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //Toolbaar

      //  toolbar.setTitle("Weather");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        aSwitch = findViewById(R.id.sb_schedule1);
        editText=findViewById(R.id.enterweatherip);
        button=findViewById(R.id.btn_weathersubmit);
        button.setBackgroundColor(Color.parseColor("#C6E2FF"));

        ConstantUtil.updateListener = this;
        MyWifiReceiver.setOnPacketReceiveCallBack(this);
        SocketRequester.setOnPacketReceiveCallBack(this);
        MyWifiReceiver.write(PacketsUrl.getWeather(), CommandUtil.cmd_get_weather);
        serial_no = getIntent().getStringExtra(ConstantUtil.serialNumber);
        macid = getIntent().getStringExtra(ConstantUtil.macid);

        SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
        macid = (firmwareSharedPref.getString(ConstantUtil.macid, ""));
        serial_no= (sharedPreferences.getString(ConstantUtil.serialNumber, ""));
        lic_no=  (sharedPreferences.getString(ConstantUtil.liscenceNumber, ""));
        //response
        //ConstantUtil.ip = "";
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                      fetchipforweather();

            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                 //   Toast.makeText(Weather.this, "Weather is On", Toast.LENGTH_SHORT).show();
                    MyWifiReceiver.write(PacketsUrl.setWeather("1",ipWheather), CommandUtil.cmd_get_weather);
                  user_switch = false;

                } else {

                    MyWifiReceiver.write(PacketsUrl.setWeather("0",""),CommandUtil.cmd_get_weather);
              //      Toast.makeText(Weather.this, "Weather is Off", Toast.LENGTH_SHORT).show();
                   user_switch = false;
                }
               // MyWifiReceiver.write(PacketsUrl.getWeather(), CommandUtil.cmd_get_weather);
            }
        });
    }

    @Override
    public void onPacketReceived(String packets, String mac) {
        Log.i("nun","4 "+packets );
    }

    @Override
    public void onPacketReceived(String packets) {
       Log.i("nun","wheater "+packets );

        String cmd = JsonUtil.getJsonDataByField("cmd", packets);
        final String data = JsonUtil.getJsonDataByField("data", packets);
        final String on_res = JsonUtil.getJsonDataByField("02",data);
        if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_weather)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(on_res.equalsIgnoreCase("1")){
                            aSwitch.setCheckedNoEvent(true);
                            user_switch=true;
                            Toast.makeText(Weather.this, "wheater is On", Toast.LENGTH_SHORT).show();
                        } else if (on_res.equalsIgnoreCase("0")){
                          //  Toast.makeText(Weather.this, "wheater is Off", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
//            if(on_res.equals("1")){
//                Toast.makeText(this, "wheater is On", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this, "Wheater is Off", Toast.LENGTH_SHORT).show();
//            }

        }
//        Log.i("nun","in "+on_res);
//        if(user_switch){
//            Log.i("nun","1.1 ");
//            if(on_res.equals("1")){
//                Log.i("nun","1.2 ");
//
//               // no need to set
//            }else{
//                Log.i("nun","1.3 ");
//                //set weather weather is off
////                if (ConstantUtil.ip!=null) {//TODO:
//                   // MyWifiReceiver.write(PacketsUrl.setWeather("1", "192.168.1.1"), CommandUtil.cmd_get_weather);
////                }else{
////                    Toast.makeText(this, "ip is null", Toast.LENGTH_SHORT).show();
////                }
//            }
//        }else{
//            if(on_res.equals("1")){
//                Log.i("nun","1.4 ");
//                //set weather to off weather is on
//              //  MyWifiReceiver.write(PacketsUrl.setWeather("0",""),CommandUtil.cmd_get_weather);
//            }else{
//                Log.i("nun","1.5 ");
//                // no need to do anthing
//            }
//        }
    }

    @Override
    public void onPacketReceiveSuccess(String response, int modifiedPosition) {
        Log.i("nun","1 "+response );


    }

    @Override
    public void onPacketReceiveError(String errorMessage, int modifiedPosition) {
        Log.i("nun","2 "+errorMessage );
    }

    public void fetchipforweather(){




        RequestQueue requestQueue= Volley.newRequestQueue(Weather.this);

        HashMap<String,String> params = new HashMap<String, String>();
        params.put("mac_id",macid);
        params.put("serial_no",serial_no);
        params.put("lic_no",lic_no);

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                try {
                    JSONObject wheatherip = new JSONObject(String.valueOf(response));
                    String ip = wheatherip.getString("edgeIP");
                    if (!ip.equalsIgnoreCase("null")){
                        ipWheather=ip;
                        editText.setText(ip);
                    }else {
                        Toast.makeText(Weather.this, "You Don't have Subscription", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);



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
