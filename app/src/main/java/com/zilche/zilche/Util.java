package com.zilche.zilche;

import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

public class Util {

    public static int[] strings = {
            R.string.category_all_2, R.string.category_auto_2, R.string.category_education_2, R.string.category_entertainment_2,
            R.string.category_fashion_2, R.string.category_finance_2, R.string.category_food_2, R.string.category_games_2, R.string.category_it_2,
            R.string.category_pet_2, R.string.category_science_2, R.string.category_social_2, R.string.category_sports_2, R.string.category_tech_2,
            R.string.category_travel_2
    };
    public static int[] title_color = {
            0xff2196F3, 0xffF44336, 0xff673AB7, 0xff9C27B0, 0xffCDDC39, 0xffFF5722, 0xffFDD835, 0xff9E9E9E, 0xff4CAF50, 0xff795548,
            0xff009688, 0xff00BCD4, 0xffE91E63, 0xffFF9800, 0xff607D8B
    };
    public static int[] noti_color = {
            0xff1976D2, 0xffD32F2F, 0xff512DA8, 0xff7B1FA2, 0xffAFB42B, 0xffE64A19, 0xffFBC02D, 0xff616161, 0xff388E3C, 0xff5D4037,
            0xff00796B, 0xff0097A7, 0xffC2185B, 0xffF57C00, 0xff455A64
    };

    public static Poll parsePollObject(ParseObject object) {
        String id = object.getObjectId();
        String question = object.getString("question");
        int options_count = object.getInt("optionNum");
        JSONArray tmpOptions = object.getJSONArray("options");
        String[] options = new String[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            try{
                options[i] = tmpOptions.getString(i);
            } catch ( JSONException e ){
                Log.d("JSON", "Array index out of bound");
            }
        }
        int[] votes = new int[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            votes[i] = object.getInt("votes" + Integer.toString(i));
        }
        String tmp = "";
        long updatedTime = object.getLong("createTime");
        long diffMS = System.currentTimeMillis() - updatedTime;
        long diffS = diffMS / 1000;
        if ( diffS > 60 ) {
            long diffM = diffS / 60;
            if ( diffM > 60 ){
                long diffH = diffM / 60;
                if ( diffH > 24 ) {
                    long diffD = diffH / 24;
                    if (diffD == 1) {
                        tmp += diffD + " day ago";
                    } else {
                        tmp += diffD + " days ago";
                    }
                }
                else {
                    if (diffH == 1) {
                        tmp += diffH + " hour ago";
                    } else {
                        tmp += diffH + " hours ago";
                    }
                }
            }
            else
                tmp += diffM + " minutes ago";
        }
        else
            tmp += " 1 minute ago";
        String date_added = tmp;
        String author = object.getString("nickname");
        int categorty = object.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, categorty);
        newPoll.setAnon(object.getInt("anon"));
        newPoll.setHasImage(object.getInt("haveImage"));
        if (object.getInt("haveImage") == 1)
            newPoll.setFile(object.getParseFile("image"));
        return newPoll;
    }

}
