package com.example.raf0c.basicmapsdisplay;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.adapter.PlacesListAdapter;
import com.example.raf0c.basicmapsdisplay.beans.RowItem;
import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;
import com.example.raf0c.basicmapsdisplay.threadsmaps.GooglePlacesReadTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class GooglePlacesActivity extends FragmentActivity implements LocationListener {

    private static final String GOOGLE_API_KEY = "AIzaSyDLOFWhrKy-tSpw6KpfAkM25spt98JQ2jw";
    GoogleMap googleMap;
    EditText placeText;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 5000;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView mOptionsList;
    private List<RowItem> rowItems;
    public List<RowPlaceItem> rowOptions;

    private PlacesListAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;

    public String cafe;
    public String restaurant;
    public String bar;

    public Context context;

    String[] menutitles;
    TypedArray menuIcons;
    public TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);
        cafe = "cafe";
        restaurant = "restaurant";
        bar = "bar";
        //placeText = (EditText) findViewById(R.id.placeText);
        txt = (TextView) findViewById(R.id.description);

        //Button btnFind = (Button) findViewById(R.id.btnFind);
        menutitles      = getResources().getStringArray(R.array.titles);
        menuIcons       = getResources().obtainTypedArray(R.array.icons);
        mDrawerLayout   = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList     = (ListView) findViewById(R.id.list_slidermenu);
        mOptionsList = (ListView) findViewById(R.id.list_options);
        rowItems        = new ArrayList<>();
        rowOptions = new ArrayList<>();
        context = getApplicationContext();

        for (int i = 0; i < menutitles.length; i++) {
            RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(i, -1));
            rowItems.add(items);
        }

        menuIcons.recycle();
        adapter = new PlacesListAdapter(getApplicationContext(), rowItems);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                // R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };


        mDrawerList.setAdapter(adapter);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set activity on portrait only

        googleMap = fragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {

                updateSearch(position);

            }

            private void updateSearch(int position) {

                switch (position) {
                    case 0://Cafeterias
                        getCafe();
                        mOptionsList.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        getRestaurant();
                        mOptionsList.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        getBar();
                        mOptionsList.setVisibility(View.VISIBLE);
                        break;
                }
            }

        });
    }

    protected void getCafe(){
        String type = cafe;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        System.out.println("The URL is : " + googlePlacesUrl.toString());
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[4];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        googlePlacesReadTask.execute(toPass);
    }

    protected void getRestaurant(){
        String type = restaurant;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        System.out.println("The URL is : " + googlePlacesUrl.toString());
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[4];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        googlePlacesReadTask.execute(toPass);
    }

    protected void getBar(){
        String type = bar;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        System.out.println("The URL is : " + googlePlacesUrl.toString());
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[4];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        googlePlacesReadTask.execute(toPass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();
            googleMap = getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public GoogleMap getMap() {
        if (googleMap == null)
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        return googleMap;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
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
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}