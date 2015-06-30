package com.zilche.zilche;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SignUpActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager_login);
        vp.setAdapter(new SignUpFragmentAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(2);
        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip2);
        stl.setDistributeEvenly(true);
        stl.setHorizontalScrollBarEnabled(false);
        stl.setCustomTabView(R.layout.custom_tab, 0);
        stl.setSelectedIndicatorColors(0xffffffff);
        stl.setViewPager(vp);

    }

    private class SignUpFragmentAdapter extends FragmentPagerAdapter {

        private String[] title = {getString(R.string.existing_user), getString(R.string.new_user)};

        public SignUpFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0 :
                    frag = new LoginFragment();
                    break;
                case 1 :
                    frag = new RegisterFragment();
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

    public static class LoginFragment extends Fragment {

        private Button loginBtn;
        private Button fbBtn;
        private View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_btn: {
                        break;
                    }
                    case R.id.login_fb_btn: {
                        break;
                    }
                }
            }
        };

        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.fragment_login, container, false);
            loginBtn = (Button) rootView.findViewById(R.id.login_btn);
            loginBtn.setOnClickListener(buttonListener);
            fbBtn = (Button) rootView.findViewById(R.id.login_fb_btn);
           fbBtn.setOnClickListener(buttonListener);
            return rootView;
        }
    }

    public static class RegisterFragment extends Fragment {
        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.fragment_register, container, false);
            return rootView;
        }
    }

    public void backButtonLogin(View v) {
        finish();
    }


}
