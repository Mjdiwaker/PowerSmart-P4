package com.neotechindia.plugsmart.Utilility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.neotechindia.plugsmart.BuildConfig;
import com.neotechindia.plugsmart.R;
import com.neotechindia.plugsmart.application.PlugSmartApplication;
import com.neotechindia.plugsmart.model.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Sheshnath on 12/7/2015.
 */
public class Util {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static android.support.v7.app.AlertDialog noUsbalertDialog;
    private static ProgressDialog showProgressLoader;
    private ProgressDialog progressDialogsplash;
    private CountDownTimer countDownTimer;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String toStringArray(int[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(array[i]);
        }
        String str = sb.toString();
        return str;
    }

    public static String getJsonDataByField(String field, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getString(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isValidateName(String field) {
        boolean res = field.matches("[a-zA-Z]*");// only for a to z character  //field.matches( "[A-Z][a-zA-Z]*" );
        if (res && field.length() >= 2 && field.length() <= 20)
            return true;
        return false;
    }

    public static boolean isValidateSerialNo(String field) {
        if ((field.length() == 12))   //  && isNumericValue(field.substring(0, 2)) && !isNumericValue(field.substring(2, 5)) && isNumericValue(field.substring(5, 12)
            return true;
        return false;
    }

    private static boolean isNumericValue(String value) {
        try {
            int num = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidateLicenseNo(String field) {
        if (field.length() == 24)
            return true;
        return false;
    }

    public static boolean isValidateUUID(String field) {
        if (field.length() >= 12 && field.length() <= 16)
            return true;
        return false;
    }

    public static boolean isValidatePhoneNo(String field) {
        String regexStr = "^[0-9]$";
        return ((field.length() == 13));
    }

    public static boolean isValidatePassword(String field) {
        return (field.length() >= 6 && field.length() <= 12);
    }

    public static boolean setJsonData(String field) {
        if (field.length() >= 6 && field.length() <= 6)
            return true;
        return false;
    }

    public static String[] splitEqualLength(String src, int len) {
        String[] result = new String[(int) Math.ceil((double) src.length() / (double) len)];
        for (int i = 0; i < result.length; i++)
            result[i] = src.substring(i * len, Math.min(src.length(), (i + 1) * len));
        return result;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void sendPacketStationMode(DeviceBean deviceBean, String packet, Context context) {
        HashMap<String, Socket> hashMap = new HashMap<>();
        hashMap = PlugSmartApplication.getHashMap();
        Iterator it = hashMap.entrySet().iterator();
        Socket mac = null;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (deviceBean.macId != null) {
                if (deviceBean.macId.equalsIgnoreCase(pair.getKey().toString())) {
                    mac = (Socket) pair.getValue();
                    break;
                }
            }

            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
        if (mac != null) {
            SocketUtil.write(packet, deviceBean.ip, mac, deviceBean.macId, 0, context);
        }
    }

    public static Bitmap getBitmapFromPath(String filePath) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return null;
    }

    public static boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static String[] splitStringWithBraces(String src) {
        String str = src;
        String delimiter = "\\}\\{";
//        Logger.d("splitStringWithBraces", "Delimiter string is: " + delimiter);
        String[] strS = (str.split(delimiter));
        if (strS.length > 1) {
            for (int i = 0; i < strS.length; i++)
                if (i == 0) {
                    strS[i] = strS[i] + "}";
                } else if (i == strS.length - 1) {
                    strS[i] = "{" + strS[i];
//                    Logger.d("splitStringWithBraces", " Last Split string is: " + (strS[i] = "{" + strS[i]));
                } else {
                    strS[i] = "{" + strS[i] + "}";
//                    Logger.d("splitStringWithBraces", "Else Split string is: " + (strS[i] = "{" + strS[i] + "}"));
                }
        }
//        Logger.d("splitStringWithBraces", "Split string is: " + Arrays.toString(strS));
        return strS;
    }

    public static boolean isValidMobile(String mob) {
        return mob.length() == 13;
    }

    public static boolean isNetworkConnected(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static Boolean isOnline() {
        try {
            boolean isReachable = InetAddress.getByName("www.google.com").isReachable(3000);
            return isReachable;
            /*Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public static String convertTimeStampToDateTime(long jsonDateString) {
        String fotmatedDate = "";
        long millis = 0;
        //Date format of incoming packet will be in this format    17-03-05-17-31-00
        SimpleDateFormat format1 = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        Log.i("test", "date is");
        try {

            fotmatedDate = format2.format(jsonDateString);


            // I assume d-M, you may refer to M-d for month-day instead.

        } catch (Exception e) {
            Log.i("test", "formatDateString  eRROR date is" + e.getLocalizedMessage());
        }
        return fotmatedDate;


    }

    public static long formatDateString(String jsonDateString) {
        String fotmatedDate = "";
        long millis = 0;
        //Date format of incoming packet will be in this format    17-03-05-17-31-00
        SimpleDateFormat format1 = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        // java.text.SimpleDateFormat format2 = new java.text.SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        Log.i("test", "date is");
        try {
            Date date = format1.parse(jsonDateString);
            fotmatedDate = format2.format(date);


            // I assume d-M, you may refer to M-d for month-day instead.

// You will need try/catch around this
            millis = date.getTime();
            // fotmatedDate = format2.format(millis);
            Log.i("timestamp", millis + "");
            Log.i("timestampFormatted", fotmatedDate + "");
        } catch (Exception e) {
            Log.i("test", "formatDateString  eRROR date is" + e.getLocalizedMessage());
        }
        return millis;
    }

    public static String convertDateToString(long Datestr) {
        String targetDateStr = null;
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyMMddHHmmss");
        try {
            targetDateStr = targetFormat.format(Datestr);
            //Date sourceDate = sourceFormat.parse(Datestr);
            //targetDateStr = targetFormat.format(sourceDate);
            Log.i("timestampconvert", targetDateStr + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetDateStr;
    }

    public static String getVesrionName() {

        return BuildConfig.VERSION_NAME;
    }


    public static void showDialog(Context context) {

        try {
            if (noUsbalertDialog == null) {
                //AquasmartApplication contextApp = (AquasmartApplication) context.getApplicationContext();
                android.support.v7.app.AlertDialog.Builder noUsbConnectedDialog = new android.support.v7.app.AlertDialog.Builder(context);
                //TODO:  Please change the setMessage according to E4 and E2

                // noUsbConnectedDialog.setMessage(context.getString(R.string.usbnotconnected));

                noUsbConnectedDialog.setCancelable(true);
                // noUsbConnectedDialog.setIcon(R.drawable.aquasmart_usb_off);

                // create alert dialog
                noUsbalertDialog = noUsbConnectedDialog.create();
                Log.i("Dialogshow", "DialogShowNull");

                noUsbalertDialog.show();
            } else {
                Log.i("Dialogshow", "DialogShow");
                noUsbalertDialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void dismissDialog() {
        if (noUsbalertDialog != null) {
            noUsbalertDialog.dismiss();
            Log.i("DialogDismiss", "DialogDismiss");
        }
    }

    public static void removeDialogInstance() {
        noUsbalertDialog = null;
    }

    public static void showLoader(Context context) {
        try {
            if (showProgressLoader == null) {
                ProgressDialog progressLoader = new ProgressDialog(context);
                progressLoader.setMessage(context.getString(R.string.pleaseWait));
                progressLoader.setCancelable(false);
                // create alert dialog
                showProgressLoader = progressLoader;
                showProgressLoader.show();

            } else {

                showProgressLoader.show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void hideLoader() {
        try {
            if (showProgressLoader != null) {
                showProgressLoader.dismiss();
                removeInstanceLoader();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void removeInstanceLoader() {
        showProgressLoader = null;

    }

    public static String formatDate(Date jsonDateString) {
        String targetDateStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            targetDateStr = formatter.format(jsonDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetDateStr;
    }

    public static String DecimalToHex(String decimalValue) {
        StringBuilder hex = new StringBuilder(1024);
        String[] mac_id = decimalValue.toString().split("-");
        for (int i = 0; i < mac_id.length; i++) {
            mac_id[i] = Integer.toHexString(Integer.parseInt(mac_id[i].toString()));
            if (mac_id[i].length() < 2) {
                mac_id[i] = "0" + mac_id[i];
            }
            if (i < mac_id.length - 1) {
                mac_id[i] = mac_id[i] + ":";
            }

            hex.append(mac_id[i]);
        }
        return hex.toString();
    }

    public static String timeConvert(int time) {
        return time / 24 / 60 + " days-" + time / 60 % 24 + " hours-" + time % 60;
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
