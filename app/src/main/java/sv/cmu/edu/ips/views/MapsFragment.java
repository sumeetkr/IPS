package sv.cmu.edu.ips.views;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.BeaconData;
import sv.cmu.edu.ips.service.IPSLocationProvider;
import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.IPSHttpClient;

public class MapsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private final String BEACON_ID = "Beacon Id: ";
    private final String LATITUDE = "Latitude: ";
    private final String LONGITUDE = "Longitude: ";
    private String beaconId = "Not Known";
    private BroadcastReceiver mMessageReceiver;
    private static View view;
    private static GoogleMap map;
    private static Double latitude, longitude;
    private String label;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);
//        latitude = 37.410372;
//        longitude = -122.059683;

        createMapIfNeeded(); // For setting up the MapFragment
        handler = new Handler();
        mMessageReceiver = new NewBeaconReceiver(handler);
        return view;
    }

    /***** Sets up the map if it is possible to do so *****/
    public void createMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.

            if(map != null){
                map.setIndoorEnabled(true);
                map.setBuildingsEnabled(true);
                map.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
                    @Override
                    public void onIndoorBuildingFocused() {
                        Toast.makeText(getActivity(), "onIndoorBuildingFocused", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
                        Toast.makeText(getActivity(), "onIndoorLevelActivated",Toast.LENGTH_SHORT);
                    }
                });

            }
        }
    }

    private void updateMapWithCurrentInfo() {
        if (map != null) {
            map = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.map)).getMap();

            map.setMyLocationEnabled(true);
            map.setLocationSource(new IPSLocationProvider(getActivity()));

            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    map.setMyLocationEnabled(true);

                    Location location = map.getMyLocation();

                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                                20));
                    }

                    // Remove listener to prevent position reset on camera move.
                    map.setOnCameraChangeListener(null);
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        createMapIfNeeded();
    }

    private void sendNewBeaconIdToServer(Context context) {
        if(beaconId != null && !beaconId.isEmpty() && label != null && !label.isEmpty()
                && String.valueOf(latitude) != null && !String.valueOf(longitude).isEmpty()){
            BeaconData data = new BeaconData(beaconId,String.valueOf(latitude), String.valueOf(longitude),label);
            Log.d("Sending beacon json info", data.getJSON());
            IPSHttpClient.sendToServer(data.getJSON(), context, Constants.URL_TO_SEND_BEACON_DATA);
            Log.d("receiver", "Sent beacon json for " + beaconId);
        }
    }
    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null) {
            try{
                MainActivity.fragmentManager.beginTransaction()
                        .remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
                map = null;
            }
            catch(Exception ex){
                Log.d("IPS", ex.getMessage());
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        createMapIfNeeded();
        updateMapWithCurrentInfo();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
        updateTextView();
    }

    @Override
    public void onPause(){

        if(map != null) map.setLocationSource(null);

        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(mMessageReceiver);
        super.onPause();

    }

    private void updateTextView() {
//        Log.d("IPS", "Setting up beacon id :" + beaconId);
//        TextView beaconIdText = (TextView)this.getView().findViewById(R.id.beaconId);
//        beaconIdText.setText(BEACON_ID + beaconId);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public void updateNewBeaconId(String beaconId){
        Log.d("IPS", "Updating new beacon id :" + beaconId);
        this.beaconId = beaconId;
    }

    public void addMarker(LatLng latlng){
        map.clear();
        map.addMarker(new MarkerOptions().position(latlng)
                .title(beaconId).snippet(" Bldg .., Room .."));
    }

    public void makeLabeltextFocussableForEdit(){
        Log.d("IPS", "Making label text focusable for edit :" );
//        EditText editText = (EditText) this.getView().findViewById(R.id.editText);
//        editText.setFocusable(true);

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
            final String beaconId = intent.getStringExtra("message");

            if(beaconId != null && !beaconId.isEmpty() && beaconId.compareTo(lastSentBeaconId)!=0){
                // Post the UI updating code to our Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateNewBeaconId(beaconId);
                        updateTextView();
                    }
                });
            }
        }

    };

    public class TouchListener implements com.google.android.gms.maps.GoogleMap.OnMapClickListener{

        private Handler handler;
        public TouchListener(Handler handler){
            this.handler = handler;
        }
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d("IPS", "Got touch event " + latLng);
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            addMarker(latLng);
            makeLabeltextFocussableForEdit();
        }
    }
}
