package sv.cmu.edu.ips.data;

import com.google.gson.Gson;

/**
 * Created by sumeet on 9/19/14.
 */
public class ScannerData {
//    # {
//        #     "identifier": "SumeetsPhone",
//        #     "beconId": "1071619057806538345"
//        # }

    private String identifier;
    private String beconId;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getBeaconId() {
        return beconId;
    }

    public void setBeaconId(String beaconId) {
        this.beconId = beaconId;
    }

    public String getJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
