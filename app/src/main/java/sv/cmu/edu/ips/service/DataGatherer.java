package sv.cmu.edu.ips.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import sv.cmu.edu.ips.data.ClassificationData;
import sv.cmu.edu.ips.data.LabelData;
import sv.cmu.edu.ips.data.WiFiData;
import sv.cmu.edu.ips.service.dataCollectors.AudioDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.SensorDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.WiFiSensorDataCollector;
import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.Logger;

/**
 * Created by sumeet on 11/30/14.
 */
public class DataGatherer {

    private Context context;
    private String beaconIdFound="";
    private Set<String> listOfProbesWhichFinishedDataCollection;
    private List<SensorDataCollector> probes;
    private FunfManager funfManager;
    private BasicPipeline pipeline;
    private ServiceConnection funfManagerConn;

    public DataGatherer(Context context){
        this.context = context;
        listOfProbesWhichFinishedDataCollection = new HashSet<String>();
        probes = new ArrayList<SensorDataCollector>();
        probes.add(new AudioDataCollector("1", "AudioData"));
        probes.add(new WiFiSensorDataCollector("3", "WiFi"));
    }

    public void startCollecting() {
        IntentFilter intentFilter = new IntentFilter(Constants.DATA_COLLECTION_FINISHED);
        context.registerReceiver(dataCollectionFinishedBroadcastReceiver, intentFilter);

        funfManagerConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                funfManager = ((FunfManager.LocalBinder)service).getManager();
                pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(Constants.PIPELINE_NAME);

                //IR previouslyCollectedData and sound previouslyCollectedData has to be done after others as they use same hardware
                // Start lengthy operation in a background thread
                for(int i=1; i<probes.size(); i++){
                    final int finalI = i;
                    Thread collectorThread = new Thread(new Runnable() {
                        public void run() {
                            Gson gson = funfManager.getGson();
                            probes.get(finalI).collectData(context, gson, true);
                        }
                    });
                    collectorThread.start();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                funfManager = null;
            }
        };

        // Bind to the service, to create the connection with FunfManager
        context.bindService(new Intent(context, FunfManager.class), funfManagerConn, context.BIND_AUTO_CREATE);
    }

    private void finishCollection(){
        context.unregisterReceiver(dataCollectionFinishedBroadcastReceiver);
        destroyFunf();
    }

    private void destroyFunf() {
        try{
            if(pipeline != null) pipeline.onDestroy();
            if(funfManagerConn != null) context.unbindService(funfManagerConn);
            pipeline =null;
            funfManager = null;
            funfManagerConn = null;
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    private BroadcastReceiver dataCollectionFinishedBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String collectorName = intent.getStringExtra(Constants.SENSOR_TYPE);
            if(intent.hasExtra("beaconId")){
                beaconIdFound = intent.getStringExtra("beaconId");
            }

            Logger.log("Received finish of " + collectorName);
            if(collectorName != null && collectorName != "" && listOfProbesWhichFinishedDataCollection!= null && !listOfProbesWhichFinishedDataCollection.contains(collectorName)){
                listOfProbesWhichFinishedDataCollection.add(collectorName);

                if(probes.size() > 1 && listOfProbesWhichFinishedDataCollection.size() == probes.size()-1){
                    Gson gson = funfManager.getGson();
                    probes.get(0).collectData(context, gson, true);

                }else if(listOfProbesWhichFinishedDataCollection.size() >= probes.size()){
                    listOfProbesWhichFinishedDataCollection.clear();
                    finishCollection();
                }
            }
        }
    };

    ClassificationData getClassificationData(){
        ClassificationData classificationData = new ClassificationData();
        List<IJsonObject> jsons = probes.get(1).getData();

        List<WiFiData> wiFiDataPoints = new ArrayList<WiFiData>();
        for(IJsonObject json :jsons){
            WiFiData data = WiFiData.getWiFiDataFromJsonString(json.toString());
            wiFiDataPoints.add(data);
        }
        classificationData.setWifiData(wiFiDataPoints);

        if(beaconIdFound.length()>2){
            LabelData label = new LabelData();
            label.setBeaconId(beaconIdFound);
            classificationData.setLabelData(label);
        }

        return classificationData;
    }
}
