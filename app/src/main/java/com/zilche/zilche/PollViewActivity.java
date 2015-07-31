package com.zilche.zilche;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;

// todo: change button to floating button
public class PollViewActivity extends ActionBarActivity {

    private int[] title_color = {
            0xff2196F3, 0xffF44336, 0xff673AB7, 0xff9C27B0, 0xffCDDC39, 0xffFF5722, 0xffFDD835, 0xff9E9E9E, 0xff4CAF50, 0xff795548,
            0xff009688, 0xff00BCD4, 0xffE91E63, 0xffFF9800, 0xff607D8B
    };
    private int[] noti_color = {
            0xff1976D2, 0xffD32F2F, 0xff512DA8, 0xff7B1FA2, 0xffAFB42B, 0xffE64A19, 0xffFBC02D, 0xff616161, 0xff388E3C, 0xff5D4037,
            0xff00796B, 0xff0097A7, 0xffC2185B, 0xffF57C00, 0xff455A64
    };
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

        Bundle extras = getIntent().getExtras();
        final Poll poll = extras.getParcelable("poll");
        Zilche app = (Zilche) getApplication();
        map = app.getMap();

        showGraph_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gpIntent = new Intent(PollViewActivity.this, BarChart.class);
                gpIntent.putExtra("poll", poll);
                startActivity(gpIntent);
            }
        });

        question.setText(poll.getQuestion());
        dateAdded.setText(poll.getDate_added());
        int t = 0;
        for (int i = 0; i < poll.getCount(); i++) {
            t += poll.getVotes()[i];
        }
        total.setText(Integer.toString(t));
        if (poll.getAnon() == 1)
            author.setText(" Anonymous");
        else
            author.setText(" " + poll.getAuthor());
        category = poll.getCategory();
        //imageView.setVisibility(View.GONE);
        showGraph_btn.setVisibility(View.GONE);
        category_title.setText(poll.getCategory_title());
        id = poll.getId();

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
            getWindow().setStatusBarColor(noti_color[category]);
        }
        final RelativeLayout lay = (RelativeLayout) findViewById(R.id.header_poll_view);
        lay.setBackgroundColor(title_color[category]);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linlay_view_poll);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = (int) (size.y - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77, getResources().getDisplayMetrics()));
        ll.setMinimumHeight(height);
        if (map.get(poll.getId()) != null) {
            c = map.get(poll.getId());
            generateResult(poll.getVotes(), poll.getOptions());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("poll");
            query.getInBackground(id, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        final ParseObject object = parseObject;
                        final int[] votes = new int[poll.getCount()];
                        for (int i = 0; i < poll.getCount(); i++) {
                            votes[i] = object.getInt("votes" + Integer.toString(i));
                        }
                        poll.setVotes(votes);
                        updateResult(votes);
                    }
                }

                ;
            });
            return;
        }
        populatePoll(poll);
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
        ParseObject po = new ParseObject("Records");
        po.put("Key", id);
        po.put("user", ParseUser.getCurrentUser().getObjectId());
        po.put("choice", c);
        final String id2 = id;
        po.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) map.put(id2, c);
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
            if (i == c) {
                percentage.setTextColor(0xFFEF5350);
            }
            int aatmp = 0;
            if (i == options.length - 1)
                percentage.setText(Integer.toString(percent) + "%");
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

    private void updateResult(int[] result) {
        int percent = 100;
        int total = 0;
        for (int i = 0; i < result.length; i++) {
            total += result[i];
        }
        this.total.setText(Integer.toString(total));
        for (int i = 0; i < result.length; i++) {
            int aatmp = 0;
            if (i == result.length - 1)
                percentages[i].setText(Integer.toString(percent) + "%");
            else {
                aatmp = (int) ((double) (result[i]) / total * 100);
                percentages[i].setText(Integer.toString(aatmp) + "%");
            }
            percent -= aatmp;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
    }

}
