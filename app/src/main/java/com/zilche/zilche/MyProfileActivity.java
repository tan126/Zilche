package com.zilche.zilche;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MyProfileActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new ProfileFragmentAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);

        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip2);
        stl.setDistributeEvenly(true);
        stl.setHorizontalScrollBarEnabled(false);
        stl.setCustomTabView(R.layout.custom_tab, 0);
        stl.setSelectedIndicatorColors(0xffffffff);
        stl.setViewPager(pager);

        onPageSelected(0);


        TextView usernameText;
        usernameText = (TextView) findViewById(R.id.userName);
        ParseUser currentUser = ParseUser.getCurrentUser();
        usernameText.setText(currentUser.getString("name"));

        //firstFrag = new FirstFragment();
        //secondFrag = new SecondFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
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

    public void backButtonProfile(View v) {
        finish();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ProfileFragmentAdapter extends FragmentPagerAdapter {

        private String[] title = {"About", "Activity"};

        public ProfileFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0 :
                    frag = new FirstFragment();
                    break;
                case 1 :
                    frag = new SecondFragment();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }



    public static class FirstFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.profile_about, container, false);
            return v;
        }

        public FirstFragment() {

        }
    }

    public static class SecondFragment extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.profile_activity, container, false);



            return v;
        }

        public SecondFragment() {

        }

    }




}

