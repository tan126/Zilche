package com.zilche.zilche;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private SlideViewAdapter adapter;
    private ViewPager viewPager;
    private PagerTabStrip pts;
    private FloatingActionsMenu plusButton;
    private ImageView filter_bg;
    private boolean loginFlag;
    private HashMap<String, Integer> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().get("restart") != null) {
            Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }
        Zilche app = (Zilche) getApplication();

        int SELECTED_POSITION = 0;
        plusButton = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        adapter = new SlideViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        map = app.getMap();
        ImageView v = (ImageView) findViewById(R.id.loginArea);
        v.setClickable(true);
        // Load Username
        TextView pt = (TextView) findViewById(R.id.portfolio_text);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && (currentUser.getString("name") != null)) {
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
        actionA.setTitle(getString(R.string.new_poll));

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(MainActivity.this, CreatePollActivity2.class);
                startActivity(t);
                plusButton.collapse();
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

        String menu[] = {"All Posts", "My Poll", "My Profile", "Settings", "Log Out"};
        Integer imgID[] = {R.drawable.ic_dashboard_white_24dp, R.mipmap.ic_assignment_white_24dp, R.mipmap.ic_assignment_ind_white_24dp,
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
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }, 250);
                    myDrawer.closeDrawer(Gravity.LEFT);

                }
                else if (v.getText() == "My Profile"){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, MyProfileActivity.class);
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
                    Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        myDrawer.setDrawerShadow(R.drawable.shadow, GravityCompat.START);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_list);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void onBackPressed() {
        if (plusButton.isExpanded()) {
            plusButton.collapse();
        } else {
            super.onBackPressed();
        }
    }

    public class SlideViewAdapter extends FragmentPagerAdapter {

        String titles[] = {getString(R.string.categories), getString(R.string.newest_posts), getString(R.string.popular_posts)};

        public SlideViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if(position == 0){
                fragment = new CategoryFragment();
            }
            else if (position == 1){
                fragment = new NewestFragment();
            }
            else if (position == 2){
                fragment = new PopularFragment();
            }
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

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.80f;
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
        Intent i = new Intent(this, CreatePollActivity2.class);
        startActivity(i);
        plusButton.collapse();
    }

    @Override
    public void onResume() {
        super.onResume();
        Zilche app = (Zilche) getApplication();
        if (app.getFav() == null || app.getMap() == null) {
            Intent i = new Intent(this, SplashScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void openProfile(View v) {
        Intent i = new Intent(this, MyProfileActivity.class);
        startActivity(i);
    }

}