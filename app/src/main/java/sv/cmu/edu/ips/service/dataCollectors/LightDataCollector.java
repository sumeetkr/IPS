package sv.cmu.edu.ips.service.dataCollectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.LightSensorProbe;
import sv.cmu.edu.ips.util.Logger;

/**
 * Created by sumeet on 11/9/14.
 */
public class LightDataCollector extends SensorDataCollector implements Probe.DataListener {


    private boolean isActive = false;
    private LightSensorProbe lightSensorProbe;
    private List<IJsonObject> data;

    public LightDataCollector(String id, String name) {
        super(id, name);
        super.setNoOfDataPointsToCollect(1);
        data = new ArrayList<IJsonObject>();
    }

    @Override
    public void collectData(Gson gson){
        registerProbe(gson);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

        Logger.log("LightData received");
        data.add(iJsonObject2);
        Logger.debug(iJsonObject2.toString());

        if(data.size() > getNoOfDataPointsToCollect()){
            lightSensorProbe.unregisterListener(this);
            lightSensorProbe.destroy();
        }
    }

    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {
        lightSensorProbe.registerPassiveListener(this);

        writeDataToFile("LightData.json", data);
        Logger.log("LightData collection completed");
    }

    private void registerProbe(Gson gson) {
        lightSensorProbe = gson.fromJson(new JsonObject(), LightSensorProbe.class);
        lightSensorProbe.registerListener(this);

        Logger.log("LightData Probe registered");
    }

}

