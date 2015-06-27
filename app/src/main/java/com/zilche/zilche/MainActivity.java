package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    SlideViewAdapter adapter;
    ViewPager viewPager;
    PagerTabStrip pts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new SlideViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        pts = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        pts.setTextColor(0xffffffff);
        pts.setDrawFullUnderline(true);
        pts.setTabIndicatorColor(0xffffffff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setTitle("New Poll");
        FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setTitle("New Survey");

    }
    public void newPoll(View v) {
        setContentView(R.layout.create_poll); // temporary redirect
    }

    public void newSurvey(View v) {
        setContentView(R.layout.activity_login); // temporary redirect
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LinearLayout searchBar = (LinearLayout) findViewById(R.id.my_search_bar);
        ImageView searchIcon = (ImageView) findViewById(R.id.searchicon);
        searchIcon.setColorFilter(0x66ffffff, PorterDuff.Mode.MULTIPLY);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchList.class);
                startActivity(i);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public class SlideViewAdapter extends FragmentPagerAdapter {

        String titles[] = {getString(R.string.categories), getString(R.string.newest_posts), getString(R.string.popular_posts)};

        public SlideViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if(position == 0){
                fragment = new categories();
            }
            else {
                fragment = new placeHolder();
            }
            Bundle args = new Bundle();
            args.putInt(placeHolder.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

    //fragment for categories
    public static class categories extends Fragment {
        public static final String ARG_OBJECT = "object";

        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.categories, container, false);
            Bundle args = getArguments();
            //((TextView) rootView.findViewById(R.id.textview)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
    }

    public static class placeHolder extends Fragment {
        public static final String ARG_OBJECT = "object";

        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.placeholder, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
    }
}