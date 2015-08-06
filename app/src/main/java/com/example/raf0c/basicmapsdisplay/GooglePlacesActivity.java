package com.example.raf0c.basicmapsdisplay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.adapter.PlacesListAdapter;
import com.example.raf0c.basicmapsdisplay.beans.RowItem;
import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;
import com.example.raf0c.basicmapsdisplay.fragments.NavigationDrawerFragment;
import com.example.raf0c.basicmapsdisplay.threadsmaps.GooglePlacesReadTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by raf0c on 22/06/15.
 */


public class GooglePlacesActivity extends ActionBarActivity implements LocationListener, NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    public MapFragment mapFragment;
    public LinearLayout myLayout;
    public Inflater inflater;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private View mFragmentContainerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        cafe = "cafe";
        restaurant = "restaurant";
        bar = "bar";
        //placeText = (EditText) findViewById(R.id.placeText);
        txt = (TextView) findViewById(R.id.description);

        //Button btnFind = (Button) findViewById(R.id.btnFind);
        menutitles      = getResources().getStringArray(R.array.titles);
        menuIcons       = getResources().obtainTypedArray(R.array.icons);
        mDrawerLayout   =  mNavigationDrawerFragment.mDrawerLayout;
        mDrawerList     = mNavigationDrawerFragment.mDrawerListView;
        mOptionsList = mNavigationDrawerFragment.mDrawerResultsList;
        mFragmentContainerView = mNavigationDrawerFragment.mFragmentContainerView;

        rowItems        = new ArrayList<>();
        rowOptions = new ArrayList<>();
        context = getApplicationContext();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set activity on portrait only

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();

        //googleMap = fragment.getMap();
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((GooglePlacesActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
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
        Object[] toPass = new Object[6];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        toPass[4] = mDrawerLayout;
        toPass[5] = mFragmentContainerView;
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
        Object[] toPass = new Object[6];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        toPass[4] = mDrawerLayout;
        toPass[5] = mFragmentContainerView;
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
        Object[] toPass = new Object[6];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = context;
        toPass[3] = mOptionsList;
        toPass[4] = mDrawerLayout;
        toPass[5] = mFragmentContainerView;
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