package com.example.raf0c.basicmapsdisplay;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap;
    Marker mCurrent;
    LatLng mPosition;
    boolean mCenterOnLocation;

    private Button btnFindMe = null;
    private EditText editLocation = null;
    private ProgressBar probar =null;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Boolean gpsPrendido = false;
    private static final String TAG = "Consola : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        initElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {


    }

    @SuppressWarnings("deprecation")
    private void initElements(){

        probar              = (ProgressBar) findViewById(R.id.proBar);
        editLocation        = (EditText) findViewById(R.id.eTlocation);
        btnFindMe           = (Button) findViewById(R.id.btnFindme);
        locationMangaer     = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set activity on portrait only
        btnFindMe.setOnClickListener(this);
        /*
        * Initialize Progress Bar not Visible, when the user interacts, make it visible
        * */
        probar.setVisibility(View.INVISIBLE);
        }

    /*
    * Check if Gps is on or not
    * */
    @SuppressWarnings("deprecation")
    private Boolean gpsProvider() {

        ContentResolver contentResolver = getBaseContext().getContentResolver();

        /*
        * Method deprecated, ask David for a better option
        * */
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);

        if (gpsStatus)
            return true;
        else
            return false;

    }


    /*
       * We override the method onClick from the interface OnClickListener
       * */
    @Override
    public void onClick(View v) {

        gpsPrendido = gpsProvider();

        if (gpsPrendido) {
            /*
            * Set ProgressBar to Visible
            * */
            probar.setVisibility(View.VISIBLE);

            //Creates a locationlistener object
            locationListener = new MyLocationListener();

            //Use requestLocationUpdates method passing the provider, the interval time, the minimum distance and the object listener of the class implementing locationlistener
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

            mCenterOnLocation = true;


        } else {
            alertbox("GPS", "Please tur on your gsp");
        }

    }

    /*
    * Method to create a Custom Alert
    * */
    protected void alertbox(String title, String mymessage) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("NO GPS Available").setCancelable(false).setTitle("GPS").setPositiveButton("Gps On", new DialogInterface.OnClickListener() {

            /*
            * Custome ONCLICK
            * */
            public void onClick(DialogInterface dialog, int id) {

                Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(myIntent);
                dialog.cancel();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
     * Classe to find the coordinates given, and showing the name of the city,
      * What this does is to get the coordinates with the parameter loc
      * then creates a Geocoder to translates the coordinates in the possible Address
      * Then we have a List of Address, this array is being feed with all the translates that the geocoder is making, then,
      * basing on this list it is filled with the most accurate address to the less accurate and takes the first one as being the best one.
     * */
    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");
            probar.setVisibility(View.INVISIBLE);


            Toast.makeText(getBaseContext(), "You moved! you are now in : Latitud: " + loc.getLatitude() + " Longitud: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitud: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitud: " + loc.getLatitude();
            Log.v(TAG, latitude);
            mPosition = new LatLng(loc.getLatitude(), loc.getLongitude());


            /*
            * We create a Geocoder, the geocoder will be able to translates the coordinates
            * */
            String city = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                }

                city = addresses.get(0).getLocality();

            } catch (IOException e) {

                e.printStackTrace();

            }

            String s = longitude + "\n" + latitude;
            editLocation.setText(s);
            mCurrent = mMap.addMarker(new MarkerOptions().position(mPosition).title("You are here !"));


            mCenterOnLocation = true;

            centerOnLocation(mPosition, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {
                }
            });

        }


        public void centerOnLocation(LatLng position, GoogleMap.CancelableCallback callback) {

            if (mCenterOnLocation && mMap != null) {
                mCenterOnLocation = false;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12), 1500, callback);
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }


    public GoogleMap getMap() {
        if (mMap == null)
            mMap = ((com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        return mMap;
    }

}