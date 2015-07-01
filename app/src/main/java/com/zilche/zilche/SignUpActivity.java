package com.zilche.zilche;

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
        private TextView login_err_text1;
        private TextView login_err_text2;

        private View.OnFocusChangeListener button_focus = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.login_email: {
                        if (!hasFocus) {
                            if (login_err_text1.getVisibility() == View.VISIBLE) {
                                if (isEmailValid(email.getText().toString())) {
                                    login_err_text1.setVisibility(View.GONE);
                                    email.getBackground().clearColorFilter();
                                }
                            }
                        }
                        break;
                    }
                }
            }
        };

        private View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.login_btn: {
                        loginBtn.setClickable(false);
                        String userName = email.getText().toString();
                        String passWord = password.getText().toString();
                        boolean focus_first = false;
                        boolean failed = false;
                        if (!isEmailValid(userName)) {
                            login_err_text1.setVisibility(View.VISIBLE);
                            focus_first = true;
                            email.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                            failed = true;
                        } else {
                            if (login_err_text1.getVisibility() == View.VISIBLE) {
                                login_err_text1.setVisibility(View.GONE);
                                email.getBackground().clearColorFilter();
                            }
                        }
                        if (passWord.length() < 6) {
                            login_err_text2.setVisibility(View.VISIBLE);
                            if (!focus_first) {
                                password.requestFocus();
                            } else {
                                email.requestFocus();
                            }
                            password.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                            failed = true;
                        } else {
                            if (login_err_text2.getVisibility() == View.VISIBLE) {
                                login_err_text2.setVisibility(View.GONE);
                                password.getBackground().clearColorFilter();
                            }
                        }
                        if (failed) {
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
            email.setOnFocusChangeListener(button_focus);
            password = (EditText) rootView.findViewById(R.id.login_password);
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() < 6) {
                        if (login_err_text2.getVisibility() != View.VISIBLE) {
                            login_err_text2.setVisibility(View.VISIBLE);
                            password.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                        }
                    } else {
                        if (login_err_text2.getVisibility() == View.VISIBLE) {
                            login_err_text2.setVisibility(View.GONE);
                            password.getBackground().clearColorFilter();
                        }
                    }
                }
            });
            forgotPassword = (TextView) rootView.findViewById(R.id.forgot_pw);
            forgotPassword.setOnClickListener(buttonListener);
            login_err_text1 = (TextView) rootView.findViewById(R.id.login_err_text1);
            login_err_text2 = (TextView) rootView.findViewById(R.id.login_err_text2);
            return rootView;
        }

        @Override
        public void onDestroy() {
            email.getBackground().clearColorFilter();
            password.getBackground().clearColorFilter();
            super.onDestroy();
        }

    }

    public static class RegisterFragment extends Fragment {

        private TextView flname;
        private EditText email;
        private EditText password;
        private Button registerButton;
        private Button fbRegisterButton;
        private TextView regis_err_text1;
        private TextView regis_err_text2;
        private TextView regis_err_text3;

        private View.OnFocusChangeListener focus_button = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.flname: {
                        if (!hasFocus) {
                            if (regis_err_text1.getVisibility() == View.VISIBLE) {
                                if (flname.getText().toString().length() >= 2) {
                                    regis_err_text1.setVisibility(View.GONE);
                                    flname.getBackground().clearColorFilter();
                                }
                            }
                        }
                        break;
                    }
                    case R.id.register_email: {
                        if (!hasFocus) {
                            if (regis_err_text2.getVisibility() == View.VISIBLE) {
                                if (isEmailValid(email.getText().toString())) {
                                    regis_err_text2.setVisibility(View.GONE);
                                    email.getBackground().clearColorFilter();
                                }
                            }
                        }
                        break;
                    }
                }
            }
        };

        private View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.regis_btn: {
                        registerButton.setClickable(false);
                        String userEmail = email.getText().toString();
                        String userPassword = password.getText().toString();
                        String userName = flname.getText().toString();
                        boolean failed = false;
                        int err = 0;
                        if (userName.length() < 2) {
                            failed = true;
                            err = 1;
                            regis_err_text1.setVisibility(View.VISIBLE);
                            flname.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                        } else {
                            if (regis_err_text1.getVisibility() == View.VISIBLE) {
                                regis_err_text1.setVisibility(View.GONE);
                                flname.getBackground().clearColorFilter();
                            }
                         }
                        if (!isEmailValid(userEmail)) {
                            failed = true;
                            if (err == 0) err = 2;
                            regis_err_text2.setVisibility(View.VISIBLE);
                            email.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                        } else {
                            if (regis_err_text2.getVisibility() == View.VISIBLE) {
                                regis_err_text2.setVisibility(View.GONE);
                                email.getBackground().clearColorFilter();
                            }
                        }
                        if (userPassword.length() < 6) {
                            failed = true;
                            if (err == 0) err = 3;
                            regis_err_text3.setVisibility(View.VISIBLE);
                            password.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                        } else {
                            if (regis_err_text3.getVisibility() == View.VISIBLE) {
                                regis_err_text3.setVisibility(View.GONE);
                                password.getBackground().clearColorFilter();
                            }
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
            email.setOnFocusChangeListener(focus_button);
            password = (EditText) rootView.findViewById(R.id.register_password);
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() < 6) {
                        if (regis_err_text3.getVisibility() != View.VISIBLE) {
                            regis_err_text3.setVisibility(View.VISIBLE);
                            password.getBackground().setColorFilter(0xffdd2c00, PorterDuff.Mode.SRC_ATOP);
                        }
                    } else {
                        if (regis_err_text3.getVisibility() == View.VISIBLE) {
                            regis_err_text3.setVisibility(View.GONE);
                            password.getBackground().clearColorFilter();
                        }
                    }
                }
            });
            flname = (TextView) rootView.findViewById(R.id.flname);
            flname.setOnFocusChangeListener(focus_button);
            regis_err_text1 = (TextView) rootView.findViewById(R.id.regis_err_text1);
            regis_err_text2 = (TextView) rootView.findViewById(R.id.regis_err_text2);
            regis_err_text3 = (TextView) rootView.findViewById(R.id.regis_err_text3);
            return rootView;
        }

        @Override
        public void onDestroy() {
            email.getBackground().clearColorFilter();
            password.getBackground().clearColorFilter();
            flname.getBackground().clearColorFilter();
            super.onDestroy();
        }

    }


}
