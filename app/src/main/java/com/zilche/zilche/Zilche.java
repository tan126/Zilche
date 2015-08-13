package com.zilche.zilche;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class Zilche extends Application {

    HashMap<String, Integer> map;
    HashMap<String, Integer> fav;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "G3fXBRPsZckZrfaxZuHaWR6h1wuUra5myTiV909i", "rsKIqI7RqBnAxodQfnUsD0w8V0HXl5MiJQf6PR0s");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }

    public void createMap() {
        map = new HashMap<>();
    }

    public void createFav() {
        fav = new HashMap<>();
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

    public HashMap<String, Integer> getFav() {return fav; }

}

