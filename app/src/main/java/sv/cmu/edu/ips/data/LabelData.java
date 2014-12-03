package sv.cmu.edu.ips.data;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by sumeet on 11/29/14.
 */
public class LabelData implements Serializable {

    private String roomInfo;
    private double lat =0;
    private double lng =0;
    private double x;
    private double y;
    private String beaconId;

    public String getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(String roomInfo) {
        this.roomInfo = roomInfo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public class Orientation{
        private String orientXValue;
        private String orientYValue;
        private String orientZValue;

        public String getOrientXValue() {
            return orientXValue;
        }

        public void setOrientXValue(String orientXValue) {
            this.orientXValue = orientXValue;
        }

        public String getOrientYValue() {
            return orientYValue;
        }

        public void setOrientYValue(String orientYValue) {
            this.orientYValue = orientYValue;
        }

        public String getOrientZValue() {
            return orientZValue;
        }

        public void setOrientZValue(String orientZValue) {
            this.orientZValue = orientZValue;
        }
    }

    public static LabelData getLabelDataFromJsonString(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, LabelData.class);
    }
}