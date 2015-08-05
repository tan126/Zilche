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
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.current_position = current_position;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listentry, null,true);

        if (position == 0)
            rowView.setBackgroundColor(Color.rgb(230, 230, 230));

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        imageView.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        extratxt.setText("Description " + itemname[position]);
        switch(itemname[position]){
            case "My Poll":
                extratxt.setText("Browse my polls");
                imageView.setColorFilter(Color.parseColor("#00BFA5"));
                rowView.setClickable(false);
                if (current_position == 1) {
                    imageView.setColorFilter(Color.parseColor("#BDBDBD"));
                    txtTitle.setTextColor(Color.parseColor("#BDBDBD"));
                    extratxt.setTextColor(Color.parseColor("#BDBDBD"));
                    rowView.setClickable(true);
                }
                break;
            case "My Profile":
                extratxt.setText("View my profile");
                imageView.setColorFilter(Color.parseColor("#F57F17"));
                rowView.setClickable(false);
                if (current_position == 1) {
                    imageView.setColorFilter(Color.parseColor("#BDBDBD"));
                    txtTitle.setTextColor(Color.parseColor("#BDBDBD"));
                    extratxt.setTextColor(Color.parseColor("#BDBDBD"));
                    rowView.setClickable(true);
                }
                break;
            case "Settings":
                extratxt.setText("Customize my settings");
                imageView.setColorFilter(Color.parseColor("#3E2723"));
                break;
            case "Log Out":
                extratxt.setText("Logging out or change user");
                rowView.setClickable(false);
                if (current_position == 1) {
                    imageView.setColorFilter(Color.parseColor("#BDBDBD"));
                    txtTitle.setTextColor(Color.parseColor("#BDBDBD"));
                    extratxt.setTextColor(Color.parseColor("#BDBDBD"));
                    rowView.setClickable(true);
                }
                break;
            case "All Posts":
                extratxt.setText("Browse all posts");
                imageView.setColorFilter(Color.parseColor("#AA00FF"));
                break;
        }

        return rowView;

    };
}