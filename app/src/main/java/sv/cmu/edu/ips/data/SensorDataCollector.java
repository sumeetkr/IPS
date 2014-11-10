package sv.cmu.edu.ips.data;

import com.google.gson.Gson;

/**
 * Created by sumeet on 11/9/14.
 */
public class SensorDataCollector {

    private String id;
    private String name;
    private String description;
    private int noOfDataPointsToCollect = 10;

    public SensorDataCollector(String id, String name){
        this.id = id;
        this.name = name;
        this.description = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void collectData(Gson gson){

    }

    public int getNoOfDataPointsToCollect() {
        return noOfDataPointsToCollect;
    }

    public void setNoOfDataPointsToCollect(int noOfDataPointsToCollect) {
        this.noOfDataPointsToCollect = noOfDataPointsToCollect;
    }
}
