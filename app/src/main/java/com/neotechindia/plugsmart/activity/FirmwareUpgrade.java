package com.neotechindia.plugsmart.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.CustomDialog;
import com.neotechindia.plugsmart.Utilility.Logger;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.constants.PacketsUrl;
import com.neotechindia.plugsmart.listeners.AquasmartDialogListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.receiver.MyWifiReceiver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FirmwareUpgrade extends AppCompatActivity implements IUpdateUiListener {
    private EditText et_Firmware;
    private int lineNumber;
    FileReader fileReader = null;
    BufferedReader bufferedReader;
    ArrayList<String> arrayList;
    Bundle bundlefirm;
    int key;
    int size, sentLineNumber, receivedLineNumber;
    private CountDownTimer countDownTimer, countDownTimer2;
    private String assetPath;
    private Activity mActivity;
    private boolean fileNotExist = false;
    ProgressDialog mProgressDialog;
    String serverVersion, oldVersion, serverVersionWithDot;
    NetworkInfo wifiNetwork;
    private boolean showProgressDialog = false;
    TextView tvIotVersion, tvappVersion;
    Button btnFirmware;
    ProgressDialog progressBar = null;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;
    public static String sendReqCmd = "";

    public void progressStatus(View v) {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage("Software Upgrading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(arrayList.size());
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
        progressBarStatus = 0;
        fileSize = 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_upgrade);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<String>();
        mProgressDialog = new ProgressDialog(this);
        btnFirmware = findViewById(R.id.btnFirmware);
        loadFileFromAssets(assetPath);
        if (arrayList.size() > 0) {
            // progressStatus(mFirmView);
        } else {
            loadFileFromAssets(assetPath);
            // progressStatus(mFirmView);
        }
        bundlefirm = null;///getArguments();
        if (bundlefirm != null) {
            key = bundlefirm.getInt(ConstantUtil.REG_KEY);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("Aqua_Logs", Context.MODE_PRIVATE);
        oldVersion = sharedPreferences.getString("Firmware_Version", "").replaceAll("[\\s.]", "");

        if (key == 8) {

            btnFirmware.setEnabled(false);
            //  btnFirmware.setBackground(getResources().getDrawable(R.drawable.ripple_grey));
            Timer timer = new Timer();
            receivedLineNumber = 0;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("test", "timer executed");
                    if (receivedLineNumber == sentLineNumber) {
                      /*  MyWifiReceiver.connectionCreated = false;
                        MyWifiReceiver.connecting = false;
                        MyWifiReceiver.isWifiConnected(getActivity());*/
                    } else {
                        receivedLineNumber = sentLineNumber;
                    }

                }
            }, 0, 10000);


        } else {
            compareVersions();
            //getting current Firmware version //
            // USBBroadcastReceiver.write(PacketsUrl.firmwareVersion());
        }
        /****************Displaying Current Version and Available version Start*********/

        tvIotVersion = findViewById(R.id.tvIotVersion);
        tvappVersion = findViewById(R.id.tvappVersion);
        tvIotVersion.setText(sharedPreferences.getString("Firmware_Version", ""));
        tvappVersion.setText(serverVersionWithDot);
        /****************Displaying Current Version and Available version End*********/
        btnFirmware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (serverVersion != null && oldVersion != null) {
                    int oldVer = Integer.parseInt(oldVersion.trim());
                    int serVer = Integer.parseInt(serverVersion.trim());
                    if (serVer > oldVer) {
                        if (progressBar != null && (!progressBar.isShowing())) {

                            progressBar.show();
                        }
                        sendReqCmd = CommandUtil.cmd_fw_update;
                        //sending request for firmware upgrade
                        MyWifiReceiver.write(PacketsUrl.firmwareUpdate("0", serverVersionWithDot), "");
                    } else {
                        Toast.makeText(FirmwareUpgrade.this, "Software already Up to date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sendReqCmd = CommandUtil.cmd_get_fw_version;
                    Toast.makeText(FirmwareUpgrade.this, "Try Again", Toast.LENGTH_SHORT).show();
                    MyWifiReceiver.write(PacketsUrl.firmwareVersion(), "");
                }


            }
        });
    }

    private void loadFileFromAssets(String version) {
        try {

            InputStream stream = getAssets().open(version);
            String line = null;
            int count = 0;
            if (stream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                while ((line = bufferedReader.readLine()) != null) {
                    count++;

                    arrayList.add(line);


                }

            } else {
                Toast.makeText(FirmwareUpgrade.this, "First Download the new Version", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void compareVersions() {
        if (serverVersion != null && oldVersion != null) {
            int oldVer = Integer.parseInt(oldVersion.trim());
            int serVer = Integer.parseInt(serverVersion.trim());
            if (serVer == oldVer) {
                btnFirmware.setEnabled(false);
                // btnFirmware.setBackground(getResources().getDrawable(R.drawable.ripple_grey));
            } else {
                btnFirmware.setEnabled(true);
                //  btnFirmware.setBackground(getResources().getDrawable(R.drawable.blue_single_button));
            }
        }
    }

    private void getLineFromFile(int lineNumber, String data) {


        size = arrayList.size();
        if (size == 0) {
            loadFileFromAssets(assetPath);
        }
        String arrayLine = "";
        if (size >= lineNumber) {
            arrayLine = arrayList.get(lineNumber);
            sentLineNumber = lineNumber;
            progressBarStatus = lineNumber;
            progressBar.setProgress(progressBarStatus);
        } else {

        }

        String completeData = "~" + data + arrayLine + "^";

        // do your code
        //writing packet to IOT (Line of File)
        MyWifiReceiver.write(completeData, "");


    }

    private void showDialogToRestartApp() {
        String msg;
        msg = "";
        CustomDialog.getInstance().showDialogOk(FirmwareUpgrade.this, "Alert!!!", msg, "OK", new AquasmartDialogListener() {


            @Override
            public void onButtonOneClick() {

                finishAffinity();

            }

            @Override
            public void onButtonTwoClick() {

            }

            @Override
            public void onButtonThreeClick(String password) {

            }
        });
    }

    private void timer2() {
        countDownTimer2 = new CountDownTimer(35000, 1000) {

            public void onTick(long millisUntilFinished) {
                Logger.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Logger.d("", "done!");
                MyWifiReceiver.clientSocket = null;
                MyWifiReceiver.connectionCreated = false;
                MyWifiReceiver.connecting = false;
                try {
                    MyWifiReceiver.dataOutputStream.close();
                    // MyWifiReceiver.dataOutputStream = null;
                    MyWifiReceiver.inputStream.close();
                    //MyWifiReceiver.inputStream = null;
                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    MyWifiReceiver.dataOutputStream = null;
                    MyWifiReceiver.inputStream = null;
                }

                MyWifiReceiver.isWifiConnected(FirmwareUpgrade.this);
                // Timer to check delay in packets, if brocken pipe restart socket
                Timer timer = new Timer();
                receivedLineNumber = 0;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i("test", "timer executed");
                        if (receivedLineNumber == sentLineNumber) {
                            MyWifiReceiver.clientSocket = null;
                            MyWifiReceiver.connectionCreated = false;
                            MyWifiReceiver.connecting = false;
                            try {
                                MyWifiReceiver.dataOutputStream.close();
                                MyWifiReceiver.inputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                MyWifiReceiver.dataOutputStream = null;
                                MyWifiReceiver.inputStream = null;
                            }

                            MyWifiReceiver.isWifiConnected(FirmwareUpgrade.this);
                        } else {
                            receivedLineNumber = sentLineNumber;
                        }

                    }
                }, 0, 10000);
                countDownTimer2 = null;

            }
        }.start();
    }

    @Override
    public void onPacketReceived(String packets, String mac) {

    }

    @Override
    public void onPacketReceived(String packets) {
        String cmd = "";
        if (packets.length() == 6 && (!packets.contains("{") || !packets.contains("}"))) {

        } else if (packets.length() == 8 && (!packets.contains("{") || !packets.contains("}")) && packets.contains("~") && packets.contains("^")) {

        } else {
            cmd = Util.getJsonDataByField("cmd", packets);
        }
        CharsetEncoder asciiEncoder =
                Charset.forName("US-ASCII").newEncoder();

        boolean isAscii = asciiEncoder.canEncode("000001");
        if (cmd.equalsIgnoreCase("")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressBar != null && (!progressBar.isShowing())) {

                        progressBar.show();
                    }
                }
            });


            if (packets.trim().equalsIgnoreCase("~000000^")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressBar != null && progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                        showDialogToRestartApp();
                        // Toast.makeText(getActivity(), "Please Restart the AquaSmart App.Also detach and attach the USB if done through USb", Toast.LENGTH_LONG).show();
                    }
                });
                // getActivity().finishAffinity();


            } else if (packets.trim().equalsIgnoreCase("~ffffff^")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (progressBar != null && progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                        Toast.makeText(FirmwareUpgrade.this, "Software Update Failed,Please try Again", Toast.LENGTH_SHORT).show();
                    }
                });


            } else {

                lineNumber = Integer.parseInt(packets.trim());


                getLineFromFile(lineNumber - 1, packets.trim());
            }


        } else {

            String data = Util.getJsonDataByField("data", packets);

            final String versionNumber = data;
            Logger.d("", "onPacketReceived in  FRAGMENT  cmd=" + cmd + "    " + packets);
            if (cmd.equalsIgnoreCase(CommandUtil.cmd_get_fw_version) && cmd.equalsIgnoreCase(sendReqCmd)) {
                sendReqCmd="";
                oldVersion = data.replaceAll("[\\s.]", "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });


            } else if (cmd.equalsIgnoreCase(CommandUtil.cmd_fw_update) && cmd.equalsIgnoreCase(sendReqCmd)) {
                sendReqCmd = "";
                if (data.equalsIgnoreCase("0")) {
                    Intent intent = new Intent("stopCallback");
                    LocalBroadcastManager.getInstance(FirmwareUpgrade.this).sendBroadcast(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer2();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            }
        }

    }
}
