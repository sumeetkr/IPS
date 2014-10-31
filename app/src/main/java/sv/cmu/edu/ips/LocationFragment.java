package sv.cmu.edu.ips;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.IPSHttpClient;

public class LocationFragment extends Fragment {

    private BroadcastReceiver mMessageReceiver;
    private BroadcastReceiver mLocationReceiver;
    private Handler handler;
    private final String LOCATION= "Your location: ";
    private final String SIGNAL_STRENGTH_CMU_IPS= "Signal Strength: CMU IPS ";
    private String beaconId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private WebView browser;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location, container, false);
        if (Constants.URL_TO_DISPLAY_LOCATION != null) {

            WebView wv = (WebView) v.findViewById(R.id.webView);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.setWebViewClient(new MyBrowser());
            wv.loadUrl(Constants.URL_TO_DISPLAY_LOCATION);

        }

        handler = new Handler();
        mMessageReceiver = new NewBeaconReceiver(handler);
        mLocationReceiver = new NewLocationReceiver(handler);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));

        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).registerReceiver(mLocationReceiver,
                new IntentFilter("new-location-event"));

//        updateLocation(beaconId);
    }

    private void updateLocation(final String location){

        if(location != null && !location.isEmpty() ){
            Log.d("IPS", "Changing location for beacon id: " + beaconId + " location: " + location);

            final TextView view = (TextView) this.getView().findViewById(R.id.txtLocation);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String [] locs = location.split("\"");
                    int len = locs.length;
                    String loc = "Not Available - Update using Beacons tab";
                    if(locs.length > 1){
                        loc = locs[len -2];
                    }else{
                        if(location.length() > 3){
                            loc = location;
                        }
                    }
                    view.setText(LOCATION + loc);
                }
            });
        }

    }

    @Override
    public void onPause(){
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(mLocationReceiver);
        super.onPause();

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public class NewBeaconReceiver extends BroadcastReceiver {
        private final Handler handler;
        private String lastSentBeaconId ="";

        public NewBeaconReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Extract data included in the Intent
            Log.d("IPS", "new beacon for location fragment");
            final String beaconId = intent.getStringExtra("message");
            if(beaconId != null && !beaconId.isEmpty() && beaconId.compareTo(lastSentBeaconId)!=0){
                IPSHttpClient.getDataFromServer(getActivity(), Constants.URL_TO_GET_LOCATION);
                lastSentBeaconId = beaconId;
            }
        }

    };

    public class NewLocationReceiver extends BroadcastReceiver {
        private final Handler handler;

        public NewLocationReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Extract data included in the Intent
            Log.d("IPS", "new location for location fragment");
            final String json = intent.getStringExtra("message");
            updateLocation(json);
        }
    };

}
