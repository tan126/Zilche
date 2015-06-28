package com.zilche.zilche;

/**
 * Created by khe on 6/27/2015.
 */ import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;

public class create_poll extends Activity {

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_poll);
        viewPager = (ViewPager)findViewById(R.id.pageViewer);
        MyPageAdapter myPageAdapter = new MyPageAdapter();
        viewPager.setAdapter(myPageAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
// TODO Auto-generated method stub
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
// TODO Auto-generated method stub
            }
        });
    }

    class MyPageAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(View container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = null;
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.layout.create_poll_1;
                    view = inflater.inflate(resId, null);
                    ((ViewPager) container).addView(view, 0);

                    break;
                case 1:
                    resId = R.layout.create_poll_1;
                    view = inflater.inflate(resId, null);
                    ((ViewPager) container).addView(view, 0);


                    break;
                case 2:
                    resId = R.layout.create_poll_1;
                    view = inflater.inflate(resId, null);
                    ((ViewPager) container).addView(view, 0);


                    break;
            }

            return view;
        }
        @Override
        public int getCount() {
// TODO Auto-generated method stub
            return 3;
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }
//a
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        public Parcelable saveState() {
            return null;
        }
        // public int getItemPosition(Object object) { return POSITION_NONE; }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }
    }

}