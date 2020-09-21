package com.neotechindia.plugsmart.receiver;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.util.Log;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.UDP_Client;
import com.neotechindia.plugsmart.Utilility.UdpHandler;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.adapter.SettingListAdapter;
import com.neotechindia.plugsmart.application.PlugSmartApplication;
import com.neotechindia.plugsmart.listeners.ConnectionListener;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.model.DeviceBean;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyWifiReceiver extends BroadcastReceiver {

    public static boolean deviceAttached = false; //indicates device is attached or not
    public static boolean connectionCreated = false;
    public static boolean connecting = false;
    public static SQLiteDatabase sqLiteDbObj;
    public static Socket clientSocket;
    // To handle Socket broken pipe Exception during Write
    public static boolean socketExceptionDuringWrite = false;
    //static InputStream inputStream;
    public static DataOutputStream dataOutputStream;
    public static InputStream inputStream = null;
    private static Context mcontext;
    private static Context mcontext1;
    public static boolean checkbtn_press;
    public static String TAG = MyWifiReceiver.class.getSimpleName();

    static StringBuffer sBuffer = new StringBuffer(2048);

    static LinkedHashMap<String, String> requestHashMap = new LinkedHashMap<>();

    @Override
    public void onReceive(final Context context, Intent intent) {
        mcontext = context;
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        //Log.i(TAG, "Wifi Status >>>>>>>>>>>>>>>>>");
        if (info != null && info.isConnected()) {
            //To check the Network Name or other info:
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = wifiInfo.getSSID();
            if (ssid.contains(ConstantUtil.SSID) || ssid.contains(ConstantUtil.SSID1)) {
                ConstantUtil.isForAPModeOnly = true;
                SharedPreferences firmwareSharedPref = mcontext.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(ConstantUtil.AP_MODE_SSID, ssid);
                editor.commit();
                connectUsingSocket();
            } else {
                startUdp();
                getIp();
            }
        } else {
            startUdp();
            getIp();
        }
    }

    private static void startUdp() {
        UdpHandler.runUdpServer(mcontext);
        String getIpPacket = "{\"sid\":\"9000\",\"sad\":\"x\",\"cmd\":\"8219\",\"data\":\"" + ConstantUtil.getAutoFillString(ConstantUtil.getDeviceUuid(mcontext), 16, "0").toString() + "\"}";
        UdpHandler.datagramsendPacket(getIpPacket, mcontext);
        timer();
    }


    private static void getIp() {

        Log.i("shubham11", "getIp: shubham11");
        String packet = "90008192";
       // UdpHandler.datagramsendPacketIp(getIp, mcontext1);
       //UdpHandler.runUdpServerIp(mcontext1);
        Thread thread= new Thread(new UDP_Client(packet));
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();



    }

    private static CountDownTimer countDownTimer;

    private static void timer() {

        countDownTimer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                String getIpPacket = "{\"sid\":\"9000\",\"sad\":\"x\",\"cmd\":\"8219\",\"data\":\"" + ConstantUtil.getAutoFillString(ConstantUtil.getDeviceUuid(mcontext), 16, "0").toString() + "\"}";
                UdpHandler.datagramsendPacket(getIpPacket, mcontext);
                Log.d("", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("", "Finish progress bar!");
                UdpHandler.getList();
                ArrayList<DeviceBean> arrayList = new ArrayList<DeviceBean>();
                Iterator it = UdpHandler.getList().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    arrayList.add((DeviceBean) pair.getValue());
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    // it.remove(); // avoids a ConcurrentModificationException
                }
                ConstantUtil.CONNECTED_NETWORK_MODE = ConstantUtil.INetworkMode.CONNECTED_NETWORK_STATION_MODE;
                ConstantUtil.isForAPModeOnly = false;
                if (arrayList.size() != 0) {
                    mConnectionListener = ConstantUtil.connectionListener;
                    mConnectionListener.onWifiConnectStatus(true, arrayList);
                    connectionCreated = true;
                }
                //  UdpHandler.StopListeningAsync();
                countDownTimer = null;
            }
        }.start();
    }

    public static boolean isWifiConnected(Context context) {
        mcontext = context;
        ConnectivityManager cm = (ConnectivityManager) PlugSmartApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            final WifiManager wifiManager = (WifiManager) PlugSmartApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();

            if (ssid.contains(ConstantUtil.SSID) || ssid.contains(ConstantUtil.SSID1)) {
                ConstantUtil.isForAPModeOnly = true;
                connectUsingSocket();
            } else {
                startUdp();
                getIp();
            }
        } else {
            startUdp();
            getIp();

        }


        return false;
    }

    static public void connectUsingSocket() {
        connecting = true;
        //create a socket
        try {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!connectionCreated) {
                            clientSocket = new Socket(ConstantUtil.IBuildConstant.IP, ConstantUtil.IBuildConstant.PORT);
                            clientSocket.setKeepAlive(true);
                            clientSocket.setSoTimeout(ConstantUtil.SOCKET_TIMEOUT);
                            connectionCreated = clientSocket.isConnected();
                            Log.i("SocketCreated", "SocketCreated");
                            if (connectionCreated) {
                                retryReqFromHashMap();

                                mConnectionListener = ConstantUtil.connectionListener;
                                mConnectionListener.onWifiConnectStatus(true);
                                ConstantUtil.CONNECTED_NETWORK_MODE = ConstantUtil.CONNECTED_NETWORK_AP_MODE;
                                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                                //retryReqFromHashMap();
                            } else {
                                mConnectionListener = ConstantUtil.connectionListener;
                                mConnectionListener.onWifiConnectStatus(true);
                                connectionCreated = false;
                            }
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                            byte[] buffer = new byte[2048];
                            int bytesRead;
                            try {
                                while (true) {
                                    byteArrayOutputStream.reset();
                                    inputStream = clientSocket.getInputStream();
                                    if (inputStream != null) {
                                        if (clientSocket.isConnected() && !clientSocket.isInputShutdown()) {
                                            bytesRead = inputStream.read(buffer);
                                            if (bytesRead < 0) {
                                                connectionCreated = false;
                                                connecting = false;
                                                dataOutputStream.close();
                                                inputStream.close();
                                                clientSocket.close();
                                                Thread.sleep(3000);
                                                sBuffer = new StringBuffer(2048);
                                                isWifiConnected(null);
                                            } else {
                                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                                                String response = byteArrayOutputStream.toString("UTF-8");
                                                try {
                                                    // Run loop to get 1 packet at a time. This string may have multiple responses.

                                                    String st = response;
                                                    st = st.trim();
                                                    Log.i("receive", st.toString());
                                                    if ((st.length() > 6 && (!st.contains("{") && !st.contains("}"))) || (st.contains("~") && st.contains("^") && (!st.contains("{") && !st.contains("}")))) {
                                                        if (st.contains("~") && st.contains("^") && (!st.contains("{") || !st.contains("}"))) {
                                                            st = st.substring(0, 8);
                                                        } else {
                                                            st = st.substring(0, 6);
                                                        }
                                                    }
                                                    if (st.length() == 6 && (!st.contains("{") || !st.contains("}"))) {
                                                        updateOnUi(st);
                                                    } else if (st.length() == 8 && (!st.contains("{") || !st.contains("}")) && st.contains("~") && st.contains("^")) {
                                                        updateOnUi(st);
                                                    } else if (st.length() == 0) {
                                                        return;
                                                    } else {
                                                        if (st.contains("{\"sid\":\"9000\"") && !st.contains(",{\"sid\":\"9000\"")) {
                                                            sBuffer = new StringBuffer(2048);
                                                        }
                                                        if (st.length() <= 200 && JsonUtil.isJSONValid(st)) {
                                                            String cmd = Util.getJsonDataByField("cmd", st);
                                                            requestHashMap.remove(cmd);
                                                            updateOnUi(st);
                                                            sBuffer = new StringBuffer(2048);
                                                        } else {
                                                            sBuffer.append(st);
                                                            Log.i("scheduleCheck", sBuffer.toString());
                                                            if (sBuffer.toString().startsWith("{{") && sBuffer.toString().endsWith("}}")) {
                                                                String cmd = Util.getJsonDataByField("cmd", sBuffer.toString());
                                                                requestHashMap.remove(cmd);
                                                                updateOnUi(sBuffer.toString());
                                                                sBuffer = new StringBuffer(2048);
                                                            } else if (JsonUtil.isJSONValid(sBuffer.toString())) {
                                                                String cmd = Util.getJsonDataByField("cmd", sBuffer.toString());
                                                                requestHashMap.remove(cmd);

                                                                updateOnUi(sBuffer.toString());
                                                                sBuffer = new StringBuffer(2048);
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            connectionCreated = false;
                                            connecting = false;
                                            dataOutputStream.close();
                                            inputStream.close();
                                            clientSocket.close();
                                            Thread.sleep(3000);
                                            sBuffer = new StringBuffer(2048);
                                            isWifiConnected(null);
                                        }
                                    } else {
                                        connectionCreated = false;
                                        connecting = false;
                                        dataOutputStream.close();
                                        inputStream.close();
                                        clientSocket.close();
                                        Thread.sleep(3000);
                                        sBuffer = new StringBuffer(2048);
                                        isWifiConnected(null);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                connectionCreated = false;
                                connecting = false;
                                try {
                                    dataOutputStream.close();
                                    inputStream.close();
                                    clientSocket.close();
                                    Thread.sleep(3000);
                                    sBuffer = new StringBuffer(2048);
                                    // // TODO: 1/13/2017 for time being we are passing null here after we wil remove this
                                    isWifiConnected(null);
                                } catch (Exception e1) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mConnectionListener = ConstantUtil.connectionListener;
                            mConnectionListener.onWifiConnectStatus(true);
                        }
                    } catch (Exception e) {
                        Log.i("socketcreateprob", e.toString());
                        connectionCreated = false;
                        connecting = false;
                        try {
                            dataOutputStream.close();
                            inputStream.close();
                            clientSocket.close();
                            Thread.sleep(3000);
                            sBuffer = new StringBuffer(2048);
                            // // TODO: 1/13/2017 for time being we are passing null here after we wil remove this
                            isWifiConnected(null);
                        } catch (Exception e1) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            connectionCreated = false;
            connecting = false;
            try {
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
                sBuffer = new StringBuffer(2048);
                // // TODO: 1/13/2017 for time being we are passing null here after we wil remove this
                isWifiConnected(null);
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }
    }

    public static boolean write(final String data, String commandReq) {
        try {

            socketExceptionDuringWrite = false;
            String cmd = "";

            if (commandReq.equals("7777"))                                                  //shubham
            {
                checkbtn_press = true;
            }

            if (data.contains("~") && data.contains("^") && (!data.contains("{") || !data.contains("}"))) {

            } else {
                cmd = Util.getJsonDataByField("cmd", data);
            }

            if (ConstantUtil.isFromSplash) {
                requestHashMap.clear();

            }
            if (commandReq.equalsIgnoreCase("")) {
            } else {

                requestHashMap.put(commandReq, data);


            }
            if (clientSocket != null && clientSocket.isConnected() && dataOutputStream != null && !clientSocket.isOutputShutdown()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (data.contains("~") && data.contains("^") && (!data.contains("{") || !data.contains("}"))) {
                                dataOutputStream.write(data.getBytes());
                                dataOutputStream.flush();
                            } else {
                                dataOutputStream.write(data.getBytes());
                                dataOutputStream.flush();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error while Sending..........DATA" + e.getLocalizedMessage());
                            try {
                                socketExceptionDuringWrite = true;
                                connectionCreated = false;
                                connecting = false;
                                dataOutputStream.close();
                                inputStream.close();
                                clientSocket.close();
                                sBuffer = new StringBuffer(2048);
                                Thread.sleep(3000);
                            } catch (Exception excep) {
                                excep.printStackTrace();
                            } finally {
                                isWifiConnected(null);
                            }

                        }
                        Log.d("Wifi BroadCast", "SendingDATA  " + data + "\n\n");
                    }
                }).start();
            } else {
                connectionCreated = false;
                connecting = false;
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
                isWifiConnected(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while Sending..........DATA" + e.getLocalizedMessage());
            try {
                connectionCreated = false;
                connecting = false;
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
            } catch (Exception e1) {
                e.printStackTrace();
            } finally {
                isWifiConnected(null);
            }


        }
        return false;

    }


    private static int position;

    public static boolean write(final String data, String commandReq, int modifiedPosition) {
        position = modifiedPosition;
        try {

            socketExceptionDuringWrite = false;
            String cmd = "";

            if (data.contains("~") && data.contains("^") && (!data.contains("{") || !data.contains("}"))) {

            } else {
                cmd = Util.getJsonDataByField("cmd", data);
            }

            if (ConstantUtil.isFromSplash) {
                requestHashMap.clear();

            }
            if (commandReq.equalsIgnoreCase("")) {
            } else {

                requestHashMap.put(commandReq, data);


            }
            if (clientSocket != null && clientSocket.isConnected() && dataOutputStream != null && !clientSocket.isOutputShutdown()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (data.contains("~") && data.contains("^") && (!data.contains("{") || !data.contains("}"))) {
                                dataOutputStream.write(data.getBytes());
                                dataOutputStream.flush();
                            } else {
                                dataOutputStream.write(data.getBytes());
                                dataOutputStream.flush();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error while Sending..........DATA" + e.getLocalizedMessage());
                            try {
                                socketExceptionDuringWrite = true;
                                connectionCreated = false;
                                connecting = false;
                                dataOutputStream.close();
                                inputStream.close();
                                clientSocket.close();
                                sBuffer = new StringBuffer(2048);
                                Thread.sleep(3000);
                            } catch (Exception excep) {
                                excep.printStackTrace();
                            } finally {
                                isWifiConnected(null);
                            }

                        }
                        Log.d("Wifi BroadCast", "SendingDATA  " + data + "\n\n");
                    }
                }).start();
            } else {
                connectionCreated = false;
                connecting = false;
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
                isWifiConnected(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while Sending..........DATA" + e.getLocalizedMessage());
            try {
                connectionCreated = false;
                connecting = false;
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
            } catch (Exception e1) {
                e.printStackTrace();
            } finally {
                isWifiConnected(null);
            }


        }
        return false;

    }

    static void retryReqFromHashMap() {
        for (Map.Entry<String, String> entry : requestHashMap.entrySet()) {
            write(entry.getValue(), entry.getKey());
            break;
        }
    }

    private static IUpdateUiListener mUpdateListener;
    private static ConnectionListener mConnectionListener;
    private static SettingListAdapter.OnListItemClickCallBack onListItemClickCallBack;

    private static IPacketReceiveCallBack packetReceiveCallBack;

    public static void setOnPacketReceiveCallBack(IPacketReceiveCallBack mPacketReceiveCallBack) {
        packetReceiveCallBack = mPacketReceiveCallBack;
    }


    private static void updateOnUi(String st) {
        if (st.length() == 6 && (!st.contains("{") || !st.contains("}"))) {
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st);

            packetReceiveCallBack.onPacketReceiveSuccess(st, position);

        } else if (st.length() == 8 && (!st.contains("{") || !st.contains("}")) && st.contains("~") && st.contains("^")) {
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st);
            packetReceiveCallBack.onPacketReceiveSuccess(st, position);
        } else {
            final String cmd = Util.getJsonDataByField("cmd", st);
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st);
            packetReceiveCallBack.onPacketReceiveSuccess(st, position);

        }

    }


}
