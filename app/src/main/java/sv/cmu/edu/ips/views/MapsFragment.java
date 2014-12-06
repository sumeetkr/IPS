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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.LabelData;
import sv.cmu.edu.ips.service.WEALocationProvider;
import sv.cmu.edu.ips.util.Constants;
import sv.cmu.edu.ips.util.Logger;

import static sv.cmu.edu.ips.data.LabelData.Source;

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
    private Handler handler;
    private TextView locationText;
    private WEALocationProvider locationProvider;
    private ToggleButton btnGPS;
    private ToggleButton btnIR;
    private ToggleButton btnWiFi;
    private ToggleButton btnCompass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);
        locationText = (TextView) view.findViewById(R.id.txtLocation);
        btnGPS = (ToggleButton) view.findViewById(R.id.btnGPS);
        btnIR = (ToggleButton) view.findViewById(R.id.btnIR);
        btnWiFi = (ToggleButton) view.findViewById(R.id.btnWiFi);
        btnCompass = (ToggleButton) view.findViewById(R.id.btnCompass);

//        latitude = 37.410372;
//        longitude = -122.059683;

        createMapIfNeeded(); // For setting up the MapFragment
        handler = new Handler();
        mMessageReceiver = new NewLabelReceiver(handler);
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
            locationProvider= new WEALocationProvider(getActivity()){
                @Override
                public void onLocationChanged(Location location)
                {
                    super.onLocationChanged(location);

                    if(getListener() != null)
                    {
                        final Location bestLocation = getBestLocation();
                        if(bestLocation!= null && !bestLocation.getProvider().isEmpty()){
                            try{
                                Bundle extra = bestLocation.getExtras();
                                if(extra!= null && extra.containsKey("label")){
                                    final String room =  extra.getString("label");
                                    if(room!= null && !room.isEmpty()){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateLocationInfo(room , bestLocation);
                                            }
                                        });
                                    }
                                }

                            }catch (Exception ex){
                                Logger.log(ex.getMessage());
                            }
                            getListener().onLocationChanged(bestLocation);
                        }
                    }
                }
            };
            map.setLocationSource(locationProvider);

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
                new IntentFilter(Constants.NEW_DATA));

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

        locationText.setText("Location: NA, Go to menu to collect data.");
    }

    public void updateLocationInfo(String room, Location location) {
        locationText.setText("Your location: " +room + "    Accuracy: " + location.getAccuracy() + "m");
        Source source =  LabelData.getSourceFromString(location.getProvider());

        btnGPS.setChecked(false);
        btnIR.setChecked(false);
        btnWiFi.setChecked(false);
        btnCompass.setChecked(false);

        switch (source){
            case GPS:
                btnGPS.setChecked(true);
                break;

            case WiFi:
                btnWiFi.setChecked(true);
                break;

            case Compass:
                btnCompass.setChecked(true);
                break;

            case IR:
                btnIR.setChecked(true);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
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

    public class NewLabelReceiver extends BroadcastReceiver {
        private final Handler handler;
        private String lastSentBeaconId ="";

        public NewLabelReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Extract data included in the Intent
            final String beaconId = intent.getStringExtra("message");
            final LabelData label = (LabelData) intent.getSerializableExtra("LabelData");

            if(beaconId != null && !beaconId.isEmpty() ){ //&& beaconId.compareTo(lastSentBeaconId)!=0
                // Post the UI updating code to our Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Found Beacon: " + beaconId, Toast.LENGTH_SHORT).show();
                    }
                });
                lastSentBeaconId = beaconId;
            }else{
                try{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(label!= null && label.getLat()!=0 && label.getLng()!=0){
                                Location improvedLocation = new Location("IPS");
                                improvedLocation.setAccuracy((float) label.getAccuracyInMeter());
                                improvedLocation.setLatitude(label.getLat());
                                improvedLocation.setLongitude(label.getLng());
                                improvedLocation.setProvider(label.getBestSource().name());

                                Bundle bundle = new Bundle();
                                bundle.putString("label", label.getRoomInfo());
                                improvedLocation.setExtras(bundle);

                                locationProvider.onLocationChanged(improvedLocation);
                            }
                        }
                    });
                }catch(Exception ex){
                    Logger.log(ex.getMessage());
                }
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
