package com.zilche.zilche;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private boolean load = false;
    private TextView title;
    int category = 0;
    private ImageButton sort;
    private LinkedList<Poll> pollList;
    private ProgressBar spinner;
    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private int isRefreshing = 0;
    private HashMap<String, Integer> map;
    private int skip = 0;
    private int complete = 0;
    private int visibleThreshold = 15;
    private int sortBy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Zilche app = (Zilche) getApplication();
        map = app.getMap();
        Bundle extras = getIntent().getExtras();
        title = (TextView) findViewById(R.id.title_cat);
        sort = (ImageButton) findViewById(R.id.sort_cat);

        if (extras != null) {
            category = extras.getInt("category_index");
        }
        title.setText(Util.strings[category]);
        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        ((ImageButton)findViewById(R.id.back_button_cat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Util.noti_color[category]);
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
                skip = 0;
                populateList(skip, sortBy);
            }
        });
        srl.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.header);
        lay.setBackgroundColor(Util.title_color[category]);
        rv = (RecyclerView) findViewById(R.id.rv_cat);
        rv.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        pollList = new LinkedList<>();
        populateList(skip, sortBy);
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
                        populateList(skip, sortBy);
                    }
                }

            }
        });
    }

    public void populateList(final int skip2, int sortBy) {
        load = true;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        query.setLimit(15);
        query.setSkip(skip2 * 15);
        query.whereNotEqualTo("archived", 1);
        if (category != 0)
            query.whereEqualTo("category", category);
        if (sortBy == 1) {
            query.orderByDescending("lastUpdate");
        } else {
            query.orderByDescending("total");
        }
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
                        srl.setRefreshing(false);
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
                    Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (spinner.getVisibility() == View.VISIBLE) {
                    spinner.setVisibility(View.GONE);
                }
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
                            pollList.addFirst(Util.parsePollObject(list.get(i)));
                        } else {
                            pollList.set(tmp, Util.parsePollObject(list.get(i)));
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
        Zilche app = (Zilche) getApplication();
        if (app.getFav() == null || app.getMap() == null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("restart", 1);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }
        rv.getAdapter().notifyDataSetChanged();
    }

    public class RVadapter extends RecyclerView.Adapter<RVadapter.PollViewHolder> {

        List<Poll> polls;
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                Intent i = new Intent(CategoryActivity.this, PollViewActivity.class);
                i.putExtra("poll", polls.get(pos));
                startActivity(i);
                overridePendingTransition(R.anim.right_to_left, 0);
            }

        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

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
            }
        }

        public RVadapter(List<Poll> pollList) {
            polls = pollList;
        }

        @Override
        public RVadapter.PollViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
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

    public void openMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenuInflater().inflate(R.menu.menu_category, menu.getMenu());
        if (sortBy == 0) {
            menu.getMenu().findItem(R.id.sort_date).setCheckable(false);
            menu.getMenu().findItem(R.id.sort_popular).setCheckable(true);
            menu.getMenu().findItem(R.id.sort_popular).setChecked(true);
        } else {
            menu.getMenu().findItem(R.id.sort_popular).setCheckable(false);
            menu.getMenu().findItem(R.id.sort_date).setCheckable(true);
            menu.getMenu().findItem(R.id.sort_date).setChecked(true);
        }
        menu.show();
    }

    public void sortPopular(MenuItem m) {
        sortBy = 0;
        skip = 0;
        pollList.clear();
        rv.getAdapter().notifyDataSetChanged();
        spinner.setVisibility(View.VISIBLE);
        populateList(skip, sortBy);
    }

    public void sortDate(MenuItem m) {
        sortBy = 1;
        skip = 0;
        pollList.clear();
        rv.getAdapter().notifyDataSetChanged();
        spinner.setVisibility(View.VISIBLE);
        populateList(skip, sortBy);
    }
}
