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
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.Date;
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
    private SlidingPaneLayout spl;
    private View background;
    private LinearLayout reload_bg_full;
    private Date lastCreated;
    private int lastVotes;

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
        reload_bg_full = (LinearLayout) findViewById(R.id.reload_bg_full);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        background = findViewById(R.id.background);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (Build.VERSION.SDK_INT >= 21) {
                    int off = (int) ((1 - slideOffset) * 250);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor((Util.noti_color[category] & 0x00ffffff) | (off << 24));
                }
                int color = (int) ((1 - slideOffset) * 170);
                background.setBackgroundColor(0x00000000 | (color << 24));
            }

            @Override
            public void onPanelOpened(View panel) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
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
                            Toast.makeText(CategoryActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
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
                int firstVisibleItem = llm.findFirstVisibleItemPosition();
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

    public void populateList(final int skip2, final int sortBy) {
        load = true;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        query.setLimit(25);
        if (sortBy == 1 && skip2 % 400 == 0 && skip2 != 0 && isRefreshing == 0) {
            query.whereLessThanOrEqualTo("createdAt", lastCreated);
            query.setSkip(skip2 % 400 * 25 + 1);
        } else if (sortBy == 0 && skip2 % 400 == 0 && skip2 != 0 && isRefreshing == 0) {
            query.whereLessThanOrEqualTo("total", lastVotes);
            query.setSkip(skip2 % 400 * 25 + 1);
        } else {
            query.setSkip(skip2 * 25);
        }
        query.whereNotEqualTo("archived", 1);
        if (category != 0)
            query.whereEqualTo("category", category);
        if (sortBy == 1) {
            query.orderByDescending("createdAt");
        } else {
            query.orderByDescending("total");
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (reload_bg_full.getVisibility() == View.VISIBLE) {
                        reload_bg_full.setVisibility(View.GONE);
                    }
                    if (pollList.size() != 0 && pollList.getLast() != null && pollList.getLast().getId().compareTo("-1") == 0) {
                        pollList.removeLast();
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    if (list.size() < 25) {
                        complete = 1;
                    } else {
                        complete = 0;
                    }
                    if (list != null && list.size() != 0) {
                        if (skip2 == 0) {
                            pollList.clear();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            pollList.add(Util.parsePollObject(list.get(i), CategoryActivity.this));
                            if (i == list.size() - 1) {
                                if (sortBy == 1)
                                    lastCreated = list.get(i).getCreatedAt();
                                else
                                    lastVotes = list.get(i).getInt("total");
                            }
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
                        Poll tmp = new Poll();
                        tmp.setId("-2");
                        pollList.add(tmp);
                        rv.getAdapter().notifyDataSetChanged();
                    } else {
                        if (skip == 0 && pollList.size() != 0) {
                        } else {
                            reload_bg_full.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (spinner.getVisibility() == View.VISIBLE) {
                    spinner.setVisibility(View.GONE);
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
        View.OnClickListener reload = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollList.removeLast();
                Poll tmp = new Poll();
                tmp.setId("-1");
                pollList.add(tmp);
                rv.getAdapter().notifyDataSetChanged();
                populateList(skip, sortBy);
            }
        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

            LinearLayout reload_bg;
            Button reload;
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
                reload = (Button) itemView.findViewById(R.id.reload);
                reload_bg = (LinearLayout) itemView.findViewById(R.id.reload_bg);
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
                pollViewHolder.reload_bg.setVisibility(View.GONE);
                return;
            } if (polls.get(i).getId().compareTo("-2") == 0 ){
                pollViewHolder.pb.setVisibility(View.GONE);
                pollViewHolder.cv.setVisibility(View.GONE);
                pollViewHolder.reload_bg.setVisibility(View.VISIBLE);
                pollViewHolder.reload.setOnClickListener(reload);
                return;
            } else {
                pollViewHolder.reload_bg.setVisibility(View.GONE);
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
            pollViewHolder.author.setText(p.getAnon() == 1 ? getString(R.string.anonymous) : p.getAuthor());
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

    public void reload(View v) {
        reload_bg_full.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        populateList(skip, sortBy);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
    }

}
