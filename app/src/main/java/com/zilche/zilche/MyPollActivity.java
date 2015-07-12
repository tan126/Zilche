package com.zilche.zilche;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPollActivity extends ActionBarActivity {
    GridView gv;
    ArrayList<String> pollList;
    ArrayList<String> timeList;
    ArrayList<String> totalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_poll);
        gv = (GridView) findViewById(R.id.gridv);

        pollList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        totalList = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
        query.orderByDescending("lastUpdate");
        query.whereEqualTo("author", ParseUser.getCurrentUser().getString("username"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    //Log.d("poll", "Retrieved " + list.size() + " polls");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject thisPoll = list.get(i);
                        String tmpStr = thisPoll.getString("question");
                        pollList.add(tmpStr);
                        //timeList.add(thisPoll.getCreatedAt().toString());
                        String tmp = "";
                        int total = thisPoll.getInt("total");
                        totalList.add("" + total + " participants");
                        long updatedTime = thisPoll.getLong("lastUpdate");
                        long diffMS = System.currentTimeMillis() - updatedTime;
                        long diffS = diffMS / 1000;
                        if ( diffS > 60 ) {
                            long diffM = diffS / 60;
                            if ( diffM > 60 ){
                                long diffH = diffM / 60;
                                if ( diffH > 24 ) {
                                    long diffD = diffH / 24;
                                    tmp += "last updated " + diffD + " days ago";
                                }
                                else
                                    tmp += "last updated " + diffH + " hours ago";;
                            }
                            else
                                tmp += "last updated " + diffM + " minutes ago";
                        }
                        else
                            tmp += "last updated 1 minute ago";
                        timeList.add(tmp);
                        gv.setAdapter(new PollListAdapter(MyPollActivity.this));
                        TextView title = (TextView) findViewById(R.id.title);
                        title.setText("My Poll");
                    }
                } else {
                    Log.d("Poll", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void backButtonLogin(View v) {
        finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public class PollListAdapter extends BaseAdapter {

        private Context c;

        /*private String[] polls = {
                "What's is the color?", "What is the number?"
        };*/
        private String[] polls = pollList.toArray(new String[pollList.size()]);
        /*private String[] times= {
                "2hr ago", "1day ago"
        };*/
        private String[] times = timeList.toArray(new String[timeList.size()]);
        private String[] totals = totalList.toArray(new String[totalList.size()]);

        public PollListAdapter(Context c) {
            this.c = c;
        }

        @Override
        public int getCount() {
            return polls.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.mypolls, null);
            }
            ImageView check = (ImageView) convertView.findViewById(R.id.check);
            check.setColorFilter(Color.parseColor("#00C853"));
            TextView tv = (TextView) convertView.findViewById(R.id.poll_name);
            tv.setText(polls[position]);
            TextView timev = (TextView) convertView.findViewById(R.id.time);
            timev.setText(times[position]);
            TextView total = (TextView) convertView.findViewById(R.id.total);
            total.setText(totals[position]);
            convertView.setTag(position);
            ImageView iv = (ImageView) convertView.findViewById(R.id.assignment);
            iv.setImageResource(R.mipmap.ic_assessment_white_24dp);
            iv.setColorFilter(Color.parseColor("#11110000"));
            return convertView;
        }
    }

}
