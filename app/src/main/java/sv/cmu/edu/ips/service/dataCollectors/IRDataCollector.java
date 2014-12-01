package sv.cmu.edu.ips.service.dataCollectors;

import android.content.Context;
import android.media.MediaRecorder;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import sv.cmu.edu.ips.util.ExtAudioRecorder;
import sv.cmu.edu.ips.util.IPSFileWriter;
import sv.cmu.edu.ips.util.Logger;

/**
 * Created by sumeet on 11/10/14.
 */
public class IRDataCollector  extends SensorDataCollector implements ExtAudioRecorder.AudioDataArrivedEventListener {

    private ExtAudioRecorder dataRecorder;
    private List<ExtAudioRecorder.AudioReadResult> dataList;
    public IRDataCollector(String id, String name) {
        super(id, name);
        setNoOfDataPointsToCollect(20);
    }

    @Override
    public void collectData(Context context, Gson gson, boolean toBeWritten){
        super.collectData(context, gson, toBeWritten);
        dataRecorder = ExtAudioRecorder.getInstance(false, MediaRecorder.AudioSource.DEFAULT);
        dataList = new ArrayList<ExtAudioRecorder.AudioReadResult>();

        dataRecorder.registerDataListener(this);
        dataRecorder.prepare();
        dataRecorder.start();

    }

    public void collectData(Context context, int noOfPointsToCollect){
        try{
            setNoOfDataPointsToCollect(noOfPointsToCollect);
            collectData(context, new Gson(), false);
        }catch(Exception ex){
            Logger.log(ex.getMessage());
            releaseRecorder();
        }
    }

    @Override
    public void onNewDataArrived(ExtAudioRecorder.AudioReadResult data) {
        dataList.add(data);

        if(dataList.size() > getNoOfDataPointsToCollect()){
            notifyForDataCollectionFinished();
        }

        Logger.debug("IR data collection " + Arrays.toString(data.buffer));
    }

    @Override
    public  void notifyForDataCollectionFinished(){
        releaseRecorder();

        writeDataToFile("IRData.json", null);
        super.notifyForDataCollectionFinished();
    }

    public void releaseRecorder() {
        try{
            if(dataRecorder != null){
                dataRecorder.stop();
                dataRecorder.release();
            }
        }catch (Exception ex){
            Logger.log(ex.getMessage());
        }
    }

//    public List<ExtAudioRecorder.AudioReadResult> getData(){
//        return  dataList;
//    }

    protected void writeDataToFile(String dataFileName, List<IJsonObject> data) {
        if(dataRecorder != null){
            IPSFileWriter fileWriter = new IPSFileWriter(dataFileName);
            List<Short> shorts = aggregateData();

            fileWriter.appendText(shorts.toString());
            fileWriter.close();

            dataRecorder = null;
        }
    }

    public List<Short> aggregateData() {
        List<Short> shorts = new ArrayList<Short>();

        for(ExtAudioRecorder.AudioReadResult result: dataList){
            for(short dataPoint : result.buffer){
                shorts.add(dataPoint);
            }
        }
        return shorts;
    }

}
