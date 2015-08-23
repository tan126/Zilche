package com.zilche.zilche;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class MyProfileActivity2 extends FragmentActivity {


    private ViewPager pager;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile2);

        findViewById(R.id.back_button_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity2.this, EditProfileActivity.class);
                startActivityForResult(i, 2222);
            }
        });

        name = (TextView) findViewById(R.id.name);
        pager = (ViewPager) findViewById(R.id.viewPager);

        pager.setPageTransformer(false, new ZoomOutPageTransformer());
        pager.setAdapter(new ProfileAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);

        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip2);
        stl.setDistributeEvenly(true);
        stl.setHorizontalScrollBarEnabled(false);
        stl.setCustomTabView(R.layout.custom_tab, 0);
        stl.setSelectedIndicatorColors(0xff3F51B5);
        stl.setViewPager(pager);

        name.setText("Profile");
    }

    private class ProfileAdapter extends FragmentPagerAdapter {

        private String[] title = {"About", "Activity"};

        public ProfileAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0 :
                    frag = new Frag();
                    break;
                case 1 :
                    frag = new SecondFragment();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    public static class Frag extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_profile, container, false);
            TextView email = (TextView) v.findViewById(R.id.email_pi);
            TextView user = (TextView) v.findViewById(R.id.name_pi);
            TextView birthday = (TextView) v.findViewById(R.id.bday_pi);
            TextView intro = (TextView) v.findViewById(R.id.intro_pi);
            TextView age = (TextView) v.findViewById(R.id.age_pi);
            TextView gender = (TextView) v.findViewById(R.id.gender_pi);
            TextView country = (TextView) v.findViewById(R.id.country_pi);
            ParseUser u = ParseUser.getCurrentUser();
            email.setText(u.getEmail());
            user.setText(u.getString("name"));
            if (u.getString("message") != null) {
                intro.setText(u.getString("message"));
            } else {
                intro.setText("unspecified");
            }
            if (u.getString("gender") != null) {
                gender.setText(u.getString("gender"));
            } else {
                gender.setText("unspecified");
            }
            if (u.getString("country") != null) {
                country.setText(u.getString("country"));
            } else {
                country.setText("unspecified");
            }
            if (u.getDate("birthday") != null) {
                Date d = u.getDate("birthday");
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int age_val = Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
                if (age_val < 1) age_val = 0;
                age.setText(Integer.toString(age_val));
                DateFormat df = new SimpleDateFormat("M-dd-yyyy");
                birthday.setText(df.format(d));
            } else {
                birthday.setText("unspecified");
                age.setText("unspecified");
            }
            return v;
        }
    }


    public static class SecondFragment extends Fragment {

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
        //private String str;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.profile_activity, container, false);

            Zilche app = (Zilche) getActivity().getApplication();
            map = app.getMap();
            spinner = (ProgressBar) v.findViewById(R.id.progress_bar);

            populateList(skip);

            rv = (RecyclerView) v.findViewById(R.id.rv_cat);
            rv.setHasFixedSize(true);
            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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
                                populateList(skip);
                        }
                    }

                }
            });

            return v;
        }

        public void populateList(final int skip2) {
            load = true;
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("poll");
            query.whereEqualTo("author", ParseUser.getCurrentUser().getEmail());

            query.whereNotEqualTo("anon", 1);
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
                        if (list.size() != 0) {
                            if (skip2 == 0) {
                                pollList.clear();
                                spinner.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < list.size(); i++) {
                                pollList.add(Util.parsePollObject(list.get(i), getActivity()));
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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Intent i = new Intent(getActivity(), PollViewActivity.class);
                    i.putExtra("poll", polls.get(pos));
                    startActivity(i);
                    //overridePendingTransition(R.anim.right_to_left, 0);
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

        public SecondFragment() {

        }

    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}

