package sv.cmu.edu.ips;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sumeet on 9/10/14.
 */
public class IRDataRecorder extends MicrophoneRecorder{

    private String logLabel = "IRDataRecorder";
    private ArrayList<AbstractMap.SimpleEntry<Long, short[]>> samples = new ArrayList<AbstractMap.SimpleEntry<Long, short[]>>(30);
    private int frameIndex = 0;

    public IRDataRecorder(){
        super();
    }

    protected void dataArrival(long timestamp, short[] data, int length, int frameLength){

//		if(this.frameIndex % this.pickedSampleIndex == 1){
//
        synchronized(samples){
            samples.add(new AbstractMap.SimpleEntry<Long, short[]>(Long.valueOf(timestamp), data));
        }
        Log.i("Frame", "Frame added");

//		}

        frameIndex++;
        if(frameIndex == Integer.MAX_VALUE - 1)
            this.frameIndex = 0;
        Log.i("Frame", "Frame: " + frameIndex);
    }

    @Override
    protected void onRecordEnded(){
//        aggregateData();
    }

    private void aggregateData(){

        short [] aggregateData = new short[0];
        ArrayList<AbstractMap.SimpleEntry<Long, short[]>> data = getData();
        for(AbstractMap.SimpleEntry item : data){
            short[] values = (short[] ) item.getValue();
            aggregateData = ArrayUtils.addAll(aggregateData, values);
        }

        Log.d(logLabel, "Data aggregated!!");
        Log.d(logLabel, "data length"+ aggregateData.length);
        Log.d(logLabel, Arrays.toString(aggregateData));
    }

    public ArrayList<AbstractMap.SimpleEntry<Long, short[]>> getData(){
        return samples;
    }

}
