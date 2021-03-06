package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    public static int[] drawables = {
            R.drawable.ic_assessment_grey_18dp, R.drawable.ic_directions_car_grey_18dp, R.drawable.ic_school_grey_18dp, R.drawable.ic_mic_grey_18dp,
            R.drawable.ic_watch_grey_18dp, R.drawable.ic_local_atm_grey_18dp, R.drawable.ic_local_dining_grey_18dp, R.drawable.ic_games_grey_18dp,
            R.drawable.ic_code_grey_18dp, R.drawable.pet_icon_18, R.drawable.ic_wb_incandescent_grey_18dp, R.drawable.ic_people_grey_18dp,
            R.drawable.ic_directions_bike_grey_18dp, R.drawable.ic_laptop_windows_grey_18dp, R.drawable.ic_flight_takeoff_grey_18dp
    };

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

    public static Poll parsePollObject(ParseObject object, Context c) {
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
        long diffD = 0;
        if ( diffS > 60 ) {
            long diffM = diffS / 60;
            if ( diffM > 60 ){
                long diffH = diffM / 60;
                if ( diffH > 24 ) {
                    diffD = diffH / 24;
                    if (diffD == 1) {
                        if (c != null) {
                            tmp += diffD + " " + c.getString(R.string.day_ago);
                        } else {
                            tmp += diffD + " " + "day ago";
                        }
                    } else {
                        if (c != null) {
                            tmp += diffD + " " + c.getString(R.string.days_ago);
                        } else {
                            tmp += diffD + " " + "days ago";
                        }
                    }
                } else {
                    if (diffH == 1) {
                        if (c != null) {
                            tmp += diffH + " " + c.getString(R.string.hour_ago);
                        } else {
                            tmp += diffH + " " + "hour ago";
                        }
                    } else {
                        if (c != null) {
                            tmp += diffH + " " + c.getString(R.string.hours_ago);
                        } else {
                            tmp += diffH + " " + "hours ago";
                        }
                    }
                }
            } else {
                if (c != null) {
                    tmp += diffM + " " + c.getString(R.string.minutes_ago);
                } else {
                    tmp += diffM + " " + "minutes ago";
                }
            }
        } else {
            if (c != null) {
                tmp += c.getString(R.string.minute_ago);
            } else {
                tmp += "1 minute ago";
            }
        }
        if (diffD > 3) {
            Date d = object.getCreatedAt();
            DateFormat df = new SimpleDateFormat("M/dd/yyyy");
            tmp = df.format(d);
        }
        String date_added = tmp;
        String author = object.getString("nickname");
        String authorLogin = object.getString("author");
        int categorty = object.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, authorLogin, options_count, categorty);
        newPoll.setAnon(object.getInt("anon"));
        newPoll.setHasImage(object.getInt("haveImage"));
        if (object.getInt("haveImage") == 1)
            newPoll.setFile(object.getParseFile("image"));
        newPoll.setAuthor_id(object.getString("author_id"));
        return newPoll;
    }

    public static boolean hasInternetConnection(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static Comment parseComment(ParseObject po, ParseUser user) {
        Date d = po.getCreatedAt();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String date = df.format(d);
        String author = null;
        if (user != null) {
            author = user.getString("name");
        } else {
            author = po.getString("author");
        }
        Comment c = new Comment(author, po.getString("comment"), date);
        c.setOp(po.getInt("op"));
        c.setMod(po.getInt("mod"));
        if (user != null) {
            if (user.getBytes("image") != null) {
                c.setHasImage(true);
                c.setImage(user.getBytes("image"));
            }
        }
        c.setEmail(po.getString("email"));
        c.setAuthor_id(po.getString("author_id"));
        c.setId(po.getObjectId());
        ArrayList<String> replies = new ArrayList<>();
        if (po.getList("replies") != null) {
            for (Object e : po.getList("replies")) {
                replies.add((String) e);
            }
        }
        c.setReplies(replies);
        return c;
    }

    public static float convertDpToPixel(float dp, Activity context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static Bitmap decodeFile(String path) {
        Bitmap bm;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        final int size = 1024;
        int scale = 1;
        while (opt.outWidth / scale >= size || opt.outHeight / scale >= size) {
            scale *= 2;
        }
        opt.inSampleSize = scale;
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, opt);
        return bm;
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

}
