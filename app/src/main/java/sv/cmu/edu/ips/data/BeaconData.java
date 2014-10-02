package sv.cmu.edu.ips.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

/**
 * Created by sumeet on 10/1/14.
 */
public class BeaconData {
//    "identifier":"7203422764646594139","lat":"37.410459","lng":"-122.059998","location":"B23 Room 117"
    private String identifier;
    private String lat;
    private String lng;
    private String location;

    public BeaconData(String beaconId, LatLng latLng, String label){
        this.identifier = beaconId;
        this.lat = String.valueOf(latLng.latitude);
        this.lng = String.valueOf(latLng.longitude);
        this.location = label;
    }

    public BeaconData(String beaconId, String lat, String lng, String label){
        this.identifier = beaconId;
        this.lat = String.valueOf(lat);
        this.lng = String.valueOf(lng);
        this.location = label;
    }

    public String getJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
