package sv.cmu.edu.ips.service.dataCollectors;

import android.content.Context;
import android.media.MediaRecorder;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.media.funf.json.IJsonObject;
import sv.cmu.edu.ips.data.AudioData;
import sv.cmu.edu.ips.service.IRDataRecorder;
import sv.cmu.edu.ips.util.IPSFileWriter;
import sv.cmu.edu.ips.util.LogUtil;

/**
 * Created by sumeet on 11/10/14.
 */
public class AudioDataCollector extends SensorDataCollector{

    private IRDataRecorder dataRecorder;
    private ArrayList<AudioData> aggregatedData;
    public AudioDataCollector(String id, String name) {
        super(id, name);
        setNoOfDataPointsToCollect(100);
    }

    @Override
    public void collectData(Context context, Gson gson){
        super.collectData(context,gson);

        aggregatedData = new ArrayList<AudioData>();
        dataRecorder = new IRDataRecorder() {

            @Override
            protected void dataArrival(long timestamp, short[] data,
                                       int length, int frameLength) {
                super.dataArrival(timestamp, data, length, frameLength);

                AudioData audioData = new AudioData(timestamp, data);
                aggregatedData.add(audioData);

                LogUtil.debug("Audio Data " + audioData.toString());
                if(length > getNoOfDataPointsToCollect()){
                    dataRecorder.stopRecord();
                }
            }

            @Override
            protected void onRecordEnded() {
                super.onRecordEnded();
                writeDataToFile("AudioData.json", null);
                LogUtil.log(getName() + "Data collection completed");
            }

        };

        dataRecorder.startRecord(MediaRecorder.AudioSource.CAMCORDER);
    }

    protected void writeDataToFile(String dataFileName, List<IJsonObject> data) {
        IPSFileWriter fileWriter = new IPSFileWriter(dataFileName);
        fileWriter.appendText(Arrays.toString(dataRecorder.getAggregatedData()));
        fileWriter.close();

        super.onDataCollectionFinished();
    }
}
