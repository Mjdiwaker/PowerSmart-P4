package com.neotechindia.plugsmart.Utilility;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDP_Client implements Runnable {
    String packet;

    public UDP_Client(String packet) {
        this.packet = packet;
    }

    @Override
    public void run() {

        boolean run = true;
        try {
            String ip = "255.255.255.255";
            DatagramSocket udpSocket = new DatagramSocket(1261);
            InetAddress serverAddr = InetAddress.getByName(ip);
            byte[] buf = packet.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 8099);
            try {
                udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (run) {
                try {
                    byte[] message = new byte[128];
                    DatagramPacket packet1 = new DatagramPacket(message, message.length);
                    Log.i("UDP client: ", "about to wait to receive");
                    udpSocket.setSoTimeout(10000);
                    udpSocket.receive(packet1);
                    String text = new String(message, 8, packet1.getLength());
                    //String text = new String(message, 0, packet1.getLength());
                    Log.d("Received text", text);
                    int size=text.length();
                    int count=0;
                    int lastindex=text.lastIndexOf(".");

                    String internetP=text.substring(0,lastindex);

                    Log.d("finalIp", "run: "+internetP);

                } catch (SocketTimeoutException e) {
                    Log.e("Timeout Exception", "UDP Connection:", e);
                    run = false;
                    udpSocket.close();
                } catch (IOException e) {
                    // Log.d(" UDP client has IOException", "error: ", e);
                    run = false;
                    udpSocket.close();
                }
            }
        } catch (SocketException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

