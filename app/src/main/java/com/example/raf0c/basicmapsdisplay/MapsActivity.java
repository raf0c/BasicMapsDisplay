package com.example.raf0c.basicmapsdisplay;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.Filter;
import android.widget.Filterable;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {



    private static final String LOG_TAG = "Google P : ";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyDLOFWhrKy-tSpw6KpfAkM25spt98JQ2jw";

    private GoogleMap mMap;
    Marker mCurrent;
    LatLng mPosition;
    LatLng mPositiontemp;

    boolean mCenterOnLocation;

    private Button btnFindMe = null;
    private TextView textLocation = null;
    private ProgressBar probar =null;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Boolean gpsPrendido = false;
    private static final String TAG = "Consola : ";

    public GooglePlacesAutocompleteAdapter obj;

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
        textLocation        = (TextView) findViewById(R.id.eTlocation);
        btnFindMe           = (Button) findViewById(R.id.btnFindme);
        locationMangaer     = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set activity on portrait only
        btnFindMe.setOnClickListener(this);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);

        obj = new GooglePlacesAutocompleteAdapter(this,R.layout.list_item);
        /*
        * Initialize Progress Bar not Visible, when the user interacts, make it visible
        * */
        probar.setVisibility(View.INVISIBLE);
        }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        Geocoder gc = new Geocoder(getBaseContext());

        try {
            if(gc.isPresent()){
                List<Address> list = gc.getFromLocationName(str, 1);

                Address address = list.get(0);

                double lat = address.getLatitude();
                double lng = address.getLongitude();

                mPositiontemp = new LatLng(address.getLatitude(), address.getLongitude());

                mCenterOnLocation = true;
                mCurrent = mMap.addMarker(new MarkerOptions().position(mPositiontemp).title(list.get(0).getLocality()));

                //Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(mPosition.latitude, mPosition.longitude), new LatLng(mPositiontemp.latitude, mPositiontemp.longitude)).width(2)
                    //    .color(Color.BLUE).geodesic(true));

                centerOnLocation(mPositiontemp, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {
                    }
                });


            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(mPosition.latitude, mPosition.longitude), new LatLng(mPositiontemp.latitude, mPositiontemp.longitude)).width(2)
                        .color(Color.BLUE).geodesic(true));
            }

        }
        catch (JSONException e) {

        }
    }
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));


            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }



        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {

                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
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

            textLocation.setText("");
            probar.setVisibility(View.INVISIBLE);


            Toast.makeText(getBaseContext(), "You moved! you are now in : Latitud: " + loc.getLatitude() + " Longitud: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitud: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitud: " + loc.getLatitude();
            Log.v(TAG, latitude);
            mPosition = new LatLng(loc.getLatitude(), loc.getLongitude());

            String s = longitude + "\n" + latitude;
            textLocation.setText(s);
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

    public void centerOnLocation(LatLng position, GoogleMap.CancelableCallback callback) {

        if (mCenterOnLocation && mMap != null) {
            mCenterOnLocation = false;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12), 1500, callback);
        }
    }

    public GoogleMap getMap() {
        if (mMap == null)
            mMap = ((com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        return mMap;
    }

}