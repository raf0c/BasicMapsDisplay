package com.example.raf0c.basicmapsdisplay.beans;

/**
 * Created by raf0c on 16/06/15.
 */
public class RowItem {

    String name;
    int icon;

    public RowItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
