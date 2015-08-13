package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class SearchList extends AppCompatActivity {

    private ImageButton back_button;
    private SearchView sv;
    private RecyclerView rv;
    private LinkedList<Poll> pollList;
    private ProgressBar spinner;
    private int isRefreshing = 0;
    private HashMap<String, Integer> map;
    private int skip = 0;
    private int complete = 0;
    private int visibleThreshold = 15;
    private boolean load = false;
    private String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        Zilche app = (Zilche) getApplication();
        map = app.getMap();
        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        sv = (SearchView) findViewById(R.id.my_search_bar);
        sv.setFocusable(true);
        sv.setIconified(false);
        back_button = (ImageButton) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(back_button.getWindowToken(), 0);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                skip = 0;
                str = query;
                if (pollList.size() == 0) {
                    spinner.setVisibility(View.VISIBLE);
                }
                populateList(skip, query);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv_cat);
        rv.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        pollList = new LinkedList<>();
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
                        if (pollList.size() != 0)
                            populateList(skip, str);
                    }
                }

            }
        });
    }

    public void populateList(final int skip2, String queryStr) {
        load = true;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
        query.whereMatches("question", "(" + queryStr + ")", "i");

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("poll");
        query2.whereContains("nickname", queryStr);
        query2.whereEqualTo("anon", 0);

        List<ParseQuery<ParseObject>> list = new LinkedList<>();
        list.add(query);
        list.add(query2);

        ParseQuery<ParseObject> q = ParseQuery.or(list);

        q.setLimit(15);
        q.setSkip(skip2 * 15);
        q.orderByDescending("lastUpdate");
        q.findInBackground(new FindCallback<ParseObject>() {
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
                    if (list.size() != 0) {
                        if (skip2 == 0) {
                            pollList.clear();
                            spinner.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < list.size(); i++) {
                            pollList.add(Util.parsePollObject(list.get(i)));
                        }
                        rv.getAdapter().notifyDataSetChanged();
                        skip++;
                    } else {
                        pollList.clear();
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    load = false;
                } else {
                    if (pollList.size() != 0 && pollList.getLast() != null && pollList.getLast().getId().compareTo("-1") == 0) {
                        pollList.removeLast();
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    load = false;
                    Toast.makeText(SearchList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (spinner.getVisibility() == View.VISIBLE) {
                    spinner.setVisibility(View.GONE);
                }
            }
        });
    }

    public class RVadapter extends RecyclerView.Adapter<RVadapter.PollViewHolder> {

        List<Poll> polls;
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                Intent i = new Intent(SearchList.this, PollViewActivity.class);
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

}