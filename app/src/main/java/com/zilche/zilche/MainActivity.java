package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {

    SlideViewAdapter adapter;
    ViewPager viewPager;
    PagerTabStrip pts;
    FloatingActionsMenu plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new SlideViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setPageTransformer(false, new FadePageTransformer());
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
        actionA.setTitle("New Survey");

        TextView email_text = (TextView) findViewById(R.id.portfolio_email);
        email_text.setText("example@purdue.edu");

        plusButton = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        String menu[] = {"Poll", "Survey", "Settings", "Log Out"};
        Integer imgID[] = {R.mipmap.ic_assignment_white_24dp, R.mipmap.ic_assessment_white_24dp,
                R.drawable.ic_settings_white_24dp, R.drawable.ic_power_settings_new_white_24dp};

        CustomListAdapter myadapter=new CustomListAdapter(this, menu, imgID);
        ListView list=(ListView)findViewById(R.id.menulist);
        list.setAdapter(myadapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final DrawerLayout myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        myDrawer.setDrawerShadow(R.drawable.shadow, GravityCompat.START);
        ImageView portfolio = (ImageView) findViewById(R.id.portfolio_img);
        portfolio.setColorFilter(Color.rgb(189, 189, 189));

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
                plusButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                plusButton.setVisibility(View.GONE);
            }
        });

    }

    public void loginAndSignup(View v) {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
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

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);

            if(position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }


}