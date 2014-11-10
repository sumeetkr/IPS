package sv.cmu.edu.ips.service.dataCollectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.MagneticFieldSensorProbe;
import sv.cmu.edu.ips.util.LogUtil;

/**
 * Created by sumeet on 11/9/14.
 */
public class MagneticFiledDataCollector extends SensorDataCollector implements Probe.DataListener {


    private boolean isActive = false;
    private MagneticFieldSensorProbe magneticFieldSensorProbe;
    private List<IJsonObject> data;

    public MagneticFiledDataCollector(String id, String name) {
        super(id, name);
        data = new ArrayList<IJsonObject>();
    }

    @Override
    public void collectData(Gson gson){
        registerProbe(gson);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

        LogUtil.log("MagneticFiled Data received");
        data.add(iJsonObject2);
        LogUtil.debug(iJsonObject2.toString());

        if(data.size()>10) magneticFieldSensorProbe.destroy();
    }

    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {
        magneticFieldSensorProbe.registerPassiveListener(this);
        writeDataToFile("MagneticField.json", data);
        LogUtil.log("MagneticFiled Data collection completed");
    }

    private void registerProbe(Gson gson) {
        magneticFieldSensorProbe = gson.fromJson(new JsonObject(), MagneticFieldSensorProbe.class);
        magneticFieldSensorProbe.registerListener(this);

        LogUtil.log("MagneticFiled Probe registered");
    }

}

