package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private TextView title;
    int category = 0;
    private int[] strings = {
            R.string.category_all_2, R.string.category_auto_2, R.string.category_education_2, R.string.category_entertainment_2,
            R.string.category_fashion_2, R.string.category_finance_2, R.string.category_food_2, R.string.category_games_2, R.string.category_it_2,
            R.string.category_pet_2, R.string.category_science_2, R.string.category_social_2, R.string.category_sports_2, R.string.category_tech_2,
            R.string.category_travel_2
    };
    private int[] title_color = {
            0xff2196F3, 0xffF44336, 0xff673AB7, 0xff9C27B0, 0xffCDDC39, 0xffFF5722, 0xffFDD835, 0xff9E9E9E, 0xff4CAF50, 0xff795548,
            0xff009688, 0xff00BCD4, 0xffE91E63, 0xffFF9800, 0xff607D8B
    };
    private  int[] noti_color = {
            0xff1976D2, 0xffD32F2F, 0xff512DA8, 0xff7B1FA2, 0xffAFB42B, 0xffE64A19, 0xffFBC02D, 0xff616161, 0xff388E3C, 0xff5D4037,
            0xff00796B, 0xff0097A7, 0xffC2185B, 0xffF57C00, 0xff455A64
    };
    private LinkedList<Poll> pollList;
    private ProgressBar spinner;
    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private int isRefreshing = 0;
    private HashMap<String, Integer> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Zilche app = (Zilche) getApplication();
        map = app.getMap();
        Bundle extras = getIntent().getExtras();
        title = (TextView) findViewById(R.id.title_cat);
        if (extras != null) {
            category = extras.getInt("category_index");
        }
        title.setText(strings[category]);
        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        ((ImageButton)findViewById(R.id.back_button_cat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(noti_color[category]);
        }
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = 1;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefreshing == 1) {
                            Toast.makeText(CategoryActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            srl.setRefreshing(false);
                            isRefreshing = 0;
                        }
                    }
                }, 10000);
                get_updated();
            }
        });
        srl.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.header);
        lay.setBackgroundColor(title_color[category]);
        rv = (RecyclerView) findViewById(R.id.rv_cat);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        pollList = new LinkedList<>();
        populateList();
        RVadapter rva = new RVadapter(pollList);
        rv.setAdapter(rva);
    }

    public void populateList() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        if (category != 0)
            query.whereEqualTo("category", category);
        query.orderByDescending("lastUpdate");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            pollList.add(parsePollObject(list.get(i)));
                        }
                        rv.swapAdapter(new RVadapter(pollList), false);
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private void get_updated() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        if (category != 0)
            query.whereEqualTo("category", category);
        query.orderByDescending("lastUpdate");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    int tmp = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getObjectId().compareTo(pollList.get(tmp).getId()) != 0) {
                            pollList.addFirst(parsePollObject(list.get(i)));
                        } else {
                            pollList.set(tmp, parsePollObject(list.get(i)));
                        }
                        tmp++;
                    }
                    rv.getAdapter().notifyDataSetChanged();
                    srl.setRefreshing(false);
                    isRefreshing = 0;
                } else {
                    Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        rv.getAdapter().notifyDataSetChanged();
    }

    public class RVadapter extends RecyclerView.Adapter<RVadapter.PollViewHolder> {

        List<Poll> polls;
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                if (!hasInternetConnection()) {
                    Toast.makeText(CategoryActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pollList.get(pos).hasImage() == 1) {
                    pollList.get(pos).getFile().getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null) {
                                if (pollList.get(pos).getImage() != null) return;
                                pollList.get(pos).setImage(bytes);
                                Intent i = new Intent(CategoryActivity.this, PollViewActivity.class);
                                i.putExtra("poll", polls.get(pos));
                                startActivity(i);
                                overridePendingTransition(R.anim.right_to_left, 0);
                            } else {
                                Toast.makeText(CategoryActivity.this, "Connection Failed. Try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Intent i = new Intent(CategoryActivity.this, PollViewActivity.class);
                    i.putExtra("poll", polls.get(pos));
                    startActivity(i);
                    overridePendingTransition(R.anim.right_to_left, 0);
                }
            }
        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

            CardView cv;
            TextView question;
            TextView date;
            TextView category;
            ImageView iv;

            public PollViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                question = (TextView) itemView.findViewById(R.id.question);
                date = (TextView) itemView.findViewById(R.id.date);
                category = (TextView) itemView.findViewById(R.id.category);
                cv.setOnClickListener(onclick);
                iv = (ImageView) itemView.findViewById(R.id.done);
            }
        }

        public RVadapter(List<Poll> pollList) {
            polls = pollList;
        }

        @Override
        public RVadapter.PollViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
            PollViewHolder pvh = new PollViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RVadapter.PollViewHolder pollViewHolder, int i) {
            Poll p = polls.get(i);
            if (p.getCategory() == 0) {
                pollViewHolder.category.setText(getString(R.string.other));
            } else {
                pollViewHolder.category.setText(strings[p.getCategory()]);
            }
            pollViewHolder.date.setText(p.getDate_added());
            pollViewHolder.question.setText(p.getQuestion());
            pollViewHolder.cv.setTag(i);
            if (map.get(p.getId()) != null) {
                pollViewHolder.iv.setVisibility(View.VISIBLE);
            } else {
                pollViewHolder.iv.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView rv) {
            super.onAttachedToRecyclerView(rv);
        }


        @Override
        public int getItemCount() {
            return polls.size();
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
        String author = thisPoll.getString("nickname");
        int categorty = thisPoll.getInt("category");
        Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, categorty);
        if (categorty == 0)
            newPoll.setCategory_title(getString(R.string.other));
        else
            newPoll.setCategory_title(getString(strings[categorty]));
        newPoll.setAnon(thisPoll.getInt("anon"));
        newPoll.setHasImage(thisPoll.getInt("haveImage"));
        if (thisPoll.getInt("haveImage") == 1)
            newPoll.setFile(thisPoll.getParseFile("image"));
        return newPoll;
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
