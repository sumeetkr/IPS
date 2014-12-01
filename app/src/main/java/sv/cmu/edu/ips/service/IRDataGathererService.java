package sv.cmu.edu.ips.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.List;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.ClassificationData;
import sv.cmu.edu.ips.data.LabelData;
import sv.cmu.edu.ips.learners.WiFiProximityLearner;
import sv.cmu.edu.ips.service.dataCollectors.AudioDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.WiFiSensorDataCollector;
import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.IPSFileReader;
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
    private AudioDataCollector audioDataCollector;
    private WiFiSensorDataCollector wiFiDataCollector;
    private String logLabel = "IRDataGathererService";
    private boolean isDataToBeWrittenToFile = false;
    private Runnable runnableStartLookingForData;
    private  String beaconId ="";
    private IPSDataGatherer dataGatherer;
    private List<ClassificationData> previouslyCollectedData;
    private int  previouslyCollectedDataCount=0;
    private Runnable runnableGetClassifierData;


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

        runnableGetClassifierData = new Runnable() {
            @Override
            public void run() {
                previouslyCollectedData = IPSFileReader.getClassificationData();
                previouslyCollectedDataCount = IPSFileReader.getCountOfDataCollected();
            }
        };

        runnableGetClassifierData.run();

        handler = new Handler();
        handler.postDelayed(runnableStartLookingForData, 100);

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
        IntentFilter intentFilter = new IntentFilter(Constants.DATA_COLLECTION_FINISHED);
        this.registerReceiver(dataCollectionBroadcastReceiver, intentFilter);

        isListening = true;

        runnableStartLookingForData = new Runnable() {
            @Override
            public void run() {
                if(isListening){
//                    if(!isRecording){
                        isRecording = true;
                        startCollecting();
                        Logger.log("Recording started! :");
//                    }
                    //stop recording in 200ms
                    handler.postDelayed(this, 5000);
                }
            }
        };

        return mBinder;
    }

    @Override
    public  boolean onUnbind(Intent intent){
        this.unregisterReceiver(dataCollectionBroadcastReceiver);

        isListening= false;
        runnableStartLookingForData = null;
        return true;
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);
        Toast.makeText(this, text, LENGTH_SHORT).show();
        Logger.log(text.toString());
    }

    private void startCollecting() {

        dataGatherer = new IPSDataGatherer(getApplicationContext());
        dataGatherer.startCollecting();
        Logger.log("started previouslyCollectedData collection");
    }

    private BroadcastReceiver dataCollectionBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String collectorName = intent.getStringExtra(Constants.SENSOR_TYPE);
            if(intent.hasExtra("beaconId")){
                beaconId = intent.getStringExtra("beaconId");
            }else{
                //for now only collecting WiFi
                if(dataGatherer!= null){
                   ClassificationData newClassificationData = dataGatherer.getClassificationData();
                   if(newClassificationData.getWifiData() != null){
                       LabelData label = WiFiProximityLearner.findNearestLabel(
                                previouslyCollectedData,
                               newClassificationData);

                       Intent newIntent = new Intent(Constants.NEW_DATA);
                       newIntent.putExtra("LabelData", label);
                       LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
                   }

                    if(previouslyCollectedDataCount != IPSFileReader.getCountOfDataCollected()){
                        runnableGetClassifierData.run();
                    }
                }
            }

            Logger.log("Service received finish of " + collectorName);
        }
    };
}