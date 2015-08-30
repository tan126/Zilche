package com.zilche.zilche;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;
    private int current_position;

    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid, int current_position) {
        super(context, R.layout.listentry, itemname);

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.current_position = current_position;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listentry, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        imageView.setColorFilter(0xbbffffff, PorterDuff.Mode.MULTIPLY);
        switch(position){
            case 0:
                imageView.setColorFilter(Color.parseColor("#00BFA5"));
                rowView.setClickable(false);
                rowView.setSelected(true);
                break;
        }
        return rowView;

    };
}