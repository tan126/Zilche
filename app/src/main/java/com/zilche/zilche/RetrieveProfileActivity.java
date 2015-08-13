package com.zilche.zilche;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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


public class RetrieveProfileActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ViewPager pager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_profile);

        pager = (ViewPager) findViewById(R.id.viewPager);

        pager.setPageTransformer(false, new ZoomOutPageTransformer());
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
        usernameText.setText(getIntent().getStringExtra("authorRealName"));



       // Toast.makeText(getApplicationContext(), userNameTest.toString(), Toast.LENGTH_SHORT).show();

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

        private TextView emailText;
        private TextView messageText;
        private EditText editMessageField;
        private TextView genderText;
        private TextView countryText;

        private String username;
        private String userMessage;
        private String userEmail;
        private String userGender;
        private String userCountry;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.profile_about2, container, false);

            Bundle args = getActivity().getIntent().getExtras();
            String userNameTest = args.getString("authorName");
            //Toast.makeText(getActivity(), userNameTest, Toast.LENGTH_SHORT).show();
            //System.out.println(userNameTest);

            //ParseQuery<ParseUser> query = ParseUser.getQuery();
            //query.whereEqualTo("username", userNameTest);
           // query.findInBackground(new FindCallback<ParseUser{
                //public void done(List<ParseUser> list, ParseException e) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", userNameTest);
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> list, ParseException e) {
                    if (e == null) {
                        //System.out.println(list.size());
                        ParseUser thisUser = list.get(0);
                        username = thisUser.getString("name");
                        userMessage = thisUser.getString("message");
                        userEmail = thisUser.getString("email");
                        userGender = thisUser.getString("gender");
                        userCountry = thisUser.getString("country");


                        emailText = (TextView) v.findViewById(R.id.userEmail);
                        emailText.setText(userEmail);

                        messageText = (TextView) v.findViewById(R.id.userMessage);
                        messageText.setText(userMessage);


                        genderText = (TextView) v.findViewById(R.id.userGender);
                        if(userGender != null)
                            genderText.setText(userGender);




                        countryText = (TextView) v.findViewById(R.id.userCountry);
                        if(userCountry != null)
                            countryText.setText(userCountry);




                    } else {
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });



            //final ParseUser currentUser = ParseUser.getCurrentUser();



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


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}

