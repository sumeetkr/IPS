package sv.cmu.edu.ips.views;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.List;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.pipeline.BasicPipeline;
import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.service.dataCollectors.SensorDataCollector;
import sv.cmu.edu.ips.util.IPSFileWriter;
import sv.cmu.edu.ips.util.UserInputManager;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DataCollectDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DataCollectListFragment} and the item details
 * (if present) is a {@link DataCollectDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link DataCollectListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class DataCollectListActivity extends FragmentActivity
        implements DataCollectListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private DataCollectListFragment listFragment;
    private ProgressBar mProgress;
    private int progressStatus = 0;
    private Button startButton;
    private FunfManager funfManager;
    private BasicPipeline pipeline;
    public static final String PIPELINE_NAME = "default";
    private Handler handler = new Handler();
    private final String LABEL_BUTTON_TEXT = "Add Label";
    private final String START_BUTTON_TEXT = "Start Collection";
    private final String COLLECTING_BUTTON_TEXT = "Collecting Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datacollect_list);

        if (findViewById(R.id.datacollect_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((DataCollectListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.datacollect_list))
                    .setActivateOnItemClick(true);
        }


        listFragment = ((DataCollectListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.datacollect_list));

        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setText(START_BUTTON_TEXT);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;

                if(btn.getText() == START_BUTTON_TEXT){
                    btn.setText(COLLECTING_BUTTON_TEXT);
                    handler.postDelayed(runnable, 100);
                    final List<SensorDataCollector> probes = listFragment.getActiveSensorProbes();
                    // Start lengthy operation in a background thread
                    for(int i=0; i<probes.size(); i++){
                        final int finalI = i;
                        Thread collector = new Thread(new Runnable() {
                            public void run() {
                                Gson gson = funfManager.getGson();
                                probes.get(finalI).collectData(gson);

                                handler.post(new Runnable() {
                                    public void run() {

                                    }
                                });


                            }
                        });
//                    collector.setPriority(100);
                        collector.start();
                    }
                }else if(btn.getText() == LABEL_BUTTON_TEXT){
                    labelData();
                }
            }
        });

        // Bind to the service, to create the connection with FunfManager
        bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
    }

    private Runnable runnable = new Runnable() {
        int currentCount = 0;
        int totalCount = 100;
        @Override
        public void run() {
            progressStatus = (currentCount *100)/totalCount;
            mProgress.setProgress(progressStatus);

            try {
                Thread.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(currentCount<totalCount){
                currentCount = currentCount +1;
                handler.postDelayed(this, 500);
            }else{
                currentCount = 0;
                startButton.setText(LABEL_BUTTON_TEXT);
                labelData();
            }
        }
    };

    /**
     * Callback method from {@link DataCollectListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DataCollectDetailFragment.ARG_ITEM_ID, id);
            DataCollectDetailFragment fragment = new DataCollectDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.datacollect_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DataCollectDetailActivity.class);
            detailIntent.putExtra(DataCollectDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder)service).getManager();
            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;
        }
    };

    @Override
    protected void onDestroy(){
        if(pipeline != null) pipeline.onDestroy();
        funfManager = null;
        funfManagerConn = null;
        super.onDestroy();
    }

    private void labelData(){
        UserInputManager uim = new UserInputManager();
        Handler.Callback callback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                applyLabel(String.valueOf(msg.obj));
                return true;
            }
        };
        uim.getLabel(this, "Label the sound", callback);
        if(mProgress != null) mProgress.setProgress(0);
    }

    private void applyLabel(String value) {
        String labelString = value + "_"+ System.currentTimeMillis();

        // TODO - need to move it as event handler
        if (labelString != "") {
            IPSFileWriter.renameTempFolder(labelString);
        }

        startButton.setText(START_BUTTON_TEXT);
    }
}
