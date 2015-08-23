package com.zilche.zilche;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PollViewActivity extends ActionBarActivity {

    private GridItemView imageView;
    private TextView question;
    private TextView author;
    private TextView dateAdded;
    private RadioGroup radioGroup;
    private int option_count;
    private TextView total;
    private int category = 0;
    private TextView category_title;
    private SlidingPaneLayout spl;
    private Button submit_btn;
    private String id;
    private int checked;
    private LinearLayout lin;
    private TextView[] percentages;
    private HashMap<String, Integer> map;
    private int c;
    private ScrollView sv;
    private LinearLayout loading;
    private Poll poll;
    private ImageButton fav_button;
    private boolean favourite = false;
    private View background;
    private ListView comments;
    private String authorLogin;
    private LinearLayout ll;
    private LinearLayout comment_lay;
    private RelativeLayout lay;
    private LinearLayout cal;
    private LinkedList comments_list;
    private int comment_skip = 0;
    private TextView comment_count1;
    private TextView comment_count2;
    private int comment_count = 0;
    private int comment_total = 0;
    private boolean isLoading = false;
    private boolean complete = false;
    private LinearLayout reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviews);
        comments = (ListView) findViewById(R.id.comments);
        final View header = getLayoutInflater().inflate(R.layout.activity_poll_view, null, false);
        comments.addHeaderView(header);
        imageView = (GridItemView) findViewById(R.id.image_poll_view);
        question = (TextView) findViewById(R.id.title_poll_view);
        author = (TextView) findViewById(R.id.author_poll_view);
        dateAdded = (TextView) findViewById(R.id.date_poll_view);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_poll_view);
        category_title = (TextView) findViewById(R.id.category_poll_view);
        submit_btn = (Button) findViewById(R.id.submit_poll_view);
        lin = (LinearLayout) findViewById(R.id.result_poll_view);
        total = (TextView) findViewById(R.id.total);
        sv = (ScrollView) findViewById(R.id.scroll_view_poll);
        loading = (LinearLayout) findViewById(R.id.loading);
        fav_button = (ImageButton) findViewById(R.id.fav_poll_view);
        background = findViewById(R.id.background);
        comment_lay = (LinearLayout) findViewById(R.id.comment_title);
        cal = (LinearLayout) findViewById(R.id.cal);
        comment_count1 = (TextView) findViewById(R.id.comment_count1);
        comment_count2 = (TextView) findViewById(R.id.comment_count2);
        reload = (LinearLayout) findViewById(R.id.retry_bg);

        comments_list = new LinkedList<>();
        comments.setAdapter(new ListAdapter(comments_list));

        final Zilche app = (Zilche) getApplication();

        if (app.getMap() == null || app.getFav() == null) {
            finish();
            overridePendingTransition(0, 0);
            return;
        }

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav_button.setEnabled(false);
                fav_button.setClickable(false);
                if (favourite) {
                    fav_button.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    ParseQuery<ParseObject> query = new ParseQuery<>("Favourite");
                    query.whereEqualTo("Key", poll.getId());
                    query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if(e == null) {
                                if (list.size() == 0) return;
                                list.get(0).deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            fav_button.setImageResource(R.drawable.ic_favorite_white_24dp);
                                            fav_button.setEnabled(true);
                                            fav_button.setClickable(true);
                                            Toast.makeText(PollViewActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                        } else {
                                            app.getFav().remove(poll.getId());
                                            favourite = false;
                                            fav_button.setEnabled(true);
                                            fav_button.setClickable(true);
                                        }
                                    }
                                });
                            } else {
                                fav_button.setImageResource(R.drawable.ic_favorite_white_24dp);
                                fav_button.setEnabled(true);
                                fav_button.setClickable(true);
                                Toast.makeText(PollViewActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    fav_button.setImageResource(R.drawable.ic_favorite_white_24dp);
                    ParseObject po = new ParseObject("Favourite");
                    po.put("user", ParseUser.getCurrentUser().getObjectId());
                    po.put("Key", poll.getId());
                    po.put("Fav", 1);
                    po.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(PollViewActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                fav_button.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                                fav_button.setEnabled(true);
                                fav_button.setClickable(true);
                            } else {
                                app.getFav().put(poll.getId(), 1);
                                favourite = true;
                                fav_button.setEnabled(true);
                                fav_button.setClickable(true);
                            }
                        }
                    });
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        poll = extras.getParcelable("poll");

        map = app.getMap();
        if (app.getFav().get(poll.getId()) != null) {
            favourite = true;
            fav_button.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            fav_button.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            favourite = false;
        }

        category = poll.getCategory();
        if (category == 0)
            category_title.setText(getString(R.string.other));
        else
            category_title.setText(Util.strings[category]);
        id = poll.getId();
        populateView(poll);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
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
                PollViewActivity.this.finish();
                PollViewActivity.this.overridePendingTransition(0, 0);
            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
        ((ImageButton)findViewById(R.id.back_button_poll_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Util.noti_color[category]);
        }
        lay = (RelativeLayout) findViewById(R.id.header_poll_view);
        lay.setBackgroundColor(Util.title_color[category]);
        ll =(LinearLayout) findViewById(R.id.linlay_view_poll);
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        final int height = (int) (size.y - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics()));
        ll.setMinimumHeight(height);

        sv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (comment_lay == null || lay == null || cal == null ) {
                    sv.getViewTreeObserver().removeOnScrollChangedListener(this);
                    return;
                }
                int[] aa = new int[2];
                comment_lay.getLocationInWindow(aa);
                if (comment_lay.getVisibility() != View.GONE &&  aa[1] < lay.getHeight() + comment_lay.getHeight() / 2) {
                    cal.setVisibility(View.VISIBLE);
                } else {
                    cal.setVisibility(View.GONE);
                }
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_btn.setEnabled(false);
                checked = radioGroup.getCheckedRadioButtonId();
                if (checked == -1) {
                    submit_btn.setEnabled(true);
                } else {
                    final ParseObject po = ParseObject.createWithoutData("poll", poll.getId());
                    po.increment("total");
                    po.increment("votes" + Integer.toString(checked));
                    po.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                poll.getVotes()[checked]++;
                                final int[] votes = new int[poll.getCount()];
                                for (int i = 0; i < poll.getCount(); i++) {
                                    votes[i] = po.getInt("votes" + Integer.toString(i));
                                }
                                c = checked;
                                comment_count = po.getInt("comments_count");
                                comment_count1.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                                comment_count2.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                                generateResult(votes, poll.getOptions());
                                saveRecord(po.getObjectId(), checked);
                            } else {
                                submit_btn.setEnabled(true);
                                Toast.makeText(PollViewActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
              }
            }
        });

    }

    public void retrieveAuthorProfile(View view){
        if(authorLogin != null) {
            Intent t = new Intent(PollViewActivity.this, RetrieveProfileActivity.class);
            t.putExtra("authorName", authorLogin);
            t.putExtra("authorRealName", poll.getAuthor());
            startActivity(t);
        }
    }

    private void saveRecord(String id, final int c) {
        final ParseObject po = new ParseObject("Records");
        po.put("Key", id);
        if ( ParseUser.getCurrentUser() != null )
            po.put("user", ParseUser.getCurrentUser().getObjectId());
        else {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e != null) {

                    } else {
                        po.put("user", "null");
                    }
                }
            });
        }
        po.put("choice", c);
        final String id2 = id;
        po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) map.put(id2, c);
            }
        });
    }

    private void populateView(final Poll poll) {
        final String object_id = poll.getId();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("poll");
        query.getInBackground(object_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    final Poll poll2 = Util.parsePollObject(object, PollViewActivity.this);
                    question.setText(poll2.getQuestion());
                    dateAdded.setText(poll2.getDate_added());
                    if (poll2.getAnon() == 1) {
                        author.setText(getString(R.string.anonymous));
                    } else {
                        author.setText(poll2.getAuthor());
                        authorLogin = poll2.getAuthorLogin();
                    }
                    total.setText(Integer.toString(poll2.totalVotes()));
                    comment_count = object.getInt("comments_count");
                    if (poll2.hasImage() == 1) {
                        poll2.getFile().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                if (e == null) {
                                    BitmapWorker worker = new BitmapWorker(imageView, bytes);
                                    worker.execute();
                                    if (map.get(object_id) != null) {
                                        c = map.get(object_id);
                                        comment_count1.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                                        comment_count2.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                                        generateResult(poll2.getVotes(), poll2.getOptions());
                                    } else {
                                        populatePoll(poll2);
                                    }
                                    imageView.setVisibility(View.VISIBLE);
                                    sv.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);
                                } else {
                                    loading.setVisibility(View.GONE);
                                    reload.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        if (map.get(object_id) != null) {
                            c = map.get(object_id);
                            comment_count1.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                            comment_count2.setText(getString(R.string.comment_2) + " " + Integer.toString(comment_count));
                            generateResult(poll2.getVotes(), poll2.getOptions());
                        } else {
                            populatePoll(poll2);
                        }
                        imageView.setVisibility(View.GONE);
                        sv.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                } else {
                    loading.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void populatePoll(Poll poll) {
        String[] options = poll.getOptions();
        option_count = poll.getCount();
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(layParams);
        for (int i = 0; i < option_count; i++) {
            RadioButton rb = (RadioButton) View.inflate(this, R.layout.radiobutton, null);
            rb.setLayoutParams(layParams);
            rb.setText(options[i]);
            rb.setTextColor(0xff666666);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13,
                    getResources().getDisplayMetrics());
            rb.setPadding(pad, pad, pad, pad);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            rb.setId(i);
            View v = new View(this);
            v.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                    getResources().getDisplayMetrics())));
            v.setBackgroundColor(0xffcccccc);
            radioGroup.addView(rb);
            radioGroup.addView(v);
        }
    }

    private void generateResult(int[] votes, String[] options) {
        ll.setMinimumHeight(0);
        radioGroup.setVisibility(View.GONE);
        submit_btn.setVisibility(View.GONE);
        comment_lay.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13,
                getResources().getDisplayMetrics());

        int pad2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics());
        par.setMargins(0, pad, pad2, pad);
        LinearLayout.LayoutParams par2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        par2.setMargins(0, pad, 0, pad);
        int total = 0;
        int total_tmp = 0;
        for (int i = 0; i < options.length; i++)
            total += votes[i];
        this.total.setText(Integer.toString(total));
        int percent = 100;
        percentages = new TextView[options.length];
        for (int i = 0; i < options.length; i++) {
            LinearLayout ll1 = new LinearLayout(getApplicationContext());
            ll1.setLayoutParams(layParams);
            TextView percentage = new TextView(getApplicationContext());
            percentages[i] = percentage;
            percentage.setLayoutParams(par2);
            percentage.setTextColor(0xff666666);
            percentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            percentage.setEms(4);
            percentage.setTypeface(null, Typeface.BOLD);
            percentage.setGravity(Gravity.CENTER);
            total_tmp += votes[i];
            if (i == c) {
                percentage.setTextColor(0xFFEF5350);
            }
            int aatmp = 0;
            if (total_tmp == total) {
                percentage.setText(Integer.toString(percent) + "%");
                percent -= percent;
            }
            else {
                aatmp = (int) ((double) (votes[i]) / total * 100);
                percentage.setText(Integer.toString(aatmp) + "%");
            }
            percent -= aatmp;
            TextView options_tv = new TextView(getApplicationContext());
            options_tv.setLayoutParams(par);
            options_tv.setTextColor(0xff666666);
            options_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            options_tv.setGravity(Gravity.CENTER_VERTICAL);
            options_tv.setText(options[i]);
            ll1.addView(percentage);
            ll1.addView(options_tv);
            View v1 = new View(getApplicationContext());
            v1.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                    getResources().getDisplayMetrics())));
            v1.setBackgroundColor(0xffcccccc);
            lin.addView(ll1);
            lin.addView(v1);
        }
        lin.setVisibility(View.VISIBLE);
        loadComments(comment_skip);
        comments.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem >= totalItemCount && !isLoading && !complete) {
                    isLoading = true;
                    loadComments(comment_skip);
                }
            }
        });
    }

    public void loadComments(int skip) {
        isLoading = true;
        if (skip == 0)
            comments_list.clear();
        comments_list.add("progress");
        HeaderViewListAdapter ad = (HeaderViewListAdapter) (comments.getAdapter());
        ListAdapter la = (ListAdapter) ad.getWrappedAdapter();
        la.notifyDataSetChanged();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comment");
        query.setLimit(20);
        query.whereEqualTo("pollId", poll.getId());
        query.setSkip(skip * 20);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    comments_list.removeLast();
                    for (ParseObject o : list) {
                        Comment c = Util.parseComment(o);
                        comments_list.add(c);
                    }
                    comment_total += list.size();
                    if (list.size() < 20) {
                        complete = true;
                    }
                    HeaderViewListAdapter ad = (HeaderViewListAdapter) (comments.getAdapter());
                    ListAdapter la = (ListAdapter) ad.getWrappedAdapter();
                    la.notifyDataSetChanged();
                    comment_skip++;
                    isLoading = false;
                } else {
                    comments_list.removeLast();
                    comments_list.add("reload");
                    HeaderViewListAdapter ad = (HeaderViewListAdapter) (comments.getAdapter());
                    ListAdapter la = (ListAdapter) ad.getWrappedAdapter();
                    la.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
    }

    public class BitmapWorker extends AsyncTask<Integer, Void, Bitmap>  {

        private final WeakReference<GridItemView> imageViewReference;
        private final WeakReference<byte[]> data;

        public BitmapWorker(GridItemView iv, byte[] data) {
            this.imageViewReference = new WeakReference<GridItemView>(iv);
            this.data = new WeakReference<byte[]>(data);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            return BitmapFactory.decodeByteArray(data.get(), 0, data.get().length);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageViewReference.get().setImageBitmap(bitmap);
            imageViewReference.get().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Zilche app = (Zilche) getApplication();
        if (app.getMap() == null || app.getFav() == null) {
            finish();
            overridePendingTransition(0, 0);
        }
    }


    public class ListAdapter extends BaseAdapter {

        LinkedList comment;

        public ListAdapter(LinkedList list) {
            comment = list;
        }

        @Override
        public int getCount() {
            return comment.size();
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
            View v = convertView;
            if (comment.get(position) instanceof String) {
                if (((String) comment.get(position)).compareTo("progress") == 0) {
                    LayoutInflater vi = getLayoutInflater();
                    v = vi.inflate(R.layout.progress_spinner_small, null);
                    return v;
                } else if (((String) comment.get(position)).compareTo("reload") == 0) {
                    LayoutInflater vi = getLayoutInflater();
                    v = vi.inflate(R.layout.reload_small, null);

                    v.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            comments_list.removeLast();
                            loadComments(comment_skip);
                        }
                    });
                    return v;
                }
                return null;
            }
            LayoutInflater vi = getLayoutInflater();
            v = vi.inflate(R.layout.comment_item, null);
            ImageView iv = (ImageView) v.findViewById(R.id.image);
            TextView author = (TextView) v.findViewById(R.id.author);
            TextView date = (TextView) v.findViewById(R.id.date);
            TextView comment = (TextView) v.findViewById(R.id.comment);
            TextView mod = (TextView) v.findViewById(R.id.mod);
            TextView op = (TextView) v.findViewById(R.id.op);
            Comment c = (Comment) this.comment.get(position);
            author.setText(c.getName());
            date.setText(c.getDate_added());
            comment.setText(c.getComment_text());
            if (c.getOp() == 1) {
                op.setVisibility(View.VISIBLE);
            } else {
                op.setVisibility(View.GONE);
            }
            if (c.getMod() == 1) {
                mod.setVisibility(View.VISIBLE);
            } else {
                mod.setVisibility(View.GONE);
            }
            if (c.hasImage()) {
                Bitmap fac = BitmapFactory.decodeByteArray(c.getImage(), 0, c.getImage().length);
                Bitmap bm = Bitmap.createScaledBitmap(fac, (int) Util.convertDpToPixel(30, PollViewActivity.this),
                            (int) Util.convertDpToPixel(30, PollViewActivity.this), true);
                iv.setImageBitmap(bm);
            }
            return v;
        }
    }

    public void addComment(View v) {
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent i = new Intent(this, SignUpActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            Intent i = new Intent(this, CreateCommentActivity.class);
            i.putExtra("poll", poll.getId());
            i.putExtra("question", poll.getQuestion());
            i.putExtra("choice", poll.getOptions()[c]);
            i.putExtra("category", category);
            i.putExtra("isAnon", poll.getAnon());
            i.putExtra("owner", poll.getAuthorLogin());
            startActivityForResult(i, 33);
            overridePendingTransition(R.anim.right_to_left, 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 33) {
                comment_skip = 0;
                complete = false;
                int total = data.getExtras().getInt("total");
                comment_count1.setText(getString(R.string.comment_2) + " " + Integer.toString(total));
                comment_count2.setText(getString(R.string.comment_2) + " " + Integer.toString(total));
                loadComments(comment_skip);
            }
        }
    }

    public void reload(View v) {
        reload.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        populateView(poll);
    }


}
