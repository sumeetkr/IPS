package sv.cmu.edu.ips.service.dataCollectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import sv.cmu.edu.ips.util.LogUtil;

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

        LogUtil.log(this.getClass().getName()+ "Data received");
        data.add(iJsonObject2);
        LogUtil.debug(iJsonObject2.toString());
    }

    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {
        wifiProbe.registerPassiveListener(this);

        writeDataToFile("WiFiData.json", data);
        LogUtil.log(this.getClass().getName() + "Data collection completed");
    }

    private void registerProbe(Gson gson) {
        wifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);
        wifiProbe.registerListener(this);

        LogUtil.log(this.getClass().getName() + "Probe registered");
    }

}
