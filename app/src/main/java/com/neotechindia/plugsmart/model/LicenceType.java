package com.neotechindia.plugsmart.model;

import java.util.HashMap;

public class LicenceType {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LicenceType(String type){
        this.type = type;
    }

    public static int GetScheduleNumber(String type){//TODO: string /int
        int num = 0;
        switch (type){
            case "1":
                num = 3;
                break;
            case "2":
                num = 5;
                break;
            case "3":
                num = 5;
                break;
            case "4":
                num = 5;
                break;
            case "5":
                num = 5;
                break;
            default:
                num = 5;
                break;
        }
        return num;
    }


    public static int GetClientNumber(int type){//TODO: string /int
        int num = 0;
        switch (type){
            case 1:
                num = 1;
                break;
            case 2:
                num = 3;
                break;
            case 3:
                num = 3;
                break;
            case 4:
                num = 3;
                break;
            case 5:
                num = 5;
            default:
                num = 3;
                break;
        }
        return num;
    }

    public static HashMap<String,Boolean> getNetworkSetting(String type){
        HashMap<String,Boolean> keys = new HashMap<>();
        switch (type){
            case "1":
                keys.put("ap",true);
                keys.put("station",true);
                keys.put("edge",false);
                break;
            case "2":
                keys.put("ap",true);
                keys.put("station",true);
                keys.put("edge",false);
                break;
            case "5":
                keys.put("ap",true);
                keys.put("station",false);
                keys.put("edge",true);
                break;
            case "4":
                keys.put("ap",true);
                keys.put("station",true);
                keys.put("edge",true);
                break;
                default:
                    keys.put("ap",true);
                    keys.put("station",true);
                    keys.put("edge",false);
                    break;
        }
        return keys;
    }


}
