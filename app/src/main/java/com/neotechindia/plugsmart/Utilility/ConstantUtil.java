package com.neotechindia.plugsmart.Utilility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.neotechindia.plugsmart.adapter.SettingListAdapter;
import com.neotechindia.plugsmart.listeners.ConnectionListener;
import com.neotechindia.plugsmart.listeners.IAuthentcUser;
import com.neotechindia.plugsmart.listeners.IDeviceInfo;
import com.neotechindia.plugsmart.listeners.IPlugOnOff;
import com.neotechindia.plugsmart.listeners.IScheduleEnableDisableListener;
import com.neotechindia.plugsmart.listeners.IUpdateUiListener;
import com.neotechindia.plugsmart.listeners.IimagePicker;
import com.neotechindia.plugsmart.listeners.RecyclerItemClickListener;
import com.neotechindia.plugsmart.listeners.RemoveDeviceListener;
import com.neotechindia.plugsmart.listeners.TimerListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 12/21/2015.
 */
public class ConstantUtil {

    public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory() + "/aquasmart";
    public static String uuid1 = "uuid123";
    public static String imei = "imei123";
    public static final String BROADCAST_ACTION = "broadcast";
    public static String MAC_ID = "";
    public static String Srno_lic = "";
    public static String stationModeIP = "";
    public static String stationModeSsid = "";
    public static final int Firmware_Upgraded = 4005;
    public static final int BAUD_RATE = 115200;
    public static final String REG_KEY = "regKey";
    public static final String CLIENT_KEY = "key";
    public static final int USB_TYPE = 1;
    public static final int WIFI_TYPE = 2;
    public static final String ID = "id";
    public static final String MESSAGE_PASSING = "message_passing";
    public static final String DATA = "data";
    public static final String COMPLETEDATA = "completedata";
    public static final int SOCKET_TIMEOUT = 15 * (60 * 10000);
    public static String SSID = "PowerSmart";
    public static String SSID1 = "PlugSmart";
    public static String AP_MODE_SSID = "PowerSmart";
    public static final int ClientRegKey = 3005;
    public static int FIRMWARE_To_Be_Upgrade = 9;
    public static int CONNECTED_NETWORK_MODE = 0;
    public static boolean isPumpStatusReceived = false;
    public static boolean isAppLaunched = false;
    public static final int CONNECTED_NETWORK_Station_StationMOde= 0;
    public static final int CONNECTED_NETWORK_Station_EdgeMode = 1;

    public static final int CONNECTED_NETWORK_AP_MODE = 1;
    public static final int CONNECTED_NETWORK_STATION_MODE = 2;
    public static final String configureSettings = "configureSettings";
    public static boolean isSent = false;
    public static boolean isFromSplash = false;
    public static int reportIssueCode = 1234;
    public static int recordAudioPermission = 1512;
    public static boolean voiceInteraction = false;
    public static boolean isStopCalledUsbReceiver = true;
    public static String serialNumber = "serialNumber";
    public static String liscenceNumber = "licensenumber";
    public static IUpdateUiListener updateListener = null;
    public static ConnectionListener connectionListener = null;
    public static IPlugOnOff iPlugOnOff = null;
    public static IAuthentcUser authentcUser = null;
    public static boolean isfromMainActivity = false;
    public static boolean isfromRegistration;
    public static boolean isForAPModeOnly;
    public static String buyername = "buyerName";
    public static String buyerphone = "buyerPhone";
    public static String buyeremail = "buyerEmail";
    public static String Arrayname = "Arrayname";
    public static String Arrayip = "Arrayip";
    public static String Arraysource = "Arraysource";
    public static String adminPass = "adminPass";
    public static String clientName = "clientname";
    public static String clientPhone = "clientPhone";
    public static String clientRegistrationTime = "clientRegistrationTime";
    public static String plugLogs = "Plug_Logs";
    public static boolean isFromSettings;
    public static String gadgetName = "gadgetName";
    public static String macid = "macid";
    public static RemoveDeviceListener removeListener = null;
    public static TimerListener timerListener = null;
    public static String arrayList = "arrayList";
    public static String stationList = "stationlist";
    public static String editSchedule = "editSchedule";
    public static String scheduleList = "scheduleList";
    public static RecyclerItemClickListener recyclerItemClickListener = null;
    public static String iconType = "iconType";
    public static IDeviceInfo deviceInfo = null;
    public static IScheduleEnableDisableListener scheduleEnableDisableState = null;
    public static String isFromAddGadget = "isFromAddGadget";
    public static String stationModePassword = "stationModePassword";
    public static String AddGadgetMac = "AddGadgetMac";
    public static String AddGadgetserial = "AddGadgetserial";
    public static String AddGadgetlicense = "AddGadgetlic";
    public static IimagePicker iimagePick = null;
    public static String gadgetPos = "gadgetPos";
    public static String clientlang = "clientlang";
    public static String licence_type = "licence_type";
    public static String clientlong = "clientlong";
    public static String buyerlat = "buyerlat";
    public static String buyerlong = "buyerlong";
    public static String ip;
    public static String LICENCE_TYPEBOBBY= "LICENCE_TYPEBOBBY";
    public static String licupdate= "licupdate";
    public static String searilupdate= "searilupdate";
    public static SettingListAdapter.OnListItemClickCallBack iOnListItemClickCallBack = null;

