package sv.cmu.edu.ips.service.dataCollectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import sv.cmu.edu.ips.util.Logger;

/**
 * Created by sumeet on 11/9/14.
 */
public class WiFiSensorDataCollector extends SensorDataCollector implements Probe.DataListener {

    private boolean isActive = false;
    private WifiProbe wifiProbe;
    private List<IJsonObject> data;

    public WiFiSensorDataCollector(String id, String name) {
        super(id, name);
        data = new ArrayList<IJsonObject>();
    }

    @Override
    public void collectData(Gson gson){
        registerProbe(gson);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

        Logger.log(getName() + "Data received");
        data.add(iJsonObject2);
        Logger.debug(iJsonObject2.toString());
    }

    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {
        wifiProbe.registerPassiveListener(this);

        writeDataToFile("WiFiData.json", data);
        Logger.log(getName() + "Data collection completed");
    }

    @Override
    public List<IJsonObject> getData(){
        return data;
    }

    private void registerProbe(Gson gson) {
        wifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);
        wifiProbe.registerListener(this);

        Logger.log(getName() + "Probe registered");
    }

}
