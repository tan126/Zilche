package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class SearchList extends AppCompatActivity {

    SearchView mySearchView;
    GridView gv;
    ArrayList<String> pollList;
    ArrayList<String> timeList;
    ArrayList<String> totalList;
    ArrayList<String> authorList;
    ArrayList<String> hotnessList;
    ArrayList<Poll> pollOnjectList;
    SwipeRefreshLayout swipeLayout;
    ParseQuery<ParseObject> query;
    int isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        final SearchView mySearchView = (SearchView) findViewById(R.id.my_search_bar);
        mySearchView.setFocusable(true);
        mySearchView.setIconified(false);
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                0);
                finish();
            }
        });
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);

        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // Perform search here!
                performSearch(query);
                mySearchView.clearFocus();
                // Clear the text in search bar but (don't trigger a new search!)
                mySearchView.setQuery("", false);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });


    }

    public void performSearch(String queryStr) {

        //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
        gv = (GridView) findViewById(R.id.gridv);
        pollList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        totalList = new ArrayList<String>();
        authorList = new ArrayList<String>();
        hotnessList = new ArrayList<String>();
        pollOnjectList = new ArrayList<Poll>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("poll");
        query.orderByDescending("lastUpdate");


        query.whereMatches("question", "(" + queryStr+")", "i");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject thisPoll = list.get(i);
                        //String tmpStr = thisPoll.getString("question");
                        //String name = thisPoll.getString("nickname");
                        Poll tmpPoll = parsePollObject(thisPoll);
                        String tmpStr = tmpPoll.getQuestion();
                        String name = tmpPoll.getAuthor();
                        pollList.add(tmpStr);
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
                        long updatedTime = thisPoll.getLong("createTime");
                        long diffMS = System.currentTimeMillis() - updatedTime;
                        long diffS = diffMS / 1000;
                        if (diffS > 60) {
                            long diffM = diffS / 60;
                            if (diffM > 60) {
                                long diffH = diffM / 60;
                                if (diffH > 24) {
                                    long diffD = diffH / 24;
                                    tmp += diffD + " days ago";
                                } else
                                    tmp += +diffH + " hours ago";
                                ;
                            } else
                                tmp += +diffM + " minutes ago";
                        } else
                            tmp += " 1 minute ago";
                        //tmp += " by " + name;
                        timeList.add(tmp);
                        pollOnjectList.add(parsePollObject(thisPoll));
                        gv.setAdapter(new PollListAdapter(SearchList.this));
                    }
                } else {
                    Log.d("Newest Poll", "Error: " + e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

                LayoutInflater vi = (LayoutInflater) SearchList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.newestpolls, null);
            }
            ImageView check = (ImageView) convertView.findViewById(R.id.check);
            TextView tv = (TextView) convertView.findViewById(R.id.poll_name);
            tv.setText(polls[position]);
            TextView timev = (TextView) convertView.findViewById(R.id.time);
            timev.setText(times[position]);
            TextView total = (TextView) convertView.findViewById(R.id.total);
            total.setText(totals[position]);
            convertView.setTag(position);
            if ( hots[position] == "y" ) {
                check.setImageResource(R.drawable.ic_check_circle_white_18dp);
                check.setColorFilter(Color.parseColor("#FF9800"));
                total.setTextColor(Color.parseColor("#FF9800"));
            }
            else if ( hots[position] == "r" ) {
                check.setImageResource(R.drawable.ic_whatshot_white_18dp);
                check.setColorFilter(Color.parseColor("#F44336"));
                total.setTextColor(Color.parseColor("#F44336"));
            }
            else {
                check.setImageResource(R.drawable.ic_check_circle_white_18dp);
                check.setColorFilter(Color.parseColor("#00C853"));
                total.setTextColor(Color.parseColor("#00C853"));
            }
            //ImageView iv = (ImageView) convertView.findViewById(R.id.assignment);
            //iv.setImageResource(R.drawable.ic_assessment_white_48dp);
            //iv.setColorFilter(Color.parseColor("#11110000"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO intent to poll view
                    if (isRefreshing == 1)
                        return;
                    final Poll p = pollOnjectList.get(position);
                    if (p.hasImage() == 1) {
                        p.getFile().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                if (e == null) {
                                    p.setImage(bytes);
                                    Intent i = new Intent(SearchList.this, PollViewActivity.class);
                                    i.putExtra("poll", pollOnjectList.get(position));
                                    startActivity(i);
                                    SearchList.this.overridePendingTransition(R.anim.right_to_left, 0);
                                } else {
                                    Toast.makeText(SearchList.this, "Connection Failed. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Intent i = new Intent(SearchList.this, PollViewActivity.class);
                        i.putExtra("poll", pollOnjectList.get(position));
                        startActivity(i);
                        SearchList.this.overridePendingTransition(R.anim.right_to_left, 0);
                    }
                }
            });

            return convertView;
        }
    }
    private Poll parsePollObject(ParseObject thisPoll){
        String id = thisPoll.getObjectId();
        String question = thisPoll.getString("question");
        int options_count = thisPoll.getInt("optionNum");
        JSONArray tmpOptions = thisPoll.getJSONArray("options");
        String[] options = new String[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            try{
                options[i] = tmpOptions.getString(i);
            } catch ( JSONException e ){
                Log.d("JSON", "Array index out of bound");
            }
        }
        JSONArray tmpVotes = thisPoll.getJSONArray("votes");
        int[] votes = new int[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            votes[i] = thisPoll.getInt("votes" + Integer.toString(i));
        }
        String tmp = "";
        long updatedTime = thisPoll.getLong("createTime");
        long diffMS = System.currentTimeMillis() - updatedTime;
        long diffS = diffMS / 1000;
        if ( diffS > 60 ) {
            long diffM = diffS / 60;
            if ( diffM > 60 ){
                long diffH = diffM / 60;
                if ( diffH > 24 ) {
                    long diffD = diffH / 24;
                    tmp +=  diffD + " days ago";
                }
                else
                    tmp +=  + diffH + " hours ago";;
            }
            else
                tmp += + diffM + " minutes ago";
        }
        else
            tmp += " 1 minute ago";
        String date_added = tmp;
        String author = thisPoll.getString("nickname");
        int category = thisPoll.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, category);
        newPoll.setAnon(thisPoll.getInt("anon"));
        newPoll.setHasImage(thisPoll.getInt("haveImage"));
        if (thisPoll.getInt("haveImage") == 1) {
            newPoll.setFile(thisPoll.getParseFile("image"));
        }
        return newPoll;
    }/*
    private Poll parsePollObject(ParseObject thisPoll){
        String id = thisPoll.getObjectId();
        String question = thisPoll.getString("question");
        int options_count = thisPoll.getInt("optionNum");
        JSONArray tmpOptions = thisPoll.getJSONArray("options");
        String[] options = new String[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            try{
                options[i] = tmpOptions.getString(i);
            } catch ( JSONException e ){
                Log.d("JSON", "Array index out of bound");
            }
        }
        JSONArray tmpVotes = thisPoll.getJSONArray("votes");
        int[] votes = new int[options_count];
        for( int i = 0; i < options_count; i ++ ) {
            try{
                votes[i] = tmpVotes.getInt(i);
            } catch ( JSONException e ){
                Log.d("JSON", "Array index out of bound");
            }
        }
        String tmp = "";
        long updatedTime = thisPoll.getLong("createTime");
        long diffMS = System.currentTimeMillis() - updatedTime;
        long diffS = diffMS / 1000;
        if ( diffS > 60 ) {
            long diffM = diffS / 60;
            if ( diffM > 60 ){
                long diffH = diffM / 60;
                if ( diffH > 24 ) {
                    long diffD = diffH / 24;
                    tmp +=  diffD + " days ago";
                }
                else
                    tmp +=  + diffH + " hours ago";;
            }
            else
                tmp += + diffM + " minutes ago";
        }
        else
            tmp += " 1 minute ago";
        String date_added = tmp;
        String author = thisPoll.getString("nickname");
        int categorty = thisPoll.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, categorty);
        return newPoll;
    }*/

}