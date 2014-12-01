package sv.cmu.edu.ips.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.LabelData;
import sv.cmu.edu.ips.util.IPSFileReader;
import sv.cmu.edu.ips.util.Logger;


/**
 * A fragment representing a single Alert detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link LabelDataActivity}
 * on handsets.
 */
public class LabelDataFragment extends Fragment{
    private GoogleMap mMap;
    private View rootView;
    private LocationManager locationManager;
    private LatLng location;
    private SensorManager sensorManager = null;
    private float currentDegree = 0f;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Float azimut;
    private TextView txtOrientation;
    private Marker currentMarker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)  getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_label_data_detail, container, false);
        EditText txtRoomNo = (EditText) rootView.findViewById(R.id.editTextRoomNo);
        final Button btnFeedback = (Button) rootView.findViewById(R.id.buttonFeedback);

        txtRoomNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != "") {
                    btnFeedback.setEnabled(true);
                } else {
                    btnFeedback.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    private void setupView(){
//        txtOrientation = (TextView) rootView.findViewById(R.id.txtOrientation);
        addEventListenersToButtons();

        Intent activityIntent = getActivity().getIntent();
        if(activityIntent.hasExtra("beaconId")){
            String beaconId = activityIntent.getStringExtra("beaconId");
            if(beaconId != null && beaconId.length()>0){
                TextView txtBeaconId = (TextView) rootView.findViewById(R.id.txtBeconId);
                txtBeaconId.setText(beaconId);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupView();
        setUpMapIfNeeded();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addEventListenersToButtons() {
        Button btnFeedback = (Button) rootView.findViewById(R.id.buttonFeedback);
        if(btnFeedback != null){
            btnFeedback.setOnClickListener(new View.OnClickListener() {
                final Activity activity = getActivity();
                @Override
                public void onClick(View v) {
                    EditText txtRoomNo = (EditText) rootView.findViewById(R.id.editTextRoomNo);

                    if(txtRoomNo.getText() != null && txtRoomNo.getText().toString() != ""){
                        EditText txtX = (EditText) rootView.findViewById(R.id.txtXValue);
                        EditText txtY = (EditText) rootView.findViewById(R.id.txtYValue);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("room",txtRoomNo.getText().toString());

                        if(location != null){
                            resultIntent.putExtra("lat", location.latitude);
                            resultIntent.putExtra("lng", location.longitude);
                        }

                        if(txtX.getText() != null && txtX.getText().toString().length()>0 &&
                                txtY.getText() != null && txtY.getText().toString().length()>0){
                            try{
                                resultIntent.putExtra("x", Double.parseDouble(txtX.getText().toString()));
                                resultIntent.putExtra("y", Double.parseDouble(txtY.getText().toString()));
                            }catch(Exception ex){
                                Logger.log(ex.getMessage());
                            }
                        }

                        activity.setResult(Activity.RESULT_OK, resultIntent);
                        activity.finish();
                    }
                }
            });

        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            try{
                Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
                mMap = ((SupportMapFragment) fragment).getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();

                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {

                            mMap.setMyLocationEnabled(true);

                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.getUiSettings().setCompassEnabled(true);

                            if(locationManager == null){
                                locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                            }
                            if(locationManager != null){
                                Criteria criteria = new Criteria();
                                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                                if (location != null)
                                {
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                                    mMap.moveCamera(center);
                                }
                            }

                            addLabeledDataPoints();
                        }
                    });

                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                        @Override
                        public void onCameraChange(CameraPosition arg0) {
//                            Location location = mMap.getMyLocation();
//                            if (location != null) {
//                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//                                mMap.moveCamera(center);
//                            }
//                            // Remove listener to prevent position reset on camera move.
                            mMap.setOnCameraChangeListener(null);
                        }
                    });

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            location = latLng;
                            mMap.clear();

                            if(currentMarker != null){
                                currentMarker.remove();
                            }

                            currentMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("Selected location"));

                            TextView txtCoordinate = (TextView) rootView.findViewById(R.id.txtCoordinatesSelected);
                            txtCoordinate.setText(latLng.latitude + ", " + latLng.longitude);
                        }
                    });

                }
            }catch(Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }

    private void setUpMap() {
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        CameraUpdate zoom= CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel()-2);
        mMap.animateCamera(zoom);

    }

    private void addLabeledDataPoints(){
        try{
            if(mMap!=null){
                List<LabelData> labels = IPSFileReader.getLabelData();
                for(LabelData label: labels){
                    LatLng latLng = new LatLng(label.getLat(), label.getLng());
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
                            .alpha(0.3f)
                            .title(label.getRoomInfo()));
                }
            }
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }
}
