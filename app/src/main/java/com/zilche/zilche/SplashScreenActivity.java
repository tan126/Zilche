package com.zilche.zilche;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;


public class SplashScreenActivity extends ActionBarActivity {

    HashMap<String, Integer> map;
    HashMap<String, Integer> fav;
    Zilche app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        app = (Zilche) getApplication();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseUser.getCurrentUser() == null) {
                    ParseAnonymousUtils.logIn(new LogInCallback() {

                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                parseUser.put("count", 1);
                                parseUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            updateMap();
                                        }
                                    }
                                });
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                                AlertDialog dia = null;
                                builder.setTitle("Connection Failed.");
                                builder.setMessage( "Please try again later.");
                                builder.setCancelable(false);
                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                dia = builder.create();
                                dia.show();
                            }
                        }
                    });

                } else {
                    updateMap();
                }
            }
        }, 300);

    }

    public void updateMap() {
        app.createMap();
        app.createFav();
        map = app.getMap();
        fav = app.getFav();
        updateMap(0);
    }

    public void updateMap(int skip) {
        final int s = skip;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Records");
        query.setLimit(1000);
        query.setSkip(1000 * s);
        query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        map.put(o.getString("Key"), o.getInt("choice"));
                    }
                    if (list.size() == 1000) {
                        updateMap(s + 1);
                    } else {
                        updateFav();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                    AlertDialog dia = null;
                    builder.setTitle("Connection Failed.");
                    builder.setMessage("Please try again later.");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dia = builder.create();
                    dia.show();
                }
            }
        });
    }

    public void updateFav() {
        updateFav(0);
    }

    public void updateFav(final int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Favourite");
        query.setLimit(1000);
        query.setSkip(1000 * skip);
        query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject o : list) {
                        fav.put(o.getString("Key"), o.getInt("Fav"));
                    }
                    if (list.size() == 1000) {
                        updateFav(skip + 1);
                    } else {
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                    AlertDialog dia = null;
                    builder.setTitle("Connection Failed.");
                    builder.setMessage("Please try again later.");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dia = builder.create();
                    dia.show();
                }
            }
        });
    }

}
