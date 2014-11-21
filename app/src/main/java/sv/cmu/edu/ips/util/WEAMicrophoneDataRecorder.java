package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/10/14.
 */
public class WEAMicrophoneDataRecorder {

//    private String logLabel = "IRDataRecorder";
//    private ArrayList<AbstractMap.SimpleEntry<Long, short[]>> samples = new ArrayList<AbstractMap.SimpleEntry<Long, short[]>>(30);
//    private int frameIndex = 0;
//    short [] aggregatedData;
//
//
//
//    protected void dataArrival(long timestamp, short[] data, int length, int frameLength){
//
////		if(this.frameIndex % this.pickedSampleIndex == 1){
////
//        synchronized(samples){
//            samples.add(new AbstractMap.SimpleEntry<Long, short[]>(Long.valueOf(timestamp), data));
//        }
//        Log.i("Frame", "Frame added");
//
////		}
//
//        frameIndex++;
//        if(frameIndex == Integer.MAX_VALUE - 1)
//            this.frameIndex = 0;
//        Log.i("Frame", "Frame: " + frameIndex);
//    }
//
//    @Override
//    protected void onRecordEnded(){
//        super.onRecordEnded();
//        aggregatedData = aggregateData();
//    }
//
//    private short [] aggregateData(){
//
//        short [] aggregateData = new short[0];
//        ArrayList<AbstractMap.SimpleEntry<Long, short[]>> data = getData();
//        boolean firstSkipped = false;
//
//        for(AbstractMap.SimpleEntry item : data){
//            if(! firstSkipped){ // first frame has some junk data
//                firstSkipped = true;
//            }
//            else{
//                short[] values = (short[] ) item.getValue();
//                aggregateData = ArrayUtils.addAll(aggregateData, values);
//            }
//        }
//
//        Log.d(logLabel, "Data aggregated!!");
//        Log.d(logLabel, "data length"+ aggregateData.length);
//        //Log.d(logLabel, Arrays.toString(aggregatedData));
//        return aggregateData;
//    }
//
//    public ArrayList<AbstractMap.SimpleEntry<Long, short[]>> getData(){
//        return samples;
//    }
//
//    public short [] getAggregatedData(){
//        return aggregatedData;
//    }
}