package com.zilche.zilche;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;

public class MyPollActivity extends ActionBarActivity {

    private boolean load = false;
    private TextView title;
    int category = 0;
    private ImageButton sort;
    private LinkedList<Poll> pollList;
    private ProgressBar spinner;
    private RecyclerView rv;
    private int isRefreshing = 0;
    private HashMap<String, Integer> map;
    private int skip = 0;
    private int complete = 0;
    private int visibleThreshold = 15;
    private int sortBy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_poll);
        Zilche app = (Zilche) getApplication();
        map = app.getMap();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category = extras.getInt("category_index");
        }
        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        ((ImageButton)findViewById(R.id.back_button_cat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv_cat);
        rv.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        pollList = new LinkedList<>();
        populateList(skip);
        RVadapter rva = new RVadapter(pollList);
        rv.setAdapter(rva);

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = llm.getItemCount();
                int firstVisibleItem = llm.findFirstVisibleItemPosition();
                //if (!load && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if (!load && ((pollList.size() > 0 && pollList.size() - firstVisibleItem <= visibleThreshold) || !rv.canScrollVertically(1))) {
                    if (complete == 0) {
                        if (pollList.size() == 0 || pollList.getLast().getId().compareTo("-1") != 0) {
                            Poll tmp = new Poll();
                            tmp.setId("-1");
                            pollList.add(tmp);
                            rv.getAdapter().notifyDataSetChanged();
                        }
                        populateList(skip);
                    }
                }

            }
        });
    }

    public void populateList(final int skip2) {
        load = true;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        query.whereEqualTo("author", ParseUser.getCurrentUser().getEmail());
        query.setLimit(15);
        query.whereNotEqualTo("archived", 1);
        query.setSkip(skip2 * 15);
        query.orderByDescending("lastUpdate");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (pollList.size() != 0 && pollList.getLast() != null && pollList.getLast().getId().compareTo("-1") == 0) {
                        pollList.removeLast();
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    if (list.size() < 15) {
                        complete = 1;
                    } else {
                        complete = 0;
                    }
                    if (list != null && list.size() != 0) {
                        if (skip2 == 0) {
                            pollList.clear();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            pollList.add(Util.parsePollObject(list.get(i)));
                        }

                        rv.getAdapter().notifyDataSetChanged();
                        isRefreshing = 0;
                        load = false;
                        skip++;
                    }
                } else {
                    if (pollList.size() != 0 && pollList.getLast() != null && pollList.getLast().getId().compareTo("-1") == 0) {
                        pollList.removeLast();
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    load = false;
                    Toast.makeText(MyPollActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (spinner.getVisibility() == View.VISIBLE) {
                    spinner.setVisibility(View.GONE);
                }
            }
        });
    }

    public class RVadapter extends RecyclerView.Adapter<RVadapter.PollViewHolder> {

        List<Poll> polls;
        View.OnClickListener openMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(MyPollActivity.this, v);
                menu.getMenuInflater().inflate(R.menu.menu_my_poll, menu.getMenu());
                final View tag_v = v;
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MyPollActivity.this);
                            builder.setTitle("Delete poll ?");
                            builder.setMessage(" ");
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    int tag = (int) tag_v.getTag();
                                    ParseObject o = ParseObject.createWithoutData("poll", polls.get(tag).getId());
                                    o.put("archived", 1);
                                    o.saveInBackground();
                                    polls.remove(tag);
                                    rv.getAdapter().notifyDataSetChanged();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return false;
                        }
                    });
                menu.show();
            }
        };
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                Intent i = new Intent(MyPollActivity.this, PollViewActivity.class);
                i.putExtra("poll", polls.get(pos));
                startActivity(i);
                overridePendingTransition(R.anim.right_to_left, 0);
            }

        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

            ImageView overflow;
            LinearLayout main;
            ProgressBar pb;
            CardView cv;
            TextView question;
            TextView date;
            TextView total_votes;
            ImageView iv;
            ImageView has_photo;
            ImageView category_icon;
            TextView author;

            public PollViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                question = (TextView) itemView.findViewById(R.id.question);
                date = (TextView) itemView.findViewById(R.id.date);
                total_votes = (TextView) itemView.findViewById(R.id.category);
                cv.setOnClickListener(onclick);
                iv = (ImageView) itemView.findViewById(R.id.done);
                category_icon = (ImageView) itemView.findViewById(R.id.category_icon);
                has_photo = (ImageView) itemView.findViewById(R.id.have_photo);
                author = (TextView) itemView.findViewById(R.id.author);
                pb = (ProgressBar) itemView.findViewById(R.id.pb);
                overflow = (ImageView) itemView.findViewById(R.id.overflow);
                overflow.setOnClickListener(openMenu);
            }
        }

        public RVadapter(List<Poll> pollList) {
            polls = pollList;
        }

        @Override
        public RVadapter.PollViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items2, viewGroup, false);
            v.setTag(i);
            PollViewHolder pvh = new PollViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RVadapter.PollViewHolder pollViewHolder, int i) {
            if (polls.get(i).getId().compareTo("-1") == 0) {
                pollViewHolder.pb.setVisibility(View.VISIBLE);
                pollViewHolder.cv.setVisibility(View.GONE);
                return;
            } else {
                pollViewHolder.pb.setVisibility(View.GONE);
                pollViewHolder.cv.setVisibility(View.VISIBLE);
            }
            Poll p = polls.get(i);
            pollViewHolder.total_votes.setText(Integer.toString(p.totalVotes()));
            pollViewHolder.category_icon.setImageResource(Util.drawables[p.getCategory()]);
            pollViewHolder.date.setText(p.getDate_added());
            pollViewHolder.question.setText(p.getQuestion());
            pollViewHolder.cv.setTag(i);
            pollViewHolder.overflow.setTag(i);
            if (map.get(p.getId()) != null) {
                pollViewHolder.iv.setImageResource(R.drawable.ic_done_green_18dp);
            } else {
                pollViewHolder.iv.setImageResource(R.drawable.ic_done_grey_18dp);
            }
            if (p.hasImage() == 1) {
                pollViewHolder.has_photo.setVisibility(View.VISIBLE);
            } else {
                pollViewHolder.has_photo.setVisibility(View.GONE);
            }
            pollViewHolder.author.setText(p.getAnon() == 1 ? "Anonymous" : p.getAuthor());
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


}
