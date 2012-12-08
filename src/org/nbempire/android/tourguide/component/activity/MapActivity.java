/*
 * Copyright (c) 2012 Nahuel Barrios <barrios.nahuel@gmail.com>.
 * No se reconocerá ningún tipo de garantía.
 */

package org.nbempire.android.tourguide.component.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import org.nbempire.android.tourguide.R;

/**
 * Land-Activity of this application. It contains the main app screen where users can see the map with all its layers.
 *
 * @author Nahuel Barrios.
 * @since 1
 */
public class MapActivity extends FragmentActivity {

    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;

    /**
     * Tag for class' log.
     */
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //  TODO : Performance : Should I leave only this call to the method? Review "Activities lifecycle" topic.
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly installed) and the map has not already been
     * instantiated.. This will ensure that we only ever call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user
     * to install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this Activity after following the prompt and correctly installing/updating/enabling the Google Play services. Since
     * the Activity may not have been completely destroyed during this process (it is likely that it would only be stopped or paused), {@link
     * #onCreate(Bundle)} may not be called again so we should call this method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                           .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            } else {
                //  TODO : Functionality : Close app when the user doesn't install/upgrade Google Play Services and display an error message.
                Log.i(TAG, "The application will be closed because of the device is unable to run without the Google Play Services API.");
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        getCurrentLocation();

        //  TODO : Functionality : show layers.
    }

    /**
     * Gets the current location from {@link #mMap} enabling MyLocation layer when needed. If there's no location data available then shows an
     * error message to the user.
     */
    private void getCurrentLocation() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            LocationListener locationListener = createLocationListener(locationManager);

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            //  TODO : Functionality : Show an error to the user.
            Log.e(TAG, "The retrieved locationManager is null.");
        }
    }

    /**
     * TODO : Javadoc for createLocationListener
     *
     * @param locationManager
     *
     * @return
     */
    private LocationListener createLocationListener(final LocationManager locationManager) {
        // Define a listener that responds to location updates
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null) {
                    Log.e(TAG, "location is null.");
                } else {
                    Log.i(TAG, "Current location is: (" + location.getLatitude() + ", " + location.getLongitude() + ")");

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition position =
                            new CameraPosition.Builder().target(currentLocation)
                                    .zoom(mMap.getMaxZoomLevel() - 2)
                                    .bearing(0)
                                    .tilt((float) 67.5)
                                    .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),
                                              new GoogleMap.CancelableCallback() {
                                                  @Override
                                                  public void onFinish() {
                                                      //  TODO : Functionality : remove theese toasts.
                                                      Toast.makeText(getBaseContext(), "Animation complete", Toast.LENGTH_SHORT).show();
                                                  }

                                                  @Override
                                                  public void onCancel() {
                                                      Toast.makeText(getBaseContext(), "Animation canceled", Toast.LENGTH_SHORT).show();
                                                  }
                                              });
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //  TODO : Functionality : do something onStatusChanged for a provider.
                Log.e(TAG, "status changed for provider: " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e(TAG, "Enabled provider: " + s);
                locationManager.requestLocationUpdates(s, 0, 0, this);
            }

            @Override
            public void onProviderDisabled(String s) {
                //  TODO : Functionality : do something onProviderDisabled
                Log.e(TAG, "Disabled provider: " + s);
            }
        };
    }

}
