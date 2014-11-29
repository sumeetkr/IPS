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
public class SoundDataCollector extends SensorDataCollector implements ExtAudioRecorder.AudioDataArrivedEventListener {

    private ExtAudioRecorder dataRecorder;
    private List<ExtAudioRecorder.AudioReadResult> dataList;

    public SoundDataCollector(String id, String name) {
        super(id, name);
        setNoOfDataPointsToCollect(20);
    }

    @Override
    public void collectData(Context context, Gson gson, boolean toBeWritten){
        super.collectData(context, gson, toBeWritten);
        dataRecorder = ExtAudioRecorder.getInstance(false, MediaRecorder.AudioSource.CAMCORDER);
        dataList = new ArrayList<ExtAudioRecorder.AudioReadResult>();

        dataRecorder.registerDataListener(this);
        dataRecorder.prepare();
        dataRecorder.start();

    }

    @Override
    public void onNewDataArrived(ExtAudioRecorder.AudioReadResult data) {
        dataList.add(data);

        Logger.debug("Sound data collection" + Arrays.toString(data.buffer));

        if(dataList.size() > getNoOfDataPointsToCollect()){
            notifyForDataCollectionFinished();
        }
    }

    @Override
    public  void notifyForDataCollectionFinished(){
        dataRecorder.stop();
        dataRecorder.release();

        writeDataToFile("SoundData.json", null);
        super.notifyForDataCollectionFinished();
    }

    protected void writeDataToFile(String dataFileName, List<IJsonObject> data) {
        if(dataRecorder != null){
            IPSFileWriter fileWriter = new IPSFileWriter(dataFileName);
            List<Short> shorts = new ArrayList<Short>();

            for(ExtAudioRecorder.AudioReadResult result: dataList){
                for(short dataPoint : result.buffer){
                    shorts.add(dataPoint);
                }
            }

            fileWriter.appendText(shorts.toString());
            fileWriter.close();

            dataRecorder = null;
        }
    }
}
