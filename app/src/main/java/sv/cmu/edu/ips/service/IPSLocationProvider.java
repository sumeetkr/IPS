package sv.cmu.edu.ips.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.gms.maps.LocationSource;

/**
 * Created by sumeet on 11/15/14.
 */
public class IPSLocationProvider implements LocationSource, LocationListener {
        private OnLocationChangedListener listener;
        private LocationManager locationManager;
        private Location lastLocation;

        public IPSLocationProvider(Context context)
        {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        @Override
        public void activate(OnLocationChangedListener listener)
        {
            this.listener = listener;

            LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            if(gpsProvider != null)
            {
                locationManager.requestLocationUpdates(gpsProvider.getName(), 0, 10, this);
            }

            LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);;
            if(networkProvider != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 30, 0, this);
            }
        }

        @Override
        public void deactivate()
        {
            locationManager.removeUpdates(this);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            if(listener != null)
            {
                listener.onLocationChanged(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // TODO Auto-generated method stub

        }
}
