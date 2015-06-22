package com.example.raf0c.basicmapsdisplay.threadsmaps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;
import com.google.android.gms.maps.GoogleMap;
import com.example.raf0c.basicmapsdisplay.utils.Http;

import java.util.List;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    TextView texto;
    List<RowPlaceItem> lista;
    Context context;
    ListView listota;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            context = (Context) inputObj[2];
            listota = (ListView) inputObj[3];
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
        Object[] toPass = new Object[4];
        toPass[0] = googleMap;
        toPass[1] = result;
        toPass[2] = context;
        toPass[3] = listota;
        placesDisplayTask.execute(toPass);
    }
}