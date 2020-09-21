package com.neotechindia.plugsmart.Utilility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.neotechindia.plugsmart.application.PlugSmartApplication;
import com.neotechindia.plugsmart.model.DeviceBean;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UdpHandler {

    static HashMap<String, DeviceBean> hashMap = new HashMap<>();

    public static void datagramsendPacket(final String packet, final Context context) {
        String Message;

        AsyncTask<Void, Void, Void> async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;

                try {

                    String ip = "255.255.255.255";
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    //  Message = wirelessMacId;
                    ConnectivityManager cm = (ConnectivityManager) PlugSmartApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
                    if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
                        ip = "192.168.43.255";
                    } else {
                        ip = "255.255.255.255";
                    }


                    //Log.i(TAG, "Wifi Status >>>>>>>>>>>>>>>>>");


                    InetAddress local = InetAddress.getByName(ip);
                    dp = new DatagramPacket(packet.getBytes(), packet.length(), local, 10007);
                    ds.setBroadcast(true);
                    ds.send(dp);
                    Log.i("sentUdp", packet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }

//    public static void UDP_Client_Send(String packet, Context context1) throws Exception {
//
//
//        try {
//            String ip = "255.255.255.255";
//
//            ConnectivityManager cm = (ConnectivityManager) PlugSmartApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
//            WifiManager wm = (WifiManager) context1.getSystemService(WIFI_SERVICE);
//
//            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
//                ip = "192.168.43.255";
//            } else {
//                ip = "255.255.255.255";
//            }
//            DatagramSocket ds = new DatagramSocket();
//            byte[] b = packet.getBytes();
//
//            InetAddress inetAddress = InetAddress.getLocalHost();
//            DatagramPacket datagramPacket = new DatagramPacket(b, b.length, inetAddress, 8099);
//            ds.send(datagramPacket);
//
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//
//    }===
//
//    public static void UDP_Receiver() throws Exception {
//        DatagramSocket ds = new DatagramSocket();
//        byte[] b1 = new byte[128];
//        DatagramPacket dp1 = new DatagramPacket(b1, b1.length);
//        ds.receive(dp1);
//
//        String getip = new String(dp1.getData(), 0, dp1.getLength());
//        Log.d("CheckIp", "" + getip);
//    }
//
//    //////////////////////////// Get Ip From Server ////////////////////////////////////////

//    public static void datagramsendPacketIp(final String packet, final Context context) {
//        String Message;
//
//        AsyncTask<Void, Void, Void> async_cient = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                // DatagramSocket ds = null;
//                boolean run=true;
//                DatagramSocket ds = null;
//                try {
//                 ds = new DatagramSocket(11011);
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//                try {
//
//                    String ip = "255.255.255.255";
//                    //ds = new DatagramSocket();
//                    DatagramPacket dp;
//                    //  Message = wirelessMacId;
//                    ConnectivityManager cm = (ConnectivityManager) PlugSmartApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
//                    // WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
//                    if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
//                        ip = "192.168.43.255";
//                    } else {
//                        ip = "255.255.255.255";
//                    }
//
//
//                    //Log.i(TAG, "Wifi Status >>>>>>>>>>>>>>>>>");
//
//
//                    InetAddress local = InetAddress.getByName(ip);
//                    //ds = new DatagramSocket(11011,local);
//                    dp = new DatagramPacket(packet.getBytes(), packet.length(), local, 8099);
//                    ds.setBroadcast(true);
//                    ds.send(dp);
//                    Log.i("sentUdp", packet);
//                    while (run) {
//                        try {
//                            byte[] msg = new byte[256];
//                            DatagramPacket packet = new DatagramPacket(msg,msg.length);
//                            Log.i("UDP client: ", "about to wait to receive");
//                            ds.setSoTimeout(1000);
//                            ds.receive(packet);
//                            String text = new String(msg, 0, msg.length);
//                            Log.d("Received text", text);
//                        } catch (SocketTimeoutException e) {
//                            //Log.e("Timeout Exception","UDP Connection:",e);
//                            run = false;
//                            ds.close();
//                        } catch (IOException e) {
//                           // Log.e(" UDP client has IOException", "error: ", e);
//                            run = false;
//                            ds.close();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } //finally {
////                    if (ds != null) {
////                        ds.close();
////                    }
//                //}
//                return null;
//            }
//
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//            }
//        };
//
//        if (Build.VERSION.SDK_INT >= 11)
//            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else async_cient.execute();
//    }


    public static void runUdpServer(final Context context) {
        AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                hashMap = new HashMap<>();
                byte[] lMsg = new byte[256];
                DatagramSocket ds = null;
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                try {
                    ds = new DatagramSocket(10006);
                } catch (Exception e) {
                    return null;
                }

                try {

                    while (true) {
                        ds.receive(dp);
                        String from = dp.getAddress().toString();
                        String mac = new String(dp.getData());
                        Log.i("recieveUdp", mac);
                        WriteJsonForUdpResponse(from, mac);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    /*if (ds != null) {
                        ds.close();
                    }*/
                }

                return null;
            }
        };


        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


////////////////// Receive Ip From server 8098//////////////////


//    public static void runUdpServerIp(final Context context) {
//        AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                boolean run=true;
//                hashMap = new HashMap<>();
//                byte[] lMsg = new byte[256];
//                DatagramSocket ds = null;
//                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
//                try {
//                    ds = new DatagramSocket(11012);
//                } catch (Exception e) {
//                    return null;
//                }
//
//                try {
////                    ds.setSoTimeout(10000);
////                    ds.close();
//                    while (run) {
//                        ds.setSoTimeout(5000);
//                        ds.receive(dp);
//                        String from = dp.getAddress().toString();
//                        String mac = new String(dp.getData());
//                        Log.i("recieveUdp", mac);
////                        WriteJsonForUdpResponse(from, mac);
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    run= false;
//                    ds.close();
//                } finally {
////                    if (ds != null) {
////                        ds.close();
////                    }
//                }
//
//                return null;
//            }
//        };
//
//
//        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//    }
//

    public static HashMap<String, DeviceBean> getList() {
        return hashMap;
    }

    private static void WriteJsonForUdpResponse(String from, String jsonPacket) {
        //{"sid":"9000","sad":"x","cmd":"8219","data":{"1":"d0:fa:1d:50:1b:4b","2":"Apurvpiestf","3":"192.168.1.29"}}
        //// String getIpPacket = "{\"sid\":\"9000\",\"sad\":\"x\",\"cmd\":\"8219\"}";
        //{"User":[{"name":"Apurvpiestf","ip":"192.168.1.29","macid":"d0:fa:1d:50:1b:4b"}]}
        try {
            String cmd = JsonUtil.getJsonDataByField("cmd", jsonPacket);
            String data = JsonUtil.getJsonDataByField("data", jsonPacket);
            Log.i("tester",data);
            if (data != null) {
                /*         if (!hashMap.containsKey(from)) {*/
                if (Logger.isPlugSmart) {

                    DeviceBean deviceData = new DeviceBean();
//                        deviceData.ip = "192.168.6.105";
                    deviceData.ip = JsonUtil.getJsonDataByField("3", data);
                    deviceData.macId = JsonUtil.getJsonDataByField("1", data);
                    deviceData.name = JsonUtil.getJsonDataByField("2", data);

                    String status = JsonUtil.getJsonDataByField("5", data);
                    String nos = JsonUtil.getJsonDataByField("6",data);
                    if(nos.contains("&")) {
                        String[] k = nos.split("&");
                        Log.i("tester1", "size "+k.length);
                        Log.i("tester1", "pos "+k[0]+" "+k[1]);
//                        if(k.length>1){
                        deviceData.iconType = k[0];
                        deviceData.LicenceType = k[1];
//                        }
                    }
                    if (status.equalsIgnoreCase("t")) {
                        deviceData.status = "#cce5cc";
                    } else {
                        deviceData.status = "#ffecef";
                    }



                    if (deviceData.ip.equalsIgnoreCase("**"))
                    {
                        deviceData.status = "#FFF0D7DB";
                    }
                    else if (deviceData.ip.equalsIgnoreCase("##"))
                    {
                        deviceData.status = "#FFF0D7DE";
                    }


                    int on = Integer.parseInt(JsonUtil.getJsonDataByField("4", data));
                    if (on == 1) {
                        deviceData.switchs = true;
                        deviceData.source = "on";
                    } else {
                        deviceData.switchs = false;
                        deviceData.source = "off";
                    }
                    hashMap.put(from, deviceData);
                } else {
                    DeviceBean deviceData = new DeviceBean();
                    deviceData.ip = JsonUtil.getJsonDataByField("03", data);
                    deviceData.macId = JsonUtil.getJsonDataByField("01", data);
                    deviceData.name = JsonUtil.getJsonDataByField("02", data);
                    String status = JsonUtil.getJsonDataByField("05", data);
                    if (status.equals("t")) {
                        deviceData.status = "#cce5cc";
                    } else {
                        deviceData.status = "#ffecef";
                    }
                    int on = Integer.parseInt(JsonUtil.getJsonDataByField("04", data));
                    if (on == 1) {
                        deviceData.switchs = true;
                        deviceData.source = "on";
                    } else {
                        deviceData.source = "off";
                    }
                    if (!Logger.isPlugSmart) {
                        try {
                            deviceData.iconType = JsonUtil.getJsonDataByField("06", data);
                            String nos = JsonUtil.getJsonDataByField("6",data);
                            if(nos.contains("&")) {
                                String[] k = nos.split("&");
                                Log.i("tester1", "size "+k.length);
                                Log.i("tester1", "pos "+k[0]+" "+k[1]);
//                                if(k.length>1){
                                deviceData.iconType = k[0];
                                deviceData.LicenceType = k[1];
//                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    hashMap.put(from, deviceData);
                }
                /*} else {
                    hashMap.remove(from);
                    DeviceBean deviceData = new DeviceBean();
                    deviceData.ip = JsonUtil.getJsonDataByField("3", data);
                    deviceData.macId = JsonUtil.getJsonDataByField("1", data);
                    deviceData.name = JsonUtil.getJsonDataByField("2", data);
                    boolean status = Boolean.parseBoolean(JsonUtil.getJsonDataByField("5", data));
                    if (status) {
                        deviceData.status = "#f1fdf9";
                    } else {
                        deviceData.status = "#fceaea";
                    }
                    deviceData.switchs = Boolean.parseBoolean(JsonUtil.getJsonDataByField("4", data));
                    if (deviceData.switchs) {
                        deviceData.source = "on";
                    } else {
                        deviceData.source = "off";
                    }
                    hashMap.put(from, deviceData);

                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
