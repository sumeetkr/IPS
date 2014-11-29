package sv.cmu.edu.ips.views;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.service.IRDataGathererService;
import sv.cmu.edu.ips.util.Logger;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener , MapsFragment.OnFragmentInteractionListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private IRDataGathererService mBoundService;
    private boolean mIsBound;
    private BroadcastReceiver mMessageReceiver;
    private String deviceId;
    public static android.support.v4.app.FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.hide();

        fragmentManager = getSupportFragmentManager();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setTabListener(this));
//            .setText(mSectionsPagerAdapter.getPageTitle(i))
        }


        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        mMessageReceiver = new NewBeaconReceiver(new Handler(), deviceId);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_collect_data) {
            Intent intent = new Intent(getApplicationContext(), DataCollectListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((IRDataGathererService.LocalBinder)service).getService();
            Intent startServiceIntent = new Intent();
            mBoundService.onStartCommand(startServiceIntent,0,0);
            Log.d(Logger.TAG," Service Connected and started");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.

            mBoundService.stopSelf();
            mBoundService = null;
            Log.d(Logger.TAG, "Service disconnected");
        }
    };

    void doBindService() {
        bindService(new Intent(this,
                IRDataGathererService.class),
                mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            stopService(new Intent(this,
                    IRDataGathererService.class));
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        doBindService();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));

        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        doUnbindService();

        super.onPause();
    }

    public android.support.v4.app.FragmentManager getMapFragmentManager(){
          return getSupportFragmentManager();
    }

    protected void onNewBeaconFound(String beaconId){
//        SignalFragment signalFrag = (SignalFragment)getFragmentManager().findFragmentById(1);
//        BeaconsFragment signalFrag = (BeaconsFragment) mSectionsPagerAdapter.getItem(2);
//        if(signalFrag != null){
//            signalFrag.updateNewBeaconId(beaconId);
//        }
    }

    public class NewBeaconReceiver extends BroadcastReceiver {
        private final Handler handler;
        private final String deviceId;
        private String lastSentBeaconId ="";

        public NewBeaconReceiver(Handler handler, String deviceId) {
            this.handler = handler;
            this.deviceId = deviceId;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Extract data included in the Intent
            final String beaconId = intent.getStringExtra("message");

            if(beaconId != null && !beaconId.isEmpty() ){ //&& beaconId.compareTo(lastSentBeaconId)!=0
                // Post the UI updating code to our Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Found New Beacon: " + beaconId, Toast.LENGTH_SHORT).show();
                    }
                });

                onNewBeaconFound(beaconId);
//                sendNewBeaconIdToServer(context, beaconId);
                lastSentBeaconId = beaconId;
            }
        }

//        private void sendNewBeaconIdToServer(Context context, String beaconId) {
//            ScannerData data = new ScannerData();
//            data.setBeaconId(beaconId);
//            data.setIdentifier(this.deviceId);
//            Log.d("Sending Json ", data.getJSON());
//            IPSHttpClient.sendToServer(data.getJSON(), context, Constants.URL_TO_SEND_SCANNER_DATA);
//            Log.d("receiver", "Sent beacon id: " + beaconId);
//        }
    };

}
