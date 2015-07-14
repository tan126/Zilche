package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {

    SlideViewAdapter adapter;
    ViewPager viewPager;
    PagerTabStrip pts;
    FloatingActionsMenu plusButton;
    private ImageView filter_bg;
    boolean loginFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SELECTED_POSITION = 0;
        plusButton = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        adapter = new SlideViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        // not sure if this will cause problems
        viewPager.setOffscreenPageLimit(3);

        ImageView v = (ImageView) findViewById(R.id.loginArea);
        v.setClickable(true);
        // Load Username
        TextView pt = (TextView) findViewById(R.id.portfolio_text);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            String n = currentUser.getString("name");
            pt.setText("Welcome " + n);
            pt.setClickable(false);
            v.setClickable(false);
            loginFlag = true;
            plusButton.setVisibility(View.VISIBLE);

        } else {
            // show the signup or login screen
            SELECTED_POSITION = 1;
            loginFlag = false;
            plusButton.setVisibility(View.GONE);
        }

        pts = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        pts.setTextColor(0xffffffff);
        pts.setDrawFullUnderline(true);
        pts.setTabIndicatorColor(0xffffffff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter_bg = (ImageView) findViewById(R.id.filter_bg);
        filter_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plusButton != null)
                    plusButton.collapse();
            }
        });

        FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setTitle("New Poll");
        FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setTitle("New Survey");

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent t = new Intent(MainActivity.this, tmpNewPoll.class);
                Intent t = new Intent(MainActivity.this, CreatePollActivity.class);
                startActivity(t);
            }
        });

        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent t = new Intent(MainActivity.this, tmpNewPoll.class);
                Intent t = new Intent(MainActivity.this, CreateSurveyActivity.class);
                startActivity(t);
                //newSurvey();
            }
        });

        final DrawerLayout myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        plusButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                filter_bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                filter_bg.setVisibility(View.GONE);
            }
        });

        String menu[] = {"All Posts", "My Poll", "My Survey", "Settings", "Log Out"};
        Integer imgID[] = {R.drawable.ic_dashboard_white_24dp, R.mipmap.ic_assignment_white_24dp, R.mipmap.ic_assessment_white_24dp,
                R.drawable.ic_settings_white_24dp, R.drawable.ic_power_settings_new_white_24dp};

        CustomListAdapter myadapter=new CustomListAdapter(this, menu, imgID, SELECTED_POSITION);
        ListView list=(ListView)findViewById(R.id.menulist);
        list.setAdapter(myadapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.item);
                if (v.getText() == "My Poll") {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, MyPollActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }, 250);
                    myDrawer.closeDrawer(Gravity.LEFT);

                }
                else if (v.getText() == "Log Out") {
                    ParseUser u = new ParseUser();
                    u.logOut();
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        myDrawer.setDrawerShadow(R.drawable.shadow, GravityCompat.START);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_list);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //plusButton.setVisibility(View.INVISIBLE);
                myDrawer.openDrawer(Gravity.LEFT);
            }
        });

        myDrawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(loginFlag==true)
                    plusButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                if(loginFlag==true) {
                    plusButton.collapse();
                    plusButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(loginFlag==true)
                    if (slideOffset == 0) {
                        plusButton.setVisibility(View.VISIBLE);
                    } else if (plusButton.getVisibility() == View.VISIBLE) {
                        plusButton.collapse();
                        plusButton.setVisibility(View.GONE);
                    }
            }

        });

    }

    public void loginAndSignup(View v) {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
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
    public void onBackPressed() {
        if (plusButton.isExpanded()) {
            plusButton.collapse();
        } else {
            super.onBackPressed();
        }
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
                fragment = new CategoryFragment();
            }
            else if (position == 1){
                fragment = new NewestFragment();
            }
            else if (position == 2){
                fragment = new PopularFragment();
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

    public static class placeHolder extends Fragment {
        public static final String ARG_OBJECT = "object";

        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.placeholder, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
    }

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.99f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else {
                view.setAlpha(0);
            }
        }
    }

    public void newPoll(View v) {

        Intent i = new Intent(this, CreatePollActivity.class);
        startActivity(i);/*
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
    }

    public void newSurvey() {
        Intent i = new Intent(this, tmpNewPoll.class);
        startActivity(i);
    }
}