    public static HashMap<String, String> macAndSerialNoMap = new HashMap<>();
    public static HashMap<String, String> macAndLicenseNoMap = new HashMap<>();

    public static String getDateByFormat(String dateFormate) {
        Date now = new Date();
        DateFormat sdf;
        /* dateFormate  "MM/dd/yy" , dd/MM/yyyy", "MM-dd-yyyy hh:mm:ss", "EEE MMM dd HH:mm:ss 'EET' yyyy" */
        sdf = new SimpleDateFormat(dateFormate);
        String strDate = sdf.format(now);
        System.out.println("Formatted date in mm/dd/yy is: " + strDate);
        return strDate.toString().trim();
    }

    public static String getDateByFormatWeek(String dateTime) {
        String str[] = dateTime.split(",");
        Date now = new Date();

        /*SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        System.out.println(simpleDateformat.format(now));

        simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        System.out.println(simpleDateformat.format(now));*/

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        dateTime = "";
        String time = str[1];
        String date = str[0];
/*        String str1[]=str[2].split(" ");
        String year = str1[0].substring(2);
        String hh=str1[1];
        hh = hh.replace(" ", "");
        hh = hh.replace(":", "");*/
        dateTime = date + "-" + ConstantUtil.getAutoFillStrings(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1), 2, "0") + "," + time;
        return dateTime;
    }

    public interface IBuildConstant {
        int PORT = 9998;
        String IP = "192.168.81.1";
        //String AP_MODE_IP = "192.168.81.1";
        // String AP_MODE_SSID_CONTAINS="AquaSmart";
        // String AP_MODE_SSID = SSID;
        String AP_MODE_SSID_PASSWORD = "123456789";
        // String STATION_MODE_SSID = "MT7681";
        // String STATION_MODE_MAC_ADDRESS = "00:B0:0C";
        String STATION_MODE_MAC_ADDRESS3[] = {"00:B0:0C", "08:EA:40"};
    }


    public static String getDeviceUuid(Context context) {
        String uuid = "";
        try {

            uuid = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("UUID id", uuid);
            String space = "0000000000000000"; //Length=16 char
            uuid += (space.substring(uuid.length(), space.length()));


            SharedPreferences firmwareSharedPref = context.getSharedPreferences("Plug_Logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firmwareSharedPref.edit();
            editor.putString(ConstantUtil.uuid1, uuid);
            editor.commit();


            return uuid;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;
    }


    public static String getAutoFillString(String str, int max, String chars) {
        try {
            String space = "";
            for (int i = 0; i < max; i++) {
                space += chars;
            }
            str += (space.substring(str.length(), space.length()));
            return str;
        } catch (Exception e) {
            Log.e("Constant", e.toString());
        }
        return "";
    }

    public static String getAutoFillStrings(String str, int max, String chars) {
        String newStr = "";
        try {

            if (str.length() > max) {
                newStr = (str.substring(str.length() - max));
                return newStr;
            } else {
                String space = "";
                for (int i = 0; i < max; i++) {
                    space += chars;
                }

                newStr = (space.substring(str.length(), space.length())) + str;
                return newStr;
            }
        } catch (Exception e) {
            Logger.e("Constant", e.toString());
        }
        return newStr;
    }

    public interface IJsonKeys {
        String JSON_KEY_01 = "01";
        String JSON_KEY_02 = "02";
        String JSON_KEY_03 = "03";
        String JSON_KEY_04 = "04";
        String JSON_KEY_05 = "05";

    }

    public interface INetworkMode {
        int CONNECTED_NETWORK_AP_MODE = 1;
        int CONNECTED_NETWORK_STATION_MODE = 2;
    }

    public interface IBuildConstantPlugSmart {
        int PORT = 9998;
        // String IP = "192.168.0.99"; // Power Smart
        String IP = "192.168.81.1";// Aqua Smart
    }
}
