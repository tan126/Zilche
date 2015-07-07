package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
                R.drawable.category_all, R.drawable.category_auto, R.drawable.category_edu, R.drawable.category_enter,
                R.drawable.category_fash, R.drawable.category_finance, R.drawable.category_food, R.drawable.category_games,
                R.drawable.category_it, R.drawable.category_pets, R.drawable.category_sci, R.drawable.category_social,
                R.drawable.category_sports, R.drawable.category_tech, R.drawable.category_travel
        };
        private Integer[] strings = {
                R.string.category_all, R.string.category_auto, R.string.category_education, R.string.category_entertainment,
                R.string.category_fashion, R.string.category_finance, R.string.category_food, R.string.category_games, R.string.category_it,
                R.string.category_pet, R.string.category_science, R.string.category_social, R.string.category_sports, R.string.category_tech,
                R.string.category_travel
        };
        private int[] bg_color = {
                0xff42baff, 0xffff6259, 0xff835bd4, 0xffab48cf, 0xffced93b, 0xffffa321, 0xffffe53d, 0xffababab, 0xff6dcf71, 0xff997368,
                0xff22b3a2, 0xff24dbf0, 0xffff4284, 0xffffa321, 0xff809dab
        };
        /*private Integer[] bg_color = {
                R.drawable.bg_blue, R.drawable.bg_red, R.drawable.bg_dpurple, R.drawable.bg_purple, R.drawable.bg_lime,
                R.drawable.bg_dorange, R.drawable.bg_yellow, R.drawable.bg_grey, R.drawable.bg_green, R.drawable.bg_brown,
                R.drawable.bg_teal, R.drawable.bg_cyan, R.drawable.bg_pink, R.drawable.bg_orange, R.drawable.bg_bgrey
        };*/

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
            ImageView bg = (ImageView) convertView.findViewById(R.id.imageViewBg);
            tv.setText(c.getResources().getString(strings[position]));
            iv.setImageResource(thumbnails[position]);
            bg.setBackgroundColor(bg_color[position]);
            //bg.setImageResource(bg_color[position]);
            //bg.setAdjustViewBounds(true);
            convertView.setTag(position);
            return convertView;
        }
    }


}
