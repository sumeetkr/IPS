package sv.cmu.edu.ips.service.dataCollectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.LocationProbe;
import sv.cmu.edu.ips.util.LogUtil;

/**
 * Created by sumeet on 11/9/14.
 */
public class LocationDataCollector extends SensorDataCollector implements Probe.DataListener{

    private boolean isActive = false;
    private LocationProbe locationProbe;
    private List<IJsonObject> data;

    public LocationDataCollector(String id, String name) {
        super(id, name);
        data = new ArrayList<IJsonObject>();
        setNoOfDataPointsToCollect(5);
    }

    @Override
    public void collectData(Gson gson){
        registerProbe(gson);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

        LogUtil.log("LocationDataCollector Data received");
        data.add(iJsonObject2);
        LogUtil.debug(iJsonObject2.toString());

        if(data.size() > getNoOfDataPointsToCollect())  {
            locationProbe.unregisterListener(this);
            locationProbe.destroy();
        }
    }

    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {
        locationProbe.registerPassiveListener(this);

        writeDataToFile("LocationData.json", data);
        LogUtil.log("LocationDataCollector collection completed");
    }

    private void registerProbe(Gson gson) {
        locationProbe = gson.fromJson(new JsonObject(), LocationProbe.class);
        locationProbe.registerListener(this);

        LogUtil.log("LocationDataCollector Probe registered");
    }
}
