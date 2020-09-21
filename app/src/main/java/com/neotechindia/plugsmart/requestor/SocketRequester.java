package com.neotechindia.plugsmart.requestor;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.Utilility.JsonUtil;
import com.neotechindia.plugsmart.Utilility.Util;
import com.neotechindia.plugsmart.application.PlugSmartApplication;
import com.neotechindia.plugsmart.listeners.IPacketReceiveCallBack;
import com.neotechindia.plugsmart.listeners.ISocketConnection;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketRequester implements Runnable {
    String ip;
    int port;
    Context context;
    String mac;
    public static InputStream inputStream = null;
    private static Context mcontext;
    static StringBuffer sBuffer = new StringBuffer(2048);
    boolean isSocketActive = false;
    private int count = 0;
    private ISocketConnection iSocketConnection;
    private int position;
    private static int pos;

    public SocketRequester(Context context, String ip, int port, String mac, int position) {
        this.iSocketConnection = iSocketConnection;
        this.position = position;
        this.context = context;
        this.port = port;
        this.ip = ip;
        this.mac = mac;
      /*  if (hashMap != null) {

        } else {
            hashMap = new HashMap<>();
        }
*/
    }

    /* public static HashMap<String, Socket> getHashList() {
         return hashMap;
     }*/
    public static void setPosition(int position) {
        pos = position;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(ConstantUtil.SOCKET_TIMEOUT);
            boolean isConnectedSocket = socket.isConnected();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[2048];
            int bytesRead;
            if (isConnectedSocket) {
                // iSocketConnection.onSocketConnect(position++);
                isSocketActive = true;
                PlugSmartApplication.getInstance().hashMap.put(mac, socket);
                SharedPreferences firmwareSharedPref = context.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firmwareSharedPref.edit();
                editor.putString(mac, socket.toString());
                editor.commit();
                try {
                    while (isSocketActive) {
                        byteArrayOutputStream.reset();
                        inputStream = socket.getInputStream();
                        if (inputStream != null) {
                            if (socket.isConnected() && !socket.isInputShutdown()) {
                                bytesRead = inputStream.read(buffer);
                                if (bytesRead < 0) {
                                } else {
                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                    String response = byteArrayOutputStream.toString("UTF-8");
                                    try {
                                        // Run loop to get 1 packet at a time. This string may have multiple responses.
                                        String st = response;
                                        Log.i("Socket Resquest Bobby", "run: "+st);

                                        st = st.trim();
                                        Log.i("receive", "got "+st.toString());
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
                                                updateOnUi(st);
                                                sBuffer = new StringBuffer(2048);
                                            } else {
                                                sBuffer.append(st);
                                                Log.i("scheduleCheck", sBuffer.toString());
                                                if (sBuffer.toString().startsWith("{{") && sBuffer.toString().endsWith("}}")) {
                                                    updateOnUi(sBuffer.toString());
                                                    sBuffer = new StringBuffer(2048);
                                                } else if (JsonUtil.isJSONValid(sBuffer.toString())) {
                                                    updateOnUi(sBuffer.toString());
                                                    sBuffer = new StringBuffer(2048);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //iSocketConnection.onSocketDisconnect(position);
                                        isSocketActive = false;
                                        new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
                                    }
                                }
                            } else {
                                // iSocketConnection.onSocketDisconnect(position);
                                isSocketActive = false;
                                new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
                            }

                        } else {
                            //iSocketConnection.onSocketDisconnect(position);
                            isSocketActive = false;
                            new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isSocketActive = false;
                    new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
                    //   iSocketConnection.onSocketDisconnect(position);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //iSocketConnection.onSocketDisconnect(position);
            isSocketActive = false;
            count++;
            if(count<6)
            {
                new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
            }

        }
    }

    private static IUpdateUiListener mUpdateListener;
    private static IPacketReceiveCallBack packetReceiveCallBack;

    public static void setOnPacketReceiveCallBack(IPacketReceiveCallBack mPacketReceiveCallBack) {
        packetReceiveCallBack = mPacketReceiveCallBack;
    }

    private void updateOnUi(String st) {
//        {"sid":"9000","sad":"x","cmd":"0777","data":{"01":"tttttt","02":"1"}}{"sid":"9000","sad":"x","cmd":"0777","data":{"01":"tttttt","02":"0"}}
        if (st.length() == 6 && (!st.contains("{") || !st.contains("}"))) {
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st, mac);
            packetReceiveCallBack.onPacketReceiveSuccess(st, pos);
        } else if (st.length() == 8 && (!st.contains("{") || !st.contains("}")) && st.contains("~") && st.contains("^")) {
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st, mac);
            packetReceiveCallBack.onPacketReceiveSuccess(st, pos);
        } else {
            final String cmd = Util.getJsonDataByField("cmd", st);
            mUpdateListener = ConstantUtil.updateListener;
            mUpdateListener.onPacketReceived(st, mac);
            packetReceiveCallBack.onPacketReceiveSuccess(st, pos);
        }

    }

}