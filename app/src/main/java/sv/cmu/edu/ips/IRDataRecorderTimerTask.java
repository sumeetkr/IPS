package sv.cmu.edu.ips;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

import sv.cmu.edu.ips.data.AudioData;

/**
 * Created by sumeet on 9/10/14.
 */
public class IRDataRecorderTimerTask extends TimerTask{

    private String logLabel = "IRDataRecorderTimerTask";
    private IRDataRecorder dataRecorder;
    private int dataCount;
    private Boolean isListening;

    @Override
    public void run() {
        Log.i(logLabel, "Starting timer task" );
        collectData();
    }

    private void collectData(){
        startRecording();
        try {
            //is called every 2 seconds
            Thread.sleep(200);
            stopRecording();
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {

        }
        aggregateData();
    }

    private void startRecording() {

        dataCount = 0;
        isListening = true;

        Log.d(logLabel, "started recording");
        dataRecorder = new IRDataRecorder() {
            int frameIndex = 0;
            ArrayList<AudioData> aggregatedData= new ArrayList<AudioData>();

            @Override
            protected void dataArrival(long timestamp, short[] data,
                                       int length, int frameLength) {
                super.dataArrival(timestamp, data, length, frameLength);

                Log.d(logLabel, "data arrived");

                AudioData audioData = new AudioData(timestamp, data);
                aggregatedData.add(audioData);
                Log.d(logLabel, "data length"+ data.length);
                Log.d(logLabel, Arrays.toString(data));

                frameIndex++;
                if(frameIndex == Integer.MAX_VALUE - 1)
                    frameIndex = 0;
            }
        };

        dataRecorder.startRecord();
    }

    private void stopRecording() {
        isListening = false;
        dataRecorder.stopRecord();
        Log.d(logLabel, "stopped recording");
    }

    private void aggregateData(){

        short [] aggregateData = new short[0];
        ArrayList<AbstractMap.SimpleEntry<Long, short[]>> data = dataRecorder.getData();
        boolean firstSkipped = false;

        for(AbstractMap.SimpleEntry item : data){
            if(! firstSkipped){ // first frame has some junk data
                firstSkipped = true;
            }
            else{
                short[] values = (short[] ) item.getValue();
                aggregateData = ArrayUtils.addAll(aggregateData, values);
            }
        }

        Log.d(logLabel, "Data aggregated!!");
        Log.d(logLabel, "data length"+ aggregateData.length);
        Log.d(logLabel, Arrays.toString(aggregateData));
    }
}
