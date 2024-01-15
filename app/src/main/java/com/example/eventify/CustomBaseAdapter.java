package com.example.eventify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    String sideMenuItems[];
    int sideMenuIcons[];
    LayoutInflater inflater;

    public CustomBaseAdapter(Context context, String[] sideMenuItems, int[] sideMenuIcons) {
        this.context = context;
        this.sideMenuItems = sideMenuItems;
        this.sideMenuIcons = sideMenuIcons;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sideMenuItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view, null);
        TextView textView = (TextView) convertView.findViewById(R.id.sideMenu_item);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageIcon);
        textView.setText(sideMenuItems[position]);
        imageView.setImageResource(sideMenuIcons[position]);
        return convertView;
    }
}
