package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
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
        grid.setColumnWidth((int) ((displayMetrics.widthPixels - px10 * 3) / 2));
        grid.setAdapter(new ImageAdapter(getActivity()));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), CategoryActivity.class);
                i.putExtra("category_index", position);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_to_left, 0);
            }
        });
        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {

        private Context c;
        private int[] thumbnails = {
                R.drawable.category_all, R.drawable.category_auto, R.drawable.category_edu, R.drawable.category_enter,
                R.drawable.category_fash, R.drawable.category_finance, R.drawable.category_food, R.drawable.category_games,
                R.drawable.category_it, R.drawable.category_pets, R.drawable.category_sci, R.drawable.category_social,
                R.drawable.category_sports, R.drawable.category_tech, R.drawable.category_travel
        };
        private int[] strings = {
                R.string.category_all, R.string.category_auto, R.string.category_education, R.string.category_entertainment,
                R.string.category_fashion, R.string.category_finance, R.string.category_food, R.string.category_games, R.string.category_it,
                R.string.category_pet, R.string.category_science, R.string.category_social, R.string.category_sports, R.string.category_tech,
                R.string.category_travel
        };
        private int[] bg_color = {
                0xff42baff, 0xffff6259, 0xff835bd4, 0xffab48cf, 0xffced93b, 0xffff8554, 0xffffe53d, 0xffababab, 0xff6dcf71, 0xff997368,
                0xff22b3a2, 0xff24dbf0, 0xffff4284, 0xffffa321, 0xff809dab
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
            ImageView bg = (ImageView) convertView.findViewById(R.id.imageViewBg);
            tv.setText(c.getResources().getString(strings[position]));
            iv.setTag(position);
            new BitmapLoader(iv, thumbnails[position], position, bg, bg_color[position]).execute();
            convertView.setTag(position);
            return convertView;
        }
    }

    public class BitmapLoader extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> image;
        private int cat;
        private int position;
        private final WeakReference<ImageView> bg;
        private int bgc;

        public BitmapLoader(ImageView iv, int cat, int pos, ImageView bg, int bgc) {
            image = new WeakReference<ImageView>(iv);
            this.cat = cat;
            position = pos;
            this.bg = new WeakReference<ImageView>(bg);
            this.bgc = bgc;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapFactory.decodeResource(getResources(), cat);
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            if (isCancelled() || bm == null || image.get() == null) return;
            if ((int)(image.get().getTag()) != position) return;
            image.get().setImageBitmap(bm);
            bg.get().setBackgroundColor(bgc);
        }

    }

}
