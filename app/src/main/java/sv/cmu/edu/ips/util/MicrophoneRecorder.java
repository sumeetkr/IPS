package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/10/14.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MicrophoneRecorder{

    private static final String CLASS_PREFIX = MicrophoneRecorder.class.getName();
    private AudioRecord recorder = null;
    private int bufferSizeInBytes = 0;
    private int sleepInterval = 0;
    private Object syncObject = new Object();
    private boolean isRecording = false;
    private State state;

    public static final int SAMPLING_FREQUENCY = 44100;
    public enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};

    public static MicrophoneRecorder getInstance(boolean recordingCompressed, int audioSourceType) {
        MicrophoneRecorder recorder = null;

        int i=0;
        do
        {
            recorder = new MicrophoneRecorder();

        } while(!(recorder.getState() == MicrophoneRecorder.State.INITIALIZING));
        return recorder;
    }


    public class AudioReadResult {
        public int sampleRead;
        public short[] buffer;
        public long timeStamp;
    }

    private List<AudioDataArrivedEventListener> dataCollectors = new ArrayList<AudioDataArrivedEventListener>();

    private MicrophoneRecorder(){
        super();
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                SAMPLING_FREQUENCY,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        initRecorder();
        recorder.setRecordPositionUpdateListener(updateListener);

         if(recorder.getState()== AudioRecord.STATE_INITIALIZED){
             state = State.INITIALIZING;
         }
    }

    public int getBufferSizeInBytes(){
        return this.bufferSizeInBytes;
    }

    public int getSleepInterval(){
        return this.sleepInterval;
    }

    public State getState()
    {
        return state;
    }

    public void startRecord(){
        try{
            this.isRecording = false;
            if(this.recorder != null){
                if(recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                    return;
                }
            }else{
                if(initRecorder() == null)
                    return;
            }
            recorder.startRecording();
            this.isRecording = true;

        }catch(Exception ex){
            ex.printStackTrace();
            Log.d(Logger.TAG, ex.toString());
        }
    }


    public void stopRecord(){
        this.isRecording = false;
        recorder.stop();
    }

    protected void dataArrival(AudioReadResult result){
        Logger.log(Arrays.toString(result.buffer));
    }

    protected void onRecordEnded(){

    }


    private AudioRecord initRecorder(){
        try{

            if(recorder != null){
                recorder.release();
                recorder = null;
            }
            bufferSizeInBytes = AudioRecord.getMinBufferSize(
                    SAMPLING_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            this.recorder = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT,
                    SAMPLING_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes * 20);
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
            Log.d(Logger.TAG, ex.toString());
        }
        return this.recorder;
    }

    public interface AudioDataArrivedEventListener{
        void onNewDataArrived(AudioReadResult data);
    }

    public void registerDataListener(AudioDataArrivedEventListener dataCollector) {
        dataCollectors.add(dataCollector);
    }

    public void prepare()
    {
        try
        {
            if (state == State.INITIALIZING)
            {

                    if((recorder.getState() == AudioRecord.STATE_INITIALIZED))
                    {
//                        buffer = new byte[framePeriod*bSamples/8*nChannels];
                        state = State.READY;
                    }
                    else
                    {
                        Log.e(ExtAudioRecorder.class.getName(), "prepare() method called on uninitialized recorder");
                        state = State.ERROR;
                    }
            }
            else
            {
                Log.e(ExtAudioRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                state = State.ERROR;
            }
        }
        catch(Exception e)
        {
            if (e.getMessage() != null)
            {
                Log.e(ExtAudioRecorder.class.getName(), e.getMessage());
            }
            else
            {
                Log.e(ExtAudioRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            state = State.ERROR;
        }
    }

    public void release()
    {
        if (state == State.RECORDING)
        {
            stop();
        }
        else
        {
            if ((state == State.READY) )
            {
//                try
//                {
//                    if(randomAccessWriter!= null)randomAccessWriter.close(); // Remove prepared file
//                }
//                catch (IOException e)
//                {
//                    Log.e(ExtAudioRecorder.class.getName(), "I/O exception occured while closing output file");
//                }
//
//                if(filePath != null) (new File(filePath)).delete();
            }
        }



        if (recorder != null)
        {
            recorder.release();
        }

        dataCollectors.clear();
    }

    public void stop()
    {
        if (state == State.RECORDING)
        {
                try
                {
                    recorder.stop();
                }
                catch(Exception e)
                {
                    Log.e(ExtAudioRecorder.class.getName(), "I/O exception occured while closing output file");
                    state = State.ERROR;
                }
            state = State.STOPPED;
        }
        else
        {
            Log.e(ExtAudioRecorder.class.getName(), "stop() called on illegal state");
            state = State.ERROR;
        }
    }

    public void start()
    {
        if (state == State.READY)
        {
            recorder.startRecording();
            short[] buffer = new short[bufferSizeInBytes / 2];
            recorder.read(buffer, 0, buffer.length);
            state = State.RECORDING;
        }
        else
        {
            Log.e(ExtAudioRecorder.class.getName(), "start() called on illegal state");
            state = State.ERROR;
        }
    }

    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener()
    {
        public void onPeriodicNotification(AudioRecord recorder)
        {
            short[] buffer = new short[bufferSizeInBytes / 2];
            int numSamplesRead = recorder.read(buffer, 0, buffer.length); // Fill buffer
            if(numSamplesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                return;
            }
            else if(numSamplesRead == AudioRecord.ERROR_BAD_VALUE) {
                return;
            }

            AudioReadResult result = new AudioReadResult();
            result.buffer = buffer;
            result.sampleRead = numSamplesRead;
            result.timeStamp = System.currentTimeMillis();

            Logger.log("Got a result" + result.timeStamp);
            dataArrival(result);
        }

        public void onMarkerReached(AudioRecord recorder)
        {
            // NOT USED
            Log.d("EXTAudioRecorder",recorder.toString());
        }
    };
}