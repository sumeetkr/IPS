package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/10/14.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.LinkedList;

public class MicrophoneRecorder extends Thread {

    private static final String CLASS_PREFIX = MicrophoneRecorder.class.getName();
    private AudioRecord recorder = null;
    private int bufferSizeInBytes = 0;
    private int sleepInterval = 0;
    private Object syncObject = new Object();
    private boolean isRecording = false;
    private int noOfSnippetsToRecord =5;

    public static final int SAMPLING_FREQUENCY = 44100;


    private class ReadResult{
        public int sampleRead;
        public short[] buffer;
        public long timeStamp;
    }

    private LinkedList<ReadResult> queue = new LinkedList<ReadResult>();
    private Thread consumerThread = new Thread(){
        public void run(){
            while(true){
                ReadResult top = null;

                synchronized(syncObject){

                    if(queue.size() > 0){
                        top = queue.poll();
                    }else{
                        if(!isRecording){
                            onRecordEnded();
                            Log.i(CLASS_PREFIX, "Consumer thread ended.");
                            break;
                        }
                    }
                }

                if(top != null){
                    dataArrival(top.timeStamp, top.buffer, top.sampleRead, top.buffer.length);
                }

                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };

    public MicrophoneRecorder(){
        super();
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                SAMPLING_FREQUENCY,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        //this.sleepInterval = sleepInterval;
    }

    public int getBufferSizeInBytes(){
        return this.bufferSizeInBytes;
    }

    public int getSleepInterval(){
        return this.sleepInterval;
    }



    public void run(){
        this.consumerThread.start();

        int snippetsReadCount =0;
        while(isRecording && bufferSizeInBytes > 0) {

            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING)
                break;

            try{
                short[] buffer = new short[bufferSizeInBytes / 2];
                //short[] buffer = new short[bufferSizeInBytes * 10];
                int numSamplesRead = recorder.read(buffer, 0, buffer.length);

                if(numSamplesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    continue;
                }
                else if(numSamplesRead == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }

                ReadResult result = new ReadResult();
                result.buffer = buffer;
                result.sampleRead = numSamplesRead;
                result.timeStamp = System.currentTimeMillis();

                snippetsReadCount = snippetsReadCount +1;
                synchronized(this.syncObject){
                    queue.add(result);
                }

                if(snippetsReadCount > noOfSnippetsToRecord){
                    stopRecord();
                }
            }catch(Exception recordException){
                Logger.log(recordException.toString());
                recordException.printStackTrace();

            }

        }

        this.recorder.stop();
        this.recorder.release();

    }

    public void startRecord(int noOfSnippets){
        try{
            this.isRecording = false;
            this.noOfSnippetsToRecord = noOfSnippets;
            if(this.recorder != null){
                if(recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                    return;
                }
            }else{
                if(initRecorder() == null)
                    return;
            }
            recorder.startRecording();
            this.start();
            this.isRecording = true;

        }catch(Exception ex){
            ex.printStackTrace();
            Logger.log(ex.toString());
        }
    }


    public void stopRecord(){
        this.isRecording = false;
    }

    protected void dataArrival(long timestamp, short[] data, int length, int frameLength){

    }

    protected void onRecordEnded(){

    }


    private AudioRecord initRecorder(){
        try{
            bufferSizeInBytes = AudioRecord.getMinBufferSize(
                    SAMPLING_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            this.recorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLING_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes * 20);
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
            Logger.log(ex.getMessage());
        }
        return this.recorder;
    }
}