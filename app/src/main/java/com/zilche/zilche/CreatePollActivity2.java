package com.zilche.zilche;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CreatePollActivity2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll_activity2);
        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip);
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager_createpoll);
    }

}
