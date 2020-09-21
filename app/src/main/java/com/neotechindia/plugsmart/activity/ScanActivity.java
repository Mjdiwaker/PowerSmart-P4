package com.neotechindia.plugsmart.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {

        try {
            JSONObject wheatherip = new JSONObject(String.valueOf(result));
            String Serial = wheatherip.getString("serialNo");
            String Lic = wheatherip.getString("licNo");
            String Model = wheatherip.getString("modelNo");

           UpdateLic.serail.setText(Serial);
           UpdateLic.lic.setText(Lic);
           UpdateLic.model.setText(Model);

            SharedPreferences firmwareSharedPref = getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.searilupdate,Serial);
            editor.putString(ConstantUtil.licupdate,Lic);
            editor.commit();



        } catch (JSONException e) {
            e.printStackTrace();
        }
        onBackPressed();

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

}
