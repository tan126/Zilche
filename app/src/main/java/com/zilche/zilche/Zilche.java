package com.zilche.zilche;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class Zilche extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "G3fXBRPsZckZrfaxZuHaWR6h1wuUra5myTiV909i", "rsKIqI7RqBnAxodQfnUsD0w8V0HXl5MiJQf6PR0s");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }

}
