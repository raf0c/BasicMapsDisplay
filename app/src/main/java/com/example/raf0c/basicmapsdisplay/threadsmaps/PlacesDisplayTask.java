package com.example.raf0c.basicmapsdisplay.threadsmaps;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raf0c.basicmapsdisplay.beans.Destination;
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

/**
 * Created by raf0c on 22/06/15.
 */

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
    private DrawerLayout drawerLayout;
    private View theView;


    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();
        try {

            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            context = (Context) inputObj[2];
            myList = (ListView) inputObj[3];
            drawerLayout = (DrawerLayout) inputObj[4];
            theView = (View) inputObj[5];
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
        final ArrayList<Destination> arrayDestino = new ArrayList<>();

        Destination destino = new Destination(null,null,null,0);


        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            placeName = googlePlace.get("place_name");
            itemstring.add(i,placeName);
            arrayDestino.add(i,new Destination(placeName,lat,lng,i));
        }

       /* for (int i = 0; i < list.size(); i++) {
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
        }*/
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1,itemstring);
        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            MarkerOptions markerOptions = new MarkerOptions();

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {

                if(markerOptions.isVisible()){
                    googleMap.clear();
                    updateSearch(position);
                }else{
                    updateSearch(position);
                }
                drawerLayout.closeDrawer(theView);

            }

            private void updateSearch(int position) {

                for (int k = 0; k < arrayDestino.size(); k++) {
                    if (arrayDestino.get(k).getPosition() == position) {
                        System.out.println("Soy la position " + position + " me llamo : " + arrayDestino.get(k).getTitle());
                        LatLng latLng = new LatLng(arrayDestino.get(k).getLatitud(), arrayDestino.get(k).getLongitud());
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        googleMap.addMarker(markerOptions);
                    } else {
                        System.out.println("No encontre ni madres");
                    }
                }
            }

        });

        myList.setVisibility(View.VISIBLE);
    }
}

