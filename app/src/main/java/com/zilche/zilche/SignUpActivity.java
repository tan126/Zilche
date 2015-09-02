package com.zilche.zilche;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void backButtonLogin(View v) {
        onBackPressed();
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
                        if (!hasInternetConnection()) {
                            Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        loginBtn.setEnabled(false);
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
                            loginBtn.setEnabled(true);
                            break;
                        }
                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.logging_in));
                        dialog.show();
                        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                dialog.dismiss();
                                if (user != null) {
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    i.putExtra("restart", 1);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().finish();
                                    startActivity(i);
                                } else {
                                    if (e.getCode() == 101 || e.getCode() == 205) {
                                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                    }
                                    loginBtn.setEnabled(true);
                                }
                            }
                        });
                        break;
                    }

                    case R.id.login_fb_btn: {

                        if (!hasInternetConnection()) {
                            Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), Arrays.asList("public_profile", "email"), new LogInCallback() {
                                        @Override
                                        public void done(final ParseUser parseUser, ParseException e) {
                                            if (e != null || parseUser == null) {
                                                Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                            } else if (parseUser.isNew()) {
                                                if (ParseUser.getCurrentUser() != null) {
                                                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                                            new GraphRequest.GraphJSONObjectCallback() {
                                                                @Override
                                                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                                                    try {
                                                                        String email_str = jsonObject.getString("email");
                                                                        String name_str = jsonObject.getString("name");
                                                                        ParseUser.getCurrentUser().put("email_str", email_str);
                                                                        ParseUser.getCurrentUser().put("name", name_str);
                                                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                                                                    i.putExtra("restart", 1);
                                                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                    getActivity().finish();
                                                                                    startActivity(i);
                                                                                    getActivity().overridePendingTransition(0, R.anim.fade_out);
                                                                                } else {
                                                                                    Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                    request.executeAsync();
                                                }
                                            } else {
                                                if (ParseUser.getCurrentUser() != null) {
                                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                                    i.putExtra("restart", 1);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    getActivity().finish();
                                                    startActivity(i);
                                                    getActivity().overridePendingTransition(0, R.anim.fade_out);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    }
                    case R.id.forgot_pw: {
                        LayoutInflater inf = getActivity().getLayoutInflater();
                        final View view = inf.inflate(R.layout.forgot_password, null);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        final AlertDialog alert11 = builder1.create();
                        final EditText etext = (EditText) view.findViewById(R.id.reset_email);
                        alert11.setView(view);
                        view.findViewById(R.id.cancel_reset).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert11.dismiss();
                            }
                        });
                        view.findViewById(R.id.reset_password).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String str = etext.getText().toString();
                                if (!isEmailValid(str)) {
                                    etext.setError(getString(R.string.invalid_email));
                                } else {
                                    if (!hasInternetConnection()) {
                                        Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            ParseUser.requestPasswordReset(str);
                                            Toast.makeText(getActivity(), getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                                        } catch (ParseException e) {
                                            if (e.getCode() == 205) {
                                                Toast.makeText(getActivity(), getString(R.string.no_email), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                            }
                                            e.printStackTrace();
                                        }
                                        alert11.dismiss();
                                    }
                                }
                            }
                        });
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

        public boolean hasInternetConnection() {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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
                        if (!hasInternetConnection()) {
                            Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        registerButton.setEnabled(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);
                        String userEmail = email.getText().toString();
                        String userPassword = password.getText().toString();
                        String userName = flname.getText().toString();
                        boolean failed = false;
                        int err = 0;
                        if (userName.length() < 2 || userName.toLowerCase().compareTo(getString(R.string.anonymous)) == 0) {
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
                            registerButton.setEnabled(true);
                            break;
                        }
                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.signing_up));
                        dialog.show();

                        ParseUser newUser;
                        newUser = ParseUser.getCurrentUser();
                         if (newUser == null)
                            newUser = new ParseUser();
                        newUser.setUsername(userEmail);
                        newUser.setPassword(userPassword);
                        newUser.setEmail(userEmail);
                        newUser.put("name", userName);
                        if (ParseUser.getCurrentUser() != null) {
                            newUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    dialog.dismiss();
                                    if (e == null) {
                                        Intent i = new Intent(getActivity(), MainActivity.class);
                                        i.putExtra("restart", 1);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        getActivity().finish();
                                        startActivity(i);
                                    } else {
                                        if (e.getCode() == 202 || e.getCode() == 203) {
                                            Toast.makeText(getActivity().getBaseContext(), getString(R.string.email_taken), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity().getBaseContext(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                        }
                                        registerButton.setEnabled(true);
                                    }
                                }
                            });
                            return;
                        }
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                dialog.dismiss();
                                if (e == null) {
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    i.putExtra("restart", 1);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().finish();
                                    startActivity(i);
                                } else {
                                    if (e.getCode() == 202 || e.getCode() == 203) {
                                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.email_taken), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                    }
                                    registerButton.setEnabled(true);
                                }
                            }
                        });

                        break;
                    }

                    case R.id.regis_fb_btn: {
                        if (!hasInternetConnection()) {
                            Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), Arrays.asList("public_profile", "email"), new LogInCallback() {
                                        @Override
                                        public void done(final ParseUser parseUser, ParseException e) {
                                            if (e != null || parseUser == null) {
                                                Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();

                                            } else if (parseUser.isNew()) {
                                                if (ParseUser.getCurrentUser() != null) {
                                                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                                            new GraphRequest.GraphJSONObjectCallback() {
                                                                @Override
                                                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                                                    try {
                                                                        String email_str = jsonObject.getString("email");
                                                                        String name_str = jsonObject.getString("name");
                                                                        ParseUser.getCurrentUser().put("email_str", email_str);
                                                                        ParseUser.getCurrentUser().put("name", name_str);
                                                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                                                                    i.putExtra("restart", 1);
                                                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                    getActivity().finish();
                                                                                    startActivity(i);
                                                                                    getActivity().overridePendingTransition(0, R.anim.fade_out);
                                                                                } else {
                                                                                    Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                    request.executeAsync();
                                                }
                                            } else {
                                                if (ParseUser.getCurrentUser() != null) {
                                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                                    i.putExtra("restart", 1);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    getActivity().finish();
                                                    startActivity(i);
                                                    getActivity().overridePendingTransition(0, R.anim.fade_out);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
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

        public boolean hasInternetConnection() {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }


    }

    @Override
    public void onBackPressed() {
        if (ParseUser.getCurrentUser() == null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("restart", 1);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else {
            super.onBackPressed();
        }
    }


}
