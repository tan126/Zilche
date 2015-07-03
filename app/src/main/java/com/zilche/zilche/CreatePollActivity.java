package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

public class CreatePollActivity extends FragmentActivity implements OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ThirdFragment thirdFrag;
    int numOptions = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);

/*        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TextView textView = (TextView) findViewById(R.id.createpollfootertext);
                switch (position) {
                    case 0:
                        textView.setText("Next");
                        System.out.println("ONE");
                    case 1:
                        textView.setText("Next");
                        System.out.println("TWO");
                    case 2:
                        textView.setText("Submit");
                        System.out.println("THREE");
                    default:
                        textView.setText("Next");
                        System.out.println("DEFAULT");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        firstFrag = new FirstFragment();
        secondFrag = new SecondFragment();
        thirdFrag = new ThirdFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelCreatePoll(View v) {
        //finish();
        Intent i = new Intent(CreatePollActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TextView textView = (TextView) findViewById(R.id.createpollfootertext);
        switch (position) {
            case 0:
                textView.setText("Next");
                break;
            case 1:
                textView.setText("Next");
                break;
            case 2:
                textView.setText("Submit");
                break;
            default:
                textView.setText("Next");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0:
                    return firstFrag;
                case 1:
                    return secondFrag;
                case 2:
                    return thirdFrag;
                default:
                    return firstFrag;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class FirstFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_poll_1, container, false);
/*            TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
            tv.setText(getArguments().getString("msg"));*/

            return v;
        }

        public FirstFragment() {

/*            FirstFragment f = new FirstFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }
    }

    public static class SecondFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_poll_2, container, false);

/*            TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
            tv.setText(getArguments().getString("msg"));*/

            return v;
        }

        public SecondFragment() {

/*            SecondFragment f = new SecondFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }
    }

    public static class ThirdFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //View v = inflater.inflate(R.layout.create_poll_3, container, false);

/*            TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
            tv.setText(getArguments().getString("msg"));*/

            //return v;
            View rootView = inflater.inflate(R.layout.create_poll_3, container, false);
            GridView grid = (GridView) rootView.findViewById(R.id.poll_categories_grid);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float px10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
            grid.setColumnWidth((int)((displayMetrics.widthPixels - px10 * 3) / 3));
            grid.setAdapter(new ImageAdapter(getActivity()));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // redirect to results activity
                    Toast.makeText(getActivity().getBaseContext(), Integer.toString((int) view.getTag()),
                            Toast.LENGTH_SHORT).show();
                }
            });
            return rootView;
        }
        public class ImageAdapter extends BaseAdapter {

            private Context c;
            private Integer[] thumbnails = {
                    R.drawable.all2, R.drawable.car2, R.drawable.enter2, R.drawable.fashion2, R.drawable.food2,
                    R.drawable.games2, R.drawable.it2, R.drawable.pet2, R.drawable.science2, R.drawable.sport2,
                    R.drawable.social, R.drawable.tech2, R.drawable.travel
            };
            private Integer[] strings = {
                    R.string.category_all, R.string.category_auto, R.string.category_entertainment, R.string.category_fashion,
                    R.string.category_food, R.string.category_games, R.string.category_it,
                    R.string.category_pet, R.string.category_science, R.string.category_sports, R.string.category_social, R.string.category_tech,
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
                iv.setImageResource(thumbnails[position]);
                iv.setAdjustViewBounds(true);
                convertView.setTag(position);
                return convertView;
            }
        }
        public ThirdFragment() {

/*            ThirdFragment f = new ThirdFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }
    }

    public void addOption(View v) {
        if (numOptions >= 10) {
            Toast.makeText(getApplicationContext(), "Reached maximum of 10 options", Toast.LENGTH_SHORT).show();
            return;
        }
        numOptions++;
        String option = "option" + numOptions;
        String optionR = "option" + numOptions + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);

    }

    public void removeOption(View v) {

    }

}
