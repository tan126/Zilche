package com.zilche.zilche;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class PollViewActivity extends Activity {

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
    private int category = 0;
    private TextView category_title;
    private SlidingPaneLayout spl;
    private Button submit_btn;
    private String id;
    public int checked;
    private LinearLayout lin;

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

        Bundle extras = getIntent().getExtras();
        final Poll poll = extras.getParcelable("poll");

        question.setText(poll.getQuestion());
        dateAdded.setText(poll.getDate_added());
        author.setText(" " + poll.getAuthor());
        category = poll.getCategory();
        imageView.setVisibility(View.GONE);
        category_title.setText(poll.getCategory_title());
        id = poll.getId();

        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelOpened(View panel) {
                PollViewActivity.this.finish();
                PollViewActivity.this.overridePendingTransition(0, R.anim.toolbar_dissapear);
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
            getWindow().setStatusBarColor(noti_color[category]);
        }
        final RelativeLayout lay = (RelativeLayout) findViewById(R.id.header_poll_view);
        lay.setBackgroundColor(title_color[category]);
        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_view_poll);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linlay_view_poll);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = (int) (size.y - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 77,
                getResources().getDisplayMetrics()));
        ll.setMinimumHeight(height);
        populatePoll(poll);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_btn.setEnabled(false);
                checked = radioGroup.getCheckedRadioButtonId();
                if (checked == -1) {
                    // failed
                    submit_btn.setEnabled(true);
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
                    //Toast.makeText(PollViewActivity.this, id, Toast.LENGTH_SHORT).show();
                    query.getInBackground(id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                final int options_count = poll.getCount();
                                JSONArray votesObject = object.getJSONArray("votes");
                                final int[] votes = new int[options_count];
                                for( int i = 0; i < options_count; i ++ ) {
                                    try{
                                        votes[i] = votesObject.getInt(i);
                                    } catch ( JSONException e2 ){
                                        Log.d("JSON", "Array index out of bound");
                                    }
                                }
                                votes[checked]++;
                                try {
                                    votesObject.put(checked, votesObject.getInt(checked) + 1);
                                    object.remove("votes");
                                    for (int i = 0; i < votesObject.length(); i++) {
                                        object.add("votes", votesObject.getInt(i));
                                    }
                                } catch (JSONException e2) {
                                    Log.d("JSON", "Array index out of bound");
                                }
                                object.increment("total");
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        radioGroup.setVisibility(View.GONE);
                                        submit_btn.setVisibility(View.GONE);
                                        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        par.setMargins(30, 30, 30, 30);
                                        LinearLayout.LayoutParams par2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        par2.setMargins(10, 30, 10, 30);
                                        String[] options = poll.getOptions();
                                        int total = 0;
                                        for (int i = 0; i < options_count; i++)
                                            total += votes[i];
                                        int percent = 100;
                                        for (int i = 0; i < options_count; i++) {
                                            LinearLayout ll = new LinearLayout(getApplicationContext());
                                            ll.setLayoutParams(layParams);
                                            TextView percentage = new TextView(getApplicationContext());
                                            percentage.setLayoutParams(par);
                                            percentage.setMinWidth(60);
                                            percentage.setTextColor(0xff666666);
                                            percentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                            percentage.setGravity(Gravity.CENTER);
                                            //percentage.setPadding(5, 5, 30, 30);
                                            int aatmp = 0;
                                            if (i == options_count - 1)
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
                                            //options_tv.setPadding(30, 30, 30, 30);
                                            options_tv.setText(options[i]);
                                            ll.addView(percentage);
                                            ll.addView(options_tv);
                                            View v = new View(getApplicationContext());
                                            v.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                                                    getResources().getDisplayMetrics())));
                                            v.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                            lin.addView(ll);
                                            lin.addView(v);
                                        }
                                        lin.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                //something went wrong
                                submit_btn.setEnabled(true);
                            }
                        }
                    });

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
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(layParams);
            rb.setText(options[i]);
            rb.setTextColor(0xff666666);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            rb.setPadding(30, 30, 30, 30);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            rb.setButtonDrawable(R.drawable.apptheme_btn_check_holo_light);
            rb.setId(i);
            View v = new View(this);
            v.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                    getResources().getDisplayMetrics())));
            v.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            radioGroup.addView(rb);
            radioGroup.addView(v);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.left_to_right);
    }

}
