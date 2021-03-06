package com.zilche.zilche;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyPollActivity extends ActionBarActivity {

    private boolean load = false;
    private TextView title;
    int category = 0;
    private ImageButton sort;
    private LinkedList<Poll> pollList;
    private ProgressBar spinner;
    private RecyclerView rv;
    private HashMap<String, Integer> map;
    private int skip = 0;
    private int complete = 0;
    private int visibleThreshold = 15;
    private SlidingPaneLayout spl;
    private View background;
    private LinearLayout reload_bg_full;
    private Date LastCreatedAt;

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
        background = findViewById(R.id.background);
        reload_bg_full = (LinearLayout) findViewById(R.id.reload_bg_full);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
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
                int firstVisibleItem = llm.findFirstVisibleItemPosition();
                if (!load && ((pollList.size() < 0 && pollList.size() - firstVisibleItem <= visibleThreshold) || !rv.canScrollVertically(1))) {
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
        query.whereEqualTo("author_id", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(25);
        query.whereNotEqualTo("archived", 1);
        if (skip2 % 400 == 0 && skip2 != 0) {
            query.whereLessThanOrEqualTo("createdAt", LastCreatedAt);
            query.setSkip(skip2 % 400 * 25 + 1);
        } else {
            query.setSkip(skip2 * 25);
        }
        query.orderByDescending("createdAt");
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
                            pollList.add(Util.parsePollObject(list.get(i), MyPollActivity.this));
                            if (i == list.size() - 1) {
                                LastCreatedAt = list.get(i).getCreatedAt();
                            }
                        }

                        rv.getAdapter().notifyDataSetChanged();
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
                        reload_bg_full.setVisibility(View.VISIBLE);
                    }
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
                            builder.setTitle(getString(R.string.delete_confirm));
                            builder.setMessage(getString(R.string.delete_confirm_2));
                            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final int tag = (int) tag_v.getTag();
                                    final ParseObject o = ParseObject.createWithoutData("poll", polls.get(tag).getId());
                                    if (!o.isDataAvailable()) {
                                        o.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                if (e == null) {
                                                    o.put("archived", 1);
                                                    o.saveInBackground();
                                                    polls.remove(tag);
                                                    rv.getAdapter().notifyDataSetChanged();
                                                } else {
                                                    Toast.makeText(MyPollActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        o.put("archived", 1);
                                        o.saveInBackground();
                                        polls.remove(tag);
                                        rv.getAdapter().notifyDataSetChanged();
                                    }
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
        View.OnClickListener reload = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollList.removeLast();
                Poll tmp = new Poll();
                tmp.setId("-1");
                pollList.add(tmp);
                rv.getAdapter().notifyDataSetChanged();
                populateList(skip);
            }
        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

            LinearLayout reload_bg;
            Button reload;
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
                reload_bg = (LinearLayout) itemView.findViewById(R.id.reload_bg);
                reload = (Button) itemView.findViewById(R.id.reload);
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
                pollViewHolder.reload_bg.setVisibility(View.GONE);
                return;
            } if (polls.get(i).getId().compareTo("-2") == 0 ) {
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
        }
    }

    public void reload(View v) {
        reload_bg_full.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        populateList(skip);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
    }

}
