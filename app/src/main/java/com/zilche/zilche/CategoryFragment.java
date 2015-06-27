package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.categories_grid);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float px10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
        grid.setColumnWidth((int)((displayMetrics.widthPixels - px10 * 3) / 2));
        grid.setAdapter(new ImageAdapter(getActivity()));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // redirect to results activity
                Toast.makeText(getActivity().getBaseContext(), Integer.toString((int)view.getTag()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {

        private Context c;
        private Integer[] thumbnails = {
            R.drawable.car
        };
        private Integer[] strings = {
                R.string.category_all, R.string.category_auto, R.string.category_entertainment, R.string.category_fashion,
                R.string.category_food, R.string.category_games, R.string.category_it, R.string.category_other,
                R.string.category_pet, R.string.category_sports, R.string.category_social, R.string.category_tech,
                R.string.category_travel
        };

        public ImageAdapter(Context c) {
            this.c = c;
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.categories, null);
            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
            TextView tv = (TextView) convertView.findViewById(R.id.category_text);
            tv.setText(c.getResources().getString(strings[position]));
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            iv.setImageResource(thumbnails[0]);
            iv.setAdjustViewBounds(true);
            convertView.setTag(position);
            return convertView;
        }
    }


}
