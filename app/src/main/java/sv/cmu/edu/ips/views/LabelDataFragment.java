package sv.cmu.edu.ips.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.util.Logger;


/**
 * A fragment representing a single Alert detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link LabelDataActivity}
 * on handsets.
 */
public class LabelDataFragment extends Fragment {
    private GoogleMap mMap;
    private View rootView;
    private LocationManager locationManager;
    private LatLng location;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_label_data_detail, container, false);

        return rootView;
    }

    private void setupView(){
        addEventListenersToButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupView();
        setUpMapIfNeeded();
        alertUserWithVibrationAndSpeech();
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
                    EditText txtFloorNo = (EditText) rootView.findViewById(R.id.editTextFloorNo);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("room",txtRoomNo.getText().toString());
                    resultIntent.putExtra("floor",txtFloorNo.getText().toString());
                    resultIntent.putExtra("lat", location.latitude);
                    resultIntent.putExtra("lng", location.longitude);
                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

        }
    }

    private void alertUserWithVibrationAndSpeech() {

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
                            mMap.addMarker(new MarkerOptions()
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

        CameraUpdate zoom= CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel()-2);
        mMap.animateCamera(zoom);

        drawPolygon();
    }

    private void drawPolygon(){
        try{
            if(mMap!=null){
                PolygonOptions polyOptions = new PolygonOptions()
                        .strokeColor(Color.RED);

//                GeoLocation[] locations = alert.getPolygon();
//
//                if(locations != null & locations.length>2){
//                    for(GeoLocation location:locations){
//                        polyOptions.add(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng())));
//                    }
//
//                    mMap.addPolygon(polyOptions);
//                    setCenter();
//                }
            }
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }
}
