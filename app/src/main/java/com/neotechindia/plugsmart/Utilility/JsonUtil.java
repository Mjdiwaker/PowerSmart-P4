package com.neotechindia.plugsmart.Utilility;

import android.util.Log;


import com.google.gson.Gson;
import com.neotechindia.plugsmart.model.MultiJsonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * This Class Handle JaksonJSON to Object and object to JaksonJSON conversion
 * 
 * @author Sheshnath
 * 
 */
public class JsonUtil {

	private static final String TAG = JsonUtil.class.getName();

	public static String getJsonOfMap(Map<String, String> map) {
		if(map.size()==0)
			return "{}";
		String json="{";
		if(map.size()>0) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				json = json + "\"" + (entry.getKey()) + "\"" + ":"
						+ "\"" + entry.getValue() + "\"" + ",";
			}
		}else {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				json = json + "\"" + (entry.getKey()) + "\"" + ":"
						+ "\"" + entry.getValue() + ",";
			}
		}
		json=json.substring(0,json.length()-1);
		return json+"}";
	}
	public static String getJsonDataByField(String field,String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return obj.getString(field);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static boolean isJSONValid(String jsonInString) {
		try {
			new JSONObject(jsonInString);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(jsonInString);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static ArrayList<MultiJsonModel> getJSONObjectListFromJSONArray(JSONArray array)
			throws JSONException {
		ArrayList<MultiJsonModel> jsonObjects = new ArrayList<>();
		for (int i = 0;
			 i < (array != null ? array.length() : 0););
			// jsonObjects.add((MultiJsonModel) toModel(array.getJSONObject(i++).toString(),MultiJsonModel.class)));
		return jsonObjects;
	}
	public static Object toModel(String json, Type listType) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, listType);
		} catch (Exception e) {
			Log.e(TAG, "Error in Converting JSON to Model "+e+"\n "+json);
		}
		return null;
	}
}
