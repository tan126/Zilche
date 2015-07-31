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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;

public class MyPollActivity extends ActionBarActivity {
    GridView gv;
    ArrayList<String> pollList;
    ArrayList<String> timeList;
    ArrayList<String> totalList;
    ArrayList<String> authorList;
    ArrayList<String> hotnessList;


    ArrayList<String> pollIdList;
    ImageButton removePollButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_poll);
        getList();
    }

    public void getList(){

        gv = (GridView) findViewById(R.id.gridv);

        pollIdList = new ArrayList<String>();

        pollList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        totalList = new ArrayList<String>();
        authorList = new ArrayList<String>();
        hotnessList = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("poll");
        query.orderByDescending("lastUpdate");
        query.whereEqualTo("author", ParseUser.getCurrentUser().getString("username"));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    //Log.d("poll", "Retrieved " + list.size() + " polls");
                    if (list.size() == 0) {
                        Toast.makeText(getApplicationContext(), "You have no polls", Toast.LENGTH_SHORT).show();
                        TextView title = (TextView) findViewById(R.id.title);
                        title.setText("My Polls");
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject thisPoll = list.get(i);
                            String tmpStr = thisPoll.getString("question");

                            pollList.add(tmpStr);

                            //String tmpString = thisPoll.getString("objectId");
                            String tmpString = thisPoll.getObjectId();
                            pollIdList.add(tmpString);

                            //timeList.add(thisPoll.getCreatedAt().toString());
                            String tmp = "";
                            int total = thisPoll.getInt("total");
                            totalList.add("" + total + " participants");
                            if (total < 50)
                                hotnessList.add("g");
                            else if (total < 100)
                                hotnessList.add("y");
                            else if (total >= 100)
                                hotnessList.add("r");
                            else
                                hotnessList.add("g");
                            long updatedTime = thisPoll.getLong("lastUpdate");
                            long diffMS = System.currentTimeMillis() - updatedTime;
                            long diffS = diffMS / 1000;
                            if (diffS > 60) {
                                long diffM = diffS / 60;
                                if (diffM > 60) {
                                    long diffH = diffM / 60;
                                    if (diffH > 24) {
                                        long diffD = diffH / 24;
                                        tmp += "last updated " + diffD + " days ago";
                                    } else
                                        tmp += "last updated " + diffH + " hours ago";
                                    ;
                                } else
                                    tmp += "last updated " + diffM + " minutes ago";
                            } else
                                tmp += "last updated 1 minute ago";
                            timeList.add(tmp);
                            gv.setAdapter(new PollListAdapter(MyPollActivity.this));
                            TextView title = (TextView) findViewById(R.id.title);
                            title.setText("My Polls");
                        }
                    }
                } else {
                    Log.d("poll", "Error: " + e.getMessage());
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
        private String [] hots = hotnessList.toArray(new String[hotnessList.size()]);

        private String[] pollIDs = pollIdList.toArray(new String[pollIdList.size()]);

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
        public View getView(final int position, View convertView, ViewGroup parent) {



            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.mypolls, null);
            }

            removePollButton = (ImageButton) convertView.findViewById(R.id.pollRemove);

            ImageView check = (ImageView) convertView.findViewById(R.id.check);

            TextView total = (TextView) convertView.findViewById(R.id.total);

            Log.d("aaa:", hots[position]);
            if ( hots[position] == "y" ) {
                check.setColorFilter(Color.parseColor("#FF9800"));
                total.setTextColor(Color.parseColor("#FF9800"));
            }
            else if ( hots[position] == "r" ) {
                check.setImageResource(R.drawable.ic_whatshot_white_18dp);
                check.setColorFilter(Color.parseColor("#F44336"));
                total.setTextColor(Color.parseColor("#F44336"));
            }
            else {
                check.setColorFilter(Color.parseColor("#00C853"));
            }

            TextView tv = (TextView) convertView.findViewById(R.id.poll_name);
            tv.setText(polls[position]);
            TextView timev = (TextView) convertView.findViewById(R.id.time);
            timev.setText(times[position]);

            total.setText(totals[position]);
            convertView.setTag(position);
            ImageView iv = (ImageView) convertView.findViewById(R.id.assignment);
            iv.setImageResource(R.drawable.ic_assessment_white_48dp);
            iv.setColorFilter(Color.parseColor("#11110000"));

            removePollButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    //Toast.makeText(getApplicationContext(), "DELETING", Toast.LENGTH_SHORT).show();
                    String pollID = pollIDs[position];
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("poll");
                    query2.whereEqualTo("objectId", pollID);
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                System.out.println(parseObjects.get(0));
                                for (ParseObject delete : parseObjects) {
                                    delete.deleteInBackground();
                                    Toast.makeText(getApplicationContext(), "Poll deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in deleting", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    getList();
                }

            });



            return convertView;
        }


    }

}
