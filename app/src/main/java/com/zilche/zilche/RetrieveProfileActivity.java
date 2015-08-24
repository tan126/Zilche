package com.zilche.zilche;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class RetrieveProfileActivity extends FragmentActivity {


    private ViewPager pager;
    private TextView name;
    private Frag frag_global;
    private SecondFragment frag2_global;
    private SlidingPaneLayout spl;
    private View background;
    private String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_profile);
        frag_global = new Frag();
        frag2_global = new SecondFragment();
        findViewById(R.id.back_button_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.left_to_right);
            }
        });

        Intent i = getIntent();
        if (i != null && i.getExtras() != null && i.getExtras().getString("author") != null) {
            author = i.getExtras().getString("author");
        } else {
            Toast.makeText(this, "User profile does not exist", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(0, 0);
        }
        frag2_global.setAuthor(author);
        frag_global.setAuthor(author);
        name = (TextView) findViewById(R.id.name);
        pager = (ViewPager) findViewById(R.id.viewPager);

        pager.setPageTransformer(false, new ZoomOutPageTransformer());
        pager.setAdapter(new ProfileAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    spl.setmCanSlide(true);
                } else {
                    spl.setmCanSlide(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip2);
        stl.setDistributeEvenly(true);
        stl.setHorizontalScrollBarEnabled(false);
        stl.setCustomTabView(R.layout.custom_tab, 0);
        stl.setSelectedIndicatorColors(0xff3F51B5);
        stl.setViewPager(pager);

        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        background = findViewById(R.id.background);

        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (Build.VERSION.SDK_INT >= 21) {
                    int off = (int) ((1 - slideOffset) * 250);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor((0xFF303F9F & 0x00ffffff) | (off << 24));
                }
                int color = (int) ((1 - slideOffset) * 170);
                background.setBackgroundColor(0x00000000 | (color << 24));
            }

            @Override
            public void onPanelOpened(View panel) {
                RetrieveProfileActivity.this.finish();
                RetrieveProfileActivity.this.overridePendingTransition(0, 0);
            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
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
                case 0:
                    return frag_global;
                case 1:
                    frag = frag2_global;
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

        TextView email;
        TextView user;
        TextView birthday;
        TextView intro;
        TextView age;
        TextView gender;
        TextView country;
        ImageView iv;
        byte[] fullImage;
        String author;

        public void setAuthor(String author) {
            this.author = author;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_profile, container, false);
            email = (TextView) v.findViewById(R.id.email_pi);
            user = (TextView) v.findViewById(R.id.name_pi);
            birthday = (TextView) v.findViewById(R.id.bday_pi);
            intro = (TextView) v.findViewById(R.id.intro_pi);
            age = (TextView) v.findViewById(R.id.age_pi);
            gender = (TextView) v.findViewById(R.id.gender_pi);
            country = (TextView) v.findViewById(R.id.country_pi);
            iv = (ImageView) v.findViewById(R.id.image_pi);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", author);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            update(list.get(0));
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, R.anim.left_to_right);
                    }
                }
            });
            return v;
        }

        public void update(ParseUser u) {
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
            if (u.getDate("bday") != null) {
                Date d = u.getDate("bday");
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
            byte[] image = u.getBytes("image");
            if (image != null) {
                Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(image, 0, image.length), (int)Util.convertDpToPixel(54, getActivity()),
                        (int)Util.convertDpToPixel(54, getActivity()), true);
                iv.setImageBitmap(bm);
                u.getParseFile("full_image").getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            fullImage = bytes;
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (fullImage != null) {
                                        Intent i = new Intent(getActivity(), FullScreenImageActivity.class);
                                        i.putExtra("image", fullImage);
                                        startActivity(i);
                                        getActivity().overridePendingTransition(R.anim.fade_in, 0);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }

    }


    public static class SecondFragment extends Fragment {

        private SearchView sv;
        private RecyclerView rv;
        private LinkedList<Poll> pollList;
        private ProgressBar spinner;
        private HashMap<String, Integer> map;
        private int skip = 0;
        private int complete = 0;
        private int visibleThreshold = 15;
        private boolean load = false;
        private LinearLayout reload_bg_full;
        private Button reload_btn;
        private String author;

        public void setAuthor(String author) {
            this.author = author;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_profile2, container, false);

            Zilche app = (Zilche) getActivity().getApplication();
            map = app.getMap();
            spinner = (ProgressBar) v.findViewById(R.id.progress_bar);
            reload_bg_full = (LinearLayout) v.findViewById(R.id.reload_bg_full);
            reload_btn = (Button) v.findViewById(R.id.reload_btn);
            reload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reload_bg_full.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    populateList(skip);
                }
            });

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
            query.whereEqualTo("author", author);

            query.whereNotEqualTo("anon", 1);
            query.setLimit(25);
            query.whereNotEqualTo("archived", 1);
            query.setSkip(skip2 * 25);
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
            View.OnClickListener onclick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = (int) v.getTag();
                    Intent i = new Intent(getActivity(), PollViewActivity.class);
                    i.putExtra("poll", polls.get(pos));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.right_to_left, 0);
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

            public class PollViewHolder extends RecyclerView.ViewHolder {

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
                    reload_bg = (LinearLayout) itemView.findViewById(R.id.reload_bg);
                    reload = (Button) itemView.findViewById(R.id.reload);
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
            rv.getAdapter().notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
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
    }

}
