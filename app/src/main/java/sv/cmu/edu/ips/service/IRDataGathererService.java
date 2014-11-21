package sv.cmu.edu.ips.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.SignalData;
import sv.cmu.edu.ips.service.dataCollectors.IRDataCollector;
import sv.cmu.edu.ips.util.Logger;
import sv.cmu.edu.ips.util.SignalAnalyzer;

import static android.widget.Toast.LENGTH_SHORT;

public class IRDataGathererService extends Service {

    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private static boolean isRecording = false;
    private Handler handler;
    private int dataCount;
    private boolean isListening = false;
    private String logLabel = "IRDataGathererService";


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
        isListening = true;
        handler.postDelayed(runnableGetData, 5000);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        stopRecording();

        Logger.log("Service onDestroy stopped! :");
        Toast.makeText(this, R.string.local_service_stopped, LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);
        Toast.makeText(this, text, LENGTH_SHORT).show();
        Log.d(Logger.TAG, String.valueOf(R.string.local_service_started));
    }


    private void stopRecording() {
        isListening = false;
        Log.d(logLabel, "stopped recording");
    }


    private Runnable runnableGetData = new Runnable() {
        @Override
        public void run() {
            IRDataCollector collector = new IRDataCollector("1", "IRDataCollector"){
                @Override
                public  void onDataCollectionFinished(){
                    super.releaseRecorder();

                    try {
                        List<Short> shorts = super.aggregateData();
                        short[] shortArray = new short[shorts.size()];
                        for(int i = 0; i<shorts.size(); i++){
                            shortArray[i] = shorts.get(i);
                        }

                        SignalData signal = SignalAnalyzer.getSignalInfoStringFromRawSignal(shortArray);

                        if(signal.getBeaconId() != null){
                            Logger.log("Beacon id" + signal.getBeaconId());
                            Logger.log("Signal amplitude " + String.valueOf(signal.getAmplitude()));
                            Toast.makeText(getApplicationContext(), signal.getBeaconId(), LENGTH_SHORT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(isListening) handler.postDelayed(runnableGetData, 2000);
                }
            };
            collector.collectData(getApplicationContext(), 1);
        }
    };

}
