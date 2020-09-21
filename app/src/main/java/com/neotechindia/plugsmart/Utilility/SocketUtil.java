package com.neotechindia.plugsmart.Utilility;

import android.content.Context;
import android.util.Log;

import com.neotechindia.plugsmart.requestor.SocketRequester;

import java.io.DataOutputStream;
import java.net.Socket;

public class SocketUtil {
    public static DataOutputStream dataOutputStream;

    public static boolean write(final String data, final String ip, Socket clientSocket, final String mac, final int position, final Context context) {
        try {
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
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
                            Log.e("error", "Error while Sending..........DATA" + e.getLocalizedMessage());

                            new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
                      /*      try {
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
                            }*/

                        }
                        Log.d("Wifi BroadCast", "SendingDATA  " + data + "\n\n");
                    }
                }).start();
            } else {

                new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
               /* connectionCreated = false;
                connecting = false;
                dataOutputStream.close();
                inputStream.close();
                clientSocket.close();
                Thread.sleep(3000);
                isWifiConnected(null);*/
            }
        } catch (Exception e) {
            e.printStackTrace();

            new Thread(new SocketRequester(context, ip, 9998, mac, position)).start();
         /*   Log.e(TAG, "Error while Sending..........DATA" + e.getLocalizedMessage());
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
            }*/


        }
        return false;

    }
}
