package com.zilche.zilche;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import android.widget.TextView;


public class CreatePollActivity extends FragmentActivity {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ThirdFragment thirdFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
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

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return firstFrag;
                case 1: return secondFrag;
                case 2: return thirdFrag;
                default: return firstFrag;
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
            View v = inflater.inflate(R.layout.create_poll_3, container, false);

/*            TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
            tv.setText(getArguments().getString("msg"));*/

            return v;
        }

        public ThirdFragment() {

/*            ThirdFragment f = new ThirdFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }
    }

}
