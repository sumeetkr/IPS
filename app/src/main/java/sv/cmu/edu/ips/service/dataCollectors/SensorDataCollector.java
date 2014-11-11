package sv.cmu.edu.ips.service.dataCollectors;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.IPSFileWriter;

/**
 * Created by sumeet on 11/9/14.
 */
public class SensorDataCollector {

    private String id;
    private String name;
    private String description;
    private int noOfDataPointsToCollect = 10;
    Context context;

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

    public void collectData(Context context, Gson gson){
        this.context = context;
        collectData(gson);
    }

    public int getNoOfDataPointsToCollect() {
        return noOfDataPointsToCollect;
    }

    public void setNoOfDataPointsToCollect(int noOfDataPointsToCollect) {
        this.noOfDataPointsToCollect = noOfDataPointsToCollect;
    }

    protected void writeDataToFile(String dataFileName, List<IJsonObject> data) {
        IPSFileWriter fileWriter = new IPSFileWriter(dataFileName);

        int i = 0;
        for(IJsonObject obj:data){
            if(i!=0){
                fileWriter.appendText("," + obj.toString());
            }else{
                fileWriter.appendText("["+ obj.toString());
            }
            i++;
        }

        fileWriter.appendText("]");
        fileWriter.close();

        onDataCollectionFinished();
    }

    protected void onDataCollectionFinished(){
        Intent intent = new Intent(Constants.DATA_COLLECTION_FINISHED);
        intent.putExtra(Constants.SENSOR_TYPE, getName());


        if(context != null) context.sendBroadcast(intent);
    }
}
