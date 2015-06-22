package com.example.raf0c.basicmapsdisplay.threadsmaps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.utils.Places;
import com.example.raf0c.basicmapsdisplay.adapter.PlaceFinalAdapter;
import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    private List<RowPlaceItem> rowItems;
    public RowPlaceItem item;

    public TextView txt ;
    private PlaceFinalAdapter adapter;
    public Context context;
    public ListView myList;
    private String placeName;


    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();
        try {

            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            context = (Context) inputObj[2];
            myList = (ListView) inputObj[3];
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
         ArrayList<String> itemstring = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            placeName = googlePlace.get("place_name");
            itemstring.add(i,placeName);
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            googleMap.addMarker(markerOptions);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,android.R.id.text1,itemstring);
        myList.setAdapter(adapter);
        myList.setVisibility(View.VISIBLE);
    }
}

