package sv.cmu.edu.ips.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.service.dataCollectors.AudioDataCollector;
import sv.cmu.edu.ips.util.Logger;

import static android.widget.Toast.LENGTH_SHORT;

public class IRDataGathererService extends Service {

    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private boolean isRecording = false;
    private  boolean isListening = false;
    private Handler handler;
    private AudioDataCollector dataRecorder;
    private String logLabel = "IRDataGathererService";
    private boolean isDataToBeWrittenToFile = false;
    private Runnable runnable;


    public IRDataGathererService() {
        isRecording = false;
    }

    public class LocalBinder extends Binder {
        public IRDataGathererService getService() {
            return IRDataGathererService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler();
        handler.postDelayed(runnable, 100);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
//        timer.cancel();
//        Log.d("IRDataGathererService", "TimerTask stopped! :");

        Toast.makeText(this, R.string.local_service_stopped, LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        isListening = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if(isListening){
//                    if(!isRecording){
                        isRecording = true;
                        startCollecting();
                        Logger.log("Recording started! :");
//                    }
                    //stop recording in 200ms
                    handler.postDelayed(this, 3000);
                }
            }
        };

        return mBinder;
    }

    @Override
    public  boolean onUnbind(Intent intent){
        isListening= false;
        runnable = null;
        return true;
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);
        Toast.makeText(this, text, LENGTH_SHORT).show();
        Logger.log(text.toString());
    }

    private void startCollecting() {
        dataRecorder = new AudioDataCollector("1", "AudioData");
        dataRecorder.collectData(getApplicationContext(), new Gson(), false);
        Log.d(logLabel, "started recording");
    }
}