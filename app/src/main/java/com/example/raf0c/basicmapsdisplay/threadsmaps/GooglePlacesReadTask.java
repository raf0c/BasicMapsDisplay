package com.example.raf0c.basicmapsdisplay.threadsmaps;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;
import com.google.android.gms.maps.GoogleMap;
import com.example.raf0c.basicmapsdisplay.utils.Http;

import java.util.List;

/**
 * Created by raf0c on 22/06/15.
 */

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    TextView texto;
    List<RowPlaceItem> lista;
    Context context;
    ListView listota;
    DrawerLayout drawerLayout;
    View mFragmentContainerView;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            context = (Context) inputObj[2];
            listota = (ListView) inputObj[3];
            drawerLayout = (DrawerLayout) inputObj[4];
            mFragmentContainerView = (View) inputObj[5];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[6];
        toPass[0] = googleMap;
        toPass[1] = result;
        toPass[2] = context;
        toPass[3] = listota;
        toPass[4] = drawerLayout;
        toPass[5] = mFragmentContainerView;
        placesDisplayTask.execute(toPass);
    }
}