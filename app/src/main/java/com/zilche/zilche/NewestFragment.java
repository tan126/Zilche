package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NewestFragment extends Fragment{

    GridView gv;
    ArrayList<String> pollList;
    ArrayList<String> timeList;
    ArrayList<String> totalList;
    ArrayList<String> authorList;
    ArrayList<String> hotnessList;
    ArrayList<Poll> pollOnjectList;
    SwipeRefreshLayout swipeLayout;
    ParseQuery<ParseObject> query;
    ArrayList<Integer> categoryList;
    int isRefreshing;

    public NewestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_newest, container, false);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        isRefreshing = 0;
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = 1;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ( isRefreshing == 1 ) {
                            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_SHORT).show();
                            swipeLayout.setRefreshing(false);
                            isRefreshing = 0;
                        }
                    }
                }, 10000);

                pollList = new ArrayList<String>();
                timeList = new ArrayList<String>();
                totalList = new ArrayList<String>();
                authorList = new ArrayList<String>();
                hotnessList = new ArrayList<String>();
                pollOnjectList = new ArrayList<Poll>();
                categoryList = new ArrayList<Integer>();

                query = ParseQuery.getQuery("poll");
                query.orderByDescending("lastUpdate");

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
                                            tmp +=  diffD + " days ago";
                                        } else
                                            tmp +=  diffH + " hours ago";
                                        ;
                                    } else
                                        tmp +=  + diffM + " minutes ago";
                                } else
                                    tmp += "1 minute ago";
                                //tmp += " by " + name;
                                pollOnjectList.add(parsePollObject(thisPoll));
                                categoryList.add(tmpPoll.getCategory());
                                timeList.add(tmp);

                                //question: tmpStr
                                //options:
                                gv.setAdapter(new PollListAdapter(getActivity()));
                                swipeLayout.setRefreshing(false);
                                isRefreshing = 0;
                            }
                        } else {
                            Log.d("Newest Poll", "Error: " + e.getMessage());
                        }
                    }
                });
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gv = (GridView) rootView.findViewById(R.id.gridv);

        pollList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        totalList = new ArrayList<String>();
        authorList = new ArrayList<String>();
        hotnessList = new ArrayList<String>();
        pollOnjectList = new ArrayList<Poll>();
        categoryList = new ArrayList<Integer>();

        query = ParseQuery.getQuery("poll");
        query.orderByDescending("lastUpdate");

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
                        if ( total < 50 )
                            hotnessList.add("g");
                        else if ( total < 100 )
                            hotnessList.add("y");
                        else if ( total >= 100 )
                            hotnessList.add("r");
                        else
                            hotnessList.add("g");
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
                        //tmp += " by " + name;
                        timeList.add(tmp);
                        categoryList.add(tmpPoll.getCategory());
                        pollOnjectList.add(parsePollObject(thisPoll));
                        gv.setAdapter(new PollListAdapter(getActivity()));
                    }
                }
                else {
                    Log.d("Newest Poll", "Error: " + e.getMessage());
                }
            }
        });
        return rootView;
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
        int tmpCategory = thisPoll.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, tmpCategory);
        newPoll.setAnon(thisPoll.getInt("anon"));
        newPoll.setHasImage(thisPoll.getInt("haveImage"));
        if (thisPoll.getInt("haveImage") == 1) {
            newPoll.setFile(thisPoll.getParseFile("image"));
        }
        return newPoll;
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
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.newestpolls, null);
            }
            TextView category_text = (TextView) convertView.findViewById(R.id.category);
            int[] strings = {
                    R.string.category_all_2, R.string.category_auto_2, R.string.category_education_2, R.string.category_entertainment_2,
                    R.string.category_fashion_2, R.string.category_finance_2, R.string.category_food_2, R.string.category_games_2, R.string.category_it_2,
                    R.string.category_pet_2, R.string.category_science_2, R.string.category_social_2, R.string.category_sports_2, R.string.category_tech_2,
                    R.string.category_travel_2
            };
            category_text.setText(getString(strings[categoryList.get(position)]));
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
                                    Intent i = new Intent(getActivity(), PollViewActivity.class);
                                    i.putExtra("poll", pollOnjectList.get(position));
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.anim.right_to_left, 0);
                                } else {
                                    Toast.makeText(getActivity(), "Connection Failed. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Intent i = new Intent(getActivity(), PollViewActivity.class);
                        i.putExtra("poll", pollOnjectList.get(position));
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.right_to_left, 0);
                    }
                }
            });

            return convertView;
        }
    }


}
