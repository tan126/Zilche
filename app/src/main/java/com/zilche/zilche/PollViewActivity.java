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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private Button showGraph_btn;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_view);
        imageView = (GridItemView) findViewById(R.id.image_poll_view);
        question = (TextView) findViewById(R.id.title_poll_view);
        author = (TextView) findViewById(R.id.author_poll_view);
        dateAdded = (TextView) findViewById(R.id.date_poll_view);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_poll_view);
        category_title = (TextView) findViewById(R.id.category_poll_view);
        submit_btn = (Button) findViewById(R.id.submit_poll_view);
        lin = (LinearLayout) findViewById(R.id.result_poll_view);
        showGraph_btn = (Button) findViewById(R.id.showGraph);
        total = (TextView) findViewById(R.id.total);
        sv = (ScrollView) findViewById(R.id.scroll_view_poll);
        loading = (LinearLayout) findViewById(R.id.loading);
        fav_button = (ImageButton) findViewById(R.id.fav_poll_view);

        final Zilche app = (Zilche) getApplication();

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
                                            //fav_button.setImageResource(R.drawable.ic_favorite_border_red_24dp);
                                            fav_button.setEnabled(true);
                                            fav_button.setClickable(true);
                                            Toast.makeText(PollViewActivity.this, "Connection Failed. Please try again later", Toast.LENGTH_SHORT).show();
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
                                //fav_button.setImageResource(R.drawable.ic_favorite_border_red_24dp);
                                fav_button.setEnabled(true);
                                fav_button.setClickable(true);
                                Toast.makeText(PollViewActivity.this, "Connection Failed. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    fav_button.setImageResource(R.drawable.ic_favorite_white_24dp);
                    //fav_button.setImageResource(R.drawable.ic_favorite_border_red_24dp);
                    ParseObject po = new ParseObject("Favourite");
                    po.put("user", ParseUser.getCurrentUser().getObjectId());
                    po.put("Key", poll.getId());
                    po.put("Fav", 1);
                    po.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(PollViewActivity.this, "Connection Failed. Please try again later.", Toast.LENGTH_SHORT).show();
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
           // fav_button.setImageResource(R.drawable.ic_favorite_border_red_24dp);
        } else {
            fav_button.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            favourite = false;
        }

        showGraph_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gpIntent = new Intent(PollViewActivity.this, BarChart.class);
                gpIntent.putExtra("poll", poll);
                startActivity(gpIntent);
            }
        });
        category = poll.getCategory();
        showGraph_btn.setVisibility(View.GONE);
        if (category == 0)
            category_title.setText("Other");
        else
            category_title.setText(Util.strings[category]);
        id = poll.getId();
        populateView(poll);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {}

            @Override
            public void onPanelOpened(View panel) {
                PollViewActivity.this.finish();
                PollViewActivity.this.overridePendingTransition(0, R.anim.toolbar_dissapear);
            }

            @Override
            public void onPanelClosed(View panel) {}
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
        final RelativeLayout lay = (RelativeLayout) findViewById(R.id.header_poll_view);
        lay.setBackgroundColor(Util.title_color[category]);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linlay_view_poll);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = (int) (size.y - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics()));
        ll.setMinimumHeight(height);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_btn.setEnabled(false);
                checked = radioGroup.getCheckedRadioButtonId();
                if (checked == -1) {
                    submit_btn.setEnabled(true);
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("poll");
                    query.getInBackground(id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                final ParseObject object = parseObject;
                                poll.getVotes()[checked]++;
                                object.increment("total");
                                object.increment("votes" + Integer.toString(checked));
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            final int[] votes = new int[poll.getCount()];
                                            for (int i = 0; i < poll.getCount(); i++) {
                                                votes[i] = object.getInt("votes" + Integer.toString(i));
                                            }
                                            c = checked;
                                            generateResult(votes, poll.getOptions());
                                            saveRecord(object.getObjectId(), checked);
                                        } else {
                                            submit_btn.setEnabled(true);
                                            Toast.makeText(PollViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        };
                    });
                };
            }
        });


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
                Poll poll2 = Util.parsePollObject(object);
                question.setText(poll2.getQuestion());
                dateAdded.setText(poll2.getDate_added());
                if (poll2.getAnon() == 1) {
                    author.setText("Anonymous");
                } else {
                    author.setText(poll2.getAuthor());
                }
                total.setText(Integer.toString(poll2.totalVotes()));
                if (map.get(object_id) != null) {
                    c = map.get(object_id);
                    generateResult(poll2.getVotes(), poll2.getOptions());
                } else {
                    populatePoll(poll2);
                }
                if (poll2.hasImage() == 1) {
                    poll2.getFile().getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            BitmapWorker worker = new BitmapWorker(imageView, bytes);
                            worker.execute();
                            imageView.setVisibility(View.VISIBLE);
                            sv.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                        }
                    });
                } else {
                    imageView.setVisibility(View.GONE);
                    sv.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
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
            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            rb.setPadding(30, 30, 30, 30);
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
        radioGroup.setVisibility(View.GONE);
        submit_btn.setVisibility(View.GONE);
        showGraph_btn.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        par.setMargins(0, 30, 20, 30);
        LinearLayout.LayoutParams par2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        par2.setMargins(0, 30, 0, 30);
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
        Zilche z = (Zilche) getApplication();
        if (z.getMap() == null) {
            z.updateMap();
        }
    }

}
