package sv.cmu.edu.ips.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sumeet on 11/30/14.
 */
public class WiFiData {
    private String SSID;
    private String BSSID;
    private double distanceCm;
    private double level;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public double getDistanceCm() {
        return distanceCm;
    }

    public void setDistanceCm(double distanceCm) {
        this.distanceCm = distanceCm;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }


    public static WiFiData getWiFiDataFromJsonString(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, WiFiData.class);
    }

    public static List<WiFiData> getWiFiDataCollectionFromJsonString(String json){
        Type listType = new TypeToken<ArrayList<WiFiData>>() {
        }.getType();

        List<WiFiData> yourClassList = new Gson().fromJson(json, listType);
        return yourClassList;
    }
}
