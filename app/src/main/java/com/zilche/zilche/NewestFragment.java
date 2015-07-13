package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class NewestFragment extends Fragment{

    GridView gv;
    ArrayList<String> pollList;
    ArrayList<String> timeList;
    ArrayList<String> totalList;
    ArrayList<String> authorList;
    ArrayList<String> hotnessList;

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

        gv = (GridView) rootView.findViewById(R.id.gridv);

        pollList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        totalList = new ArrayList<String>();
        authorList = new ArrayList<String>();
        hotnessList = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
        query.orderByDescending("lastUpdate");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject thisPoll = list.get(i);
                        String tmpStr = thisPoll.getString("question");
                        String name = thisPoll.getString("nickname");
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
                                    tmp += "submitted " + diffD + " days ago";
                                }
                                else
                                    tmp += "submitted " + diffH + " hours ago";;
                            }
                            else
                                tmp += "submitted " + diffM + " minutes ago";
                        }
                        else
                            tmp += "submitted 1 minute ago";
                        tmp += " by " + name;
                        timeList.add(tmp);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            ImageView iv = (ImageView) convertView.findViewById(R.id.assignment);
            iv.setImageResource(R.drawable.ic_assessment_white_48dp);
            iv.setColorFilter(Color.parseColor("#11110000"));
            return convertView;
        }
    }


}
