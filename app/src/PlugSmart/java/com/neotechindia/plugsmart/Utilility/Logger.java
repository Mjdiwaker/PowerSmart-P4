package com.neotechindia.plugsmart.Utilility;

import android.os.Environment;
import android.util.Log;


import com.neotechindia.plugsmart.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class Logger {



    public static boolean isShowLog = BuildConfig.DEBUG;
    public static boolean isShowDummy = BuildConfig.DEBUG;


    public static final String TAG = "Logger";
    public static final String TAG_FIle = "Error";
    public static boolean isPlugSmart=true;
    public static boolean isPlugSmartMCU=false;


    public static void d(String tag, String message) {
        if (isShowLog) {
            Log.d(TAG + " " + tag, message);
        }

    }

    public static void wrtOnFil(String tag, String message, String time) {
        if (isShowLog) {
            Log.d(TAG + " " + tag, message);

            try {
                WriteToFileExample.writeLogsInFile("", message, time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void wrtAquaDataOnFile(String serialNumber, String name, String mobileNumber,String time, String licenseNumber,String macId) {

        Log.d(TAG + serialNumber + name, mobileNumber+ time+licenseNumber + macId);

        try {
            WriteToFileExample.writeSerialNumberInFile(serialNumber, name, mobileNumber,time,licenseNumber,macId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readUserDataFromFile() {
        String userData = "";
        try {
            userData = WriteToFileExample.readDataFromFile();
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static void v(String tag, String message, String time) {
        if (isShowLog) {
            Log.v(TAG + " " + tag, message);
            WriteToFileExample.writeLogsInFile(tag + " Logger.v", message, time);
        }
    }

    public static void e(String tag, String message) {
        if (isShowLog)
            Log.e(TAG + " " + TAG_FIle + " " + tag, message);

    }

    public static void i(String tag, String message, String time) {
        if (isShowLog) {
            Log.i(TAG + " " + tag, message);
            WriteToFileExample.writeLogsInFile(tag + " Logger.i", message, time);
        }
    }
}

class WriteToFileExample {
    synchronized public static void writeLogsInFile(String tag, String message, String time) {
        BufferedWriter bw = null;
        try {
            String content = tag + " : " + message + "time  ::" + time;
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "LOG" + File.separator + "logger.txt");
            // if file doesnt exists, then create it

            if (!file.exists()) {
                new File(Environment.getExternalStorageDirectory() + File.separator + "LOG").mkdirs();
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(content);
            System.out.println("Done");
        } catch (IOException e) {
            Log.d(Logger.TAG, tag + "Closing  Writing logs error" + message);
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.d(Logger.TAG, tag + "Closing  Writing logs error" + message);
                }
        }
    }

    synchronized public static void writeSerialNumberInFile(String serialNumber, String name, String mobileNumber,String time, String licenseNumber,String macId) {
        BufferedWriter bw = null;
        try {
            String content = serialNumber + "," + name + "," + mobileNumber + "," +time + "," +licenseNumber+","+macId;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "AquaSmart" + File.separator + "userData.txt");
            // if file doesnt exists, then create it
            if (file.exists()) {
                file.delete();
            }
            // if (!file.exists()) {
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "AquaSmart").mkdirs();
            file.createNewFile();
            //}
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(content);
            System.out.println("Done");
        } catch (IOException e) {
            Log.d(Logger.TAG, serialNumber + "Closing,Writing logs error" + name);
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.d(Logger.TAG, serialNumber + "Closing,Writing logs error" + name);
                }
        }
    }

    public static String readDataFromFile() {

        StringBuffer stringBuffer = new StringBuffer();
        String aDataRow = "";
        String aBuffer = "";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "AquaSmart" + File.separator + "userData.txt");

            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fileInputStream));

                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }
                myReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return aBuffer;
    }
}
