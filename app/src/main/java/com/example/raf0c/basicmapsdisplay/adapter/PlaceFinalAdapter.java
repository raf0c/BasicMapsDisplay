package com.example.raf0c.basicmapsdisplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.raf0c.basicmapsdisplay.R;
import com.example.raf0c.basicmapsdisplay.beans.RowPlaceItem;

import java.util.List;

/**
 * Created by raf0c on 21/06/15.
 */
public class PlaceFinalAdapter extends BaseAdapter {


    Context context;
    List<RowPlaceItem> rowItem;

    public PlaceFinalAdapter(Context context, List<RowPlaceItem> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.list_item);
        RowPlaceItem row_pos = rowItem.get(position);

        // setting the image resource and title
        txtTitle.setText(row_pos.getName());
        return convertView;
    }

    @Override
    public int getCount() {
        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItem.indexOf(getItem(position));
    }

}
