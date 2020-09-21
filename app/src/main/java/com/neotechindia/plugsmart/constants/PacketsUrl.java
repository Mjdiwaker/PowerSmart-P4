package com.neotechindia.plugsmart.constants;

import android.content.Context;


import com.neotechindia.plugsmart.Utilility.CommandUtil;
import com.neotechindia.plugsmart.Utilility.ConstantUtil;
import com.neotechindia.plugsmart.model.MultiJsonModel;
import com.neotechindia.plugsmart.model.SingleJsonModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Sheshnath.Yadav on 12/22/2016.
 */
public class PacketsUrl {
    final static String SID = "9000";


    public static String deviceAuthentication(Context context) {
        //        {"sid":"9000","bad":"0100","sad":"000","cmd":"8208","data":"ABCDEFGHIJKL1234"}
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.authentication, ConstantUtil.getAutoFillString(ConstantUtil.getDeviceUuid(context), 16, "0"));
        return singleJsonModel.toStringWithoutBAD();
        // return "";
    }

    public static String getTimeSyncGetter() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_device_Time, "");
        return singleJsonModel.toStringWithoutData();
    }

    public static String getTemperature() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_device_Temperature, "");
        return singleJsonModel.toStringWithoutData();
    }

    public static String getUrlRemoveRegisteredDevice(String name) {
        //  name = ConstantUtil.getAutoFillString(name, 30, " ");

        //        {"sid":"9000","bad":"0100","sad":"001","cmd":"8211","data":"PARVESH                       "}
        SingleJsonModel singleJasonModel = new SingleJsonModel(SID, "0100", "001", CommandUtil.cmd_remove_registered_device, name);
        String json = singleJasonModel.toStringWithoutBAD();
        return json;
    }

    public static String getViewRegisteredDevices() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_view_registered_devices, "1");
        return singleJsonModel.toStringWithoutData();
    }

    public static String serverAuthentication(String serialno, String licenseno) {
        //    {"sid":"9000","bad":"0100","sad":"000","cmd":"8228","data":{"01":"1","02":"16HIS2100004","03":"F6FVSIOWNF3F0340460DB545"}}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", "1");
        data.put("02", serialno);
        data.put("03", licenseno);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.server_authentication, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String buyerRegistration(String name, String mob, String email, String pwd) {
        //name = ConstantUtil.getAutoFillString(name, 30, " ");
        //email = ConstantUtil.getAutoFillString(email, 40, " ");
        //pwd = ConstantUtil.getAutoFillString(pwd, 12, " ");
        String[] mobile;
        //  mobile = mob.split("-");
        mob.substring(3);
        //mob = mobile[1];
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", "2");
        data.put("02", pwd);
        data.put("03", "000000000000");
        data.put("04", name);
        data.put("05", mob);
        data.put("06", email);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.buyer_registration, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String verifyingAdminPassword(String packet) {
        //  packet = ConstantUtil.getAutoFillString(packet, 12, " ");
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_password_veryfication, packet);
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String clientRegistration(String name, String mob, String regType, Context context) {
        //   name = ConstantUtil.getAutoFillString(name, 30, " ");
        String uuid = ConstantUtil.getAutoFillString(ConstantUtil.getDeviceUuid(context), 16, "0");
            /*  if (AquaSmartPreference.getInstance(context).getUUid("").length() <= 4)
              {
                  ConstantUtil.getDeviceUuid();
              }*/

        try {
            String[] mobile;
            mobile = mob.split("-");
            mob = mobile[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", "" + regType);
        data.put("02", uuid);
        data.put("03", name);
        data.put("04", name);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.client_registration, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String getMacIdOrIP() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_mac_id_and_ip, "");
        return singleJsonModel.toStringWithoutData();
    }

    public static String getPowerStatus(String gadgetName) {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_power_status, gadgetName);
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String changeAdminPassword(String adminPassword, String newpass) {
        Map<String, String> data = new LinkedHashMap<>();
        //  adminPassword = ConstantUtil.getAutoFillString(adminPassword, 12, " ");
        //  newpass = ConstantUtil.getAutoFillString(newpass, 12, " ");
        data.put("01", adminPassword);
        data.put("02", newpass);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_change_password, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String forgetAdminPassword(String license, String newpass) {
        Map<String, String> data = new LinkedHashMap<>();
        //  newpass = ConstantUtil.getAutoFillString(newpass, 12, " ");
        data.put("01", license);
        data.put("02", newpass);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_forget_password, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String editGadgetName(String gadgetNamee) {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_edit_gadget_name, gadgetNamee);
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String editGadgetName(String gadgetName, int imgPosition) {
        String pos = "";
        Map<String, String> data = new LinkedHashMap<>();
        if (imgPosition < 10) {
            pos = "0" + String.valueOf(imgPosition);
        } else {
            pos = String.valueOf(imgPosition);
        }
        data.put("01", gadgetName);
        data.put("02", pos);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_edit_gadget_name, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String setNetWorkSettingPacket(String hostName, String hostPassword, String connectivityMode, String ip, String edgestation_status) {
        Map<String, String> data = new LinkedHashMap<>();
        String hostNameWithSpaces = ConstantUtil.getAutoFillString(hostName, 30, "#");
        String passwordWithSpaces = ConstantUtil.getAutoFillString(hostPassword, 30, "#");
        data.put("01", connectivityMode);
        data.put("02", hostNameWithSpaces);
        data.put("03", passwordWithSpaces);
        int hotNameLength = hostName.trim().length();
        int hostPassLength = hostPassword.trim().length();
        if (hotNameLength <= 9) {
            data.put("04", "0" + String.valueOf(hotNameLength));
        } else {
            data.put("04", String.valueOf(hotNameLength));
        }
        if (hostPassLength <= 9) {
            data.put("05", "0" + String.valueOf(hostPassLength));
        } else {
            data.put("05", String.valueOf(hostPassLength));
        }
        if (connectivityMode.equals("2")) {
            data.put("06", edgestation_status);
            if (edgestation_status.equals("1")) {
                data.put("07", ip);
            }
        }


        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.COMMAND_SAVE_NETWORK_SETTING, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String createOrSaveSchedule(String s, String s1, String s2, String s3, String startTime, String endTime, String scheduleDays) {
        //                {"sid":"9000","sad":"x","cmd":"7004","data":{"01":"tank num or buzzer id",
        // "02":"","03":"1","04":"schedule type","05":"start time","06":"stop time","07":"day number"}}
        //                {"sid":"9000","sad":"x","cmd":"7004","data":
        // {"01":"1","02":"0","03":"1","04":"0","05":"712","06":"715","07":"1234567"}}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", s);
        data.put("02", s1);
        data.put("03", s2);
        data.put("04", s3);
        data.put("05", startTime);
        data.put("06", endTime);
        data.put("07", scheduleDays);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_create_or_save_schedule, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String deleteOrDisableSchedule(String itemId, String state) {
        //        {"sid":"9000","sad":"x","cmd":"7004","data":
        // {"01":"tank num or buzzer id","02":"schedule id","03":"3"}}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", "1");
        data.put("02", itemId);
        data.put("03", state);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_create_or_save_schedule, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String setPowerStatus(String status) {
        Map<String, String> data = new LinkedHashMap<>();

        //   Map<String, String> data = new LinkedHashMap<>();
        data.put("01", status);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_set_power_status, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String setTimeSyncSetter(String time) {
        //  Logger.d("", "Date Time : " + time);
        String temp = time.replace(':', '-').replace(' ', '-').replace(",", "-");
        /*{"sid":"9000","sad":"x","cmd":"8102","data":"DD-MM-YY-WW-hh-mm-ss"}*/
        // String gmt = ConstantUtil.getAutoFillString("GMT+0530", 30, " ");
        Map<String, String> data = new LinkedHashMap<>();

        //  Map<String, String> data = new LinkedHashMap<>();
        data.put("01", temp);
        data.put("02", "GMT+0530");
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_set_time_sync, data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String getSchedulerList() {
        /*{"sid":"9000","sad":"x","cmd":"7009","data":"tank num"}*/
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_schedulers, "1");
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String getSerialNumber() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "", "x", CommandUtil.cmd_get_serial_number);
        return singleJsonModel.toStringWithoutData();
    }

    public static String getLiscenseNumber() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "", "x", CommandUtil.cmd_get_liscence_number);
        return singleJsonModel.toStringWithoutData();
    }

    public static String getSerialLiscenseNumber() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "", "x", CommandUtil.cmd_get_serialliscence_number);
        return singleJsonModel.toStringWithoutData();
    }

    public static String firmwareVersion() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_fw_version, "");
        return singleJsonModel.toStringWithoutData();
    }

    public static String firmwareUpdate(String crc, String versionNumber) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", crc);
        data.put("02", versionNumber);
        /*{"sid":"9000","sad":"x","cmd":"8003","data":"01":"crc" ,"02":"new version number"}*/
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_fw_update, data);
        // SingleJsonModel singleJsonModel = new SingleJsonModel(SID,"0100", "x", CommandUtil.cmd_fw_update,data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String getNetworkSettingsConfiguration() {
            /*
           Get Network SSetting Configuration
           device to blackbox:
           {"sid":"9000","sad":"000","cmd":"8020"}
            */
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_network_setting_configuration);
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String setTimer(String time) {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_set_timer, time);
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String getTimer() {
        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "", "x", CommandUtil.cmd_get_timer, "");
        return singleJsonModel.toStringWithoutBAD();
    }

    public static String getWeather() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01","2");
        data.put("02", "");
        data.put("03", "");
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.cmd_get_weather, data);
        return multiJsonModel.toStringWithoutBAD();

//        SingleJsonModel singleJsonModel = new SingleJsonModel(SID, "0100", "x", CommandUtil.cmd_get_weather, "");   //by bobby
//        return singleJsonModel.toStringWithoutData();
    }

    public static String setWeather(String on,String ip){
        Map<String,String>data = new LinkedHashMap<>();
        data.put("01","1");
        data.put("02",on);
        data.put("03",ip);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID,"0100","x",CommandUtil.cmd_get_weather,data);
        return multiJsonModel.toStringWithoutBAD();
    }

    public static String saveDeviceCredientials(String licenseno, String serialno, String macId) {
        //{"sid":"9000","sad":"x","cmd":"8270","data":{"01":"license number","02":"serial number","03":"mac id"}}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("01", licenseno);
        data.put("02", serialno);
        data.put("03", macId);
        MultiJsonModel multiJsonModel = new MultiJsonModel(SID, "0100", "x", CommandUtil.SAVE_CREDENTIALS_CMD, data);
        return multiJsonModel.toStringWithoutBAD();
    }


}
