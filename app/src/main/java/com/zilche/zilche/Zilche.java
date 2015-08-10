package com.zilche.zilche;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class Zilche extends Application {

    HashMap<String, Integer> map = new HashMap<String, Integer>();
    HashMap<String, Integer> fav = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "G3fXBRPsZckZrfaxZuHaWR6h1wuUra5myTiV909i", "rsKIqI7RqBnAxodQfnUsD0w8V0HXl5MiJQf6PR0s");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

    public HashMap<String, Integer> getFav() {return fav; }

    public void updateMap() {
        map = new HashMap<>();
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
                for (ParseObject o : list) {
                    map.put(o.getString("Key"), o.getInt("choice"));
                }
                if (list.size() == 1000) {
                    updateMap(s + 1);
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
                    }
                }
            }
        });
    }

}

