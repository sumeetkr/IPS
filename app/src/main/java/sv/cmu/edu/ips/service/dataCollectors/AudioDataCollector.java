package sv.cmu.edu.ips.service.dataCollectors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import sv.cmu.edu.ips.data.AudioData;
import sv.cmu.edu.ips.data.SignalData;
import sv.cmu.edu.ips.util.IPSFileWriter;
import sv.cmu.edu.ips.util.Logger;
import sv.cmu.edu.ips.util.SignalAnalyzer;
import sv.cmu.edu.ips.util.WEAMicrophoneDataRecorder;

/**
 * Created by sumeet on 11/29/14.
 */
public class AudioDataCollector extends SensorDataCollector {

    private WEAMicrophoneDataRecorder dataRecorder;
    private String fileName;
    String anyBeaconId= "";

    public AudioDataCollector(String id, String name) {
        super(id, name);
        fileName = name + ".json";
    }

    @Override
    public void collectData(Context context, Gson gson, final boolean toBeWritten ){

        final Context ctxt = context;
        super.context = ctxt;


        dataRecorder = new WEAMicrophoneDataRecorder() {
            ArrayList<AudioData> aggregatedData= new ArrayList<AudioData>();

            @Override
            protected void dataArrival(long timestamp, short[] data,
                                       int length, int frameLength) {
                super.dataArrival(timestamp, data, length, frameLength);

                Logger.log("data arrived");

                AudioData audioData = new AudioData(timestamp, data);
                aggregatedData.add(audioData);
                Logger.log("data length" + data.length);
                Logger.log(Arrays.toString(data));
            }

            @Override
            protected void onRecordEnded(){
                super.onRecordEnded();
                try {
                    SignalData data = SignalAnalyzer.getSignalInfoStringFromRawSignal(super.getAggregatedData());
                    String beaconId = data.getBeaconId();
                    Logger.log("Got beacon ID " + beaconId);

                    if(!beaconId.isEmpty() && beaconId.length()>5){
                        anyBeaconId = beaconId;
                        Intent intent = new Intent("my-event");
                        intent.putExtra("message", beaconId);
                        LocalBroadcastManager.getInstance(ctxt).sendBroadcast(intent);
                    }
                }catch (Exception e) {
                    Logger.log( "exception "+ e.getMessage());
                }finally {
                    stopRecording();
                    if(toBeWritten){
                        IPSFileWriter fileWriter = new IPSFileWriter(fileName);
                        fileWriter.appendText(Arrays.toString(super.getAggregatedData()));
                        fileWriter.close();
                    }

                    informSuperOfDataCollectionFinish(anyBeaconId);
                }
            }

        };

        dataRecorder.startRecord(7);
    }

    private void stopRecording() {
        try{
            if(dataRecorder != null) {
                dataRecorder.stopRecord();
                dataRecorder = null;
            }
            Logger.log("stopped recording");
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    private void informSuperOfDataCollectionFinish(String beaconId){
        super.notifyForDataCollectionFinished("beaconId", beaconId);
    }
}
