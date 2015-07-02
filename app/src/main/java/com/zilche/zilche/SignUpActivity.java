package com.zilche.zilche;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void backButtonLogin(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        private EditText email;
        private EditText password;
        private TextView forgotPassword;

        private View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.login_btn: {
                        loginBtn.setClickable(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(loginBtn.getWindowToken(), 0);
                        String userName = email.getText().toString();
                        String passWord = password.getText().toString();
                        boolean focus_first = false;
                        boolean failed = false;
                        if (!isEmailValid(userName)) {
                            focus_first = true;
                            email.setError(getString(R.string.invalid_email));
                            failed = true;
                        }
                        if (passWord.length() < 6) {
                            password.setError(getString(R.string.password_too_short));
                            failed = true;
                        }
                        if (failed) {
                            if (!focus_first) {
                                password.requestFocus();
                            } else {
                                email.requestFocus();
                            }
                            loginBtn.setClickable(true);
                            break;
                        }
                        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().finish();
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    loginBtn.setClickable(true);
                                }
                            }
                        });
                        break;
                    }

                    case R.id.login_fb_btn: {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Developing...");

                        builder1.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        break;
                    }
                    case R.id.forgot_pw: {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Developing...");

                        builder1.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
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
            email = (EditText) rootView.findViewById(R.id.login_email);
            password = (EditText) rootView.findViewById(R.id.login_password);
            forgotPassword = (TextView) rootView.findViewById(R.id.forgot_pw);
            forgotPassword.setOnClickListener(buttonListener);
            return rootView;
        }

    }

    public static class RegisterFragment extends Fragment {

        private TextView flname;
        private EditText email;
        private EditText password;
        private Button registerButton;
        private Button fbRegisterButton;

        private View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.regis_btn: {
                        registerButton.setClickable(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);
                        String userEmail = email.getText().toString();
                        String userPassword = password.getText().toString();
                        String userName = flname.getText().toString();
                        boolean failed = false;
                        int err = 0;
                        if (userName.length() < 2) {
                            failed = true;
                            err = 1;
                            flname.setError(getString(R.string.fl_name));
                        }
                        if (!isEmailValid(userEmail)) {
                            failed = true;
                            if (err == 0) err = 2;
                            email.setError(getString(R.string.invalid_email));
                        }
                        if (userPassword.length() < 6) {
                            failed = true;
                            if (err == 0) err = 3;
                            password.setError(getString(R.string.password_too_short));
                        }
                        if (failed) {
                            if (err == 1) flname.requestFocus();
                            else if (err == 2) email.requestFocus();
                            else password.requestFocus();
                            registerButton.setClickable(true);
                            break;
                        }
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(userEmail);
                        newUser.setPassword(userPassword);
                        newUser.setEmail(userEmail);
                        newUser.put("name", userName);
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().finish();
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    registerButton.setClickable(true);
                                }
                            }
                        });
                        break;
                    }

                    case R.id.regis_fb_btn: {
                        break;
                    }
                }
            }
        };

        public View onCreateView(LayoutInflater inf, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inf.inflate(R.layout.fragment_register, container, false);
            registerButton = (Button) rootView.findViewById(R.id.regis_btn);
            registerButton.setOnClickListener(buttonListener);
            fbRegisterButton = (Button) rootView.findViewById(R.id.regis_fb_btn);
            fbRegisterButton.setOnClickListener(buttonListener);
            email = (EditText) rootView.findViewById(R.id.register_email);
            password = (EditText) rootView.findViewById(R.id.register_password);
            flname = (TextView) rootView.findViewById(R.id.flname);
            return rootView;
        }


    }


}
