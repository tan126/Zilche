package com.zilche.zilche;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


//need to store category in poll class!!!!

public class PollViewActivity extends Activity {

    private int category = 0;
    private int[] title_color = {
            0xff2196F3, 0xffF44336, 0xff673AB7, 0xff9C27B0, 0xffCDDC39, 0xffFF5722, 0xffFDD835, 0xff9E9E9E, 0xff4CAF50, 0xff795548,
            0xff009688, 0xff00BCD4, 0xffE91E63, 0xffFF9800, 0xff607D8B
    };
    private  int[] noti_color = {
            0xff1976D2, 0xffD32F2F, 0xff512DA8, 0xff7B1FA2, 0xffAFB42B, 0xffE64A19, 0xffFBC02D, 0xff616161, 0xff388E3C, 0xff5D4037,
            0xff00796B, 0xff0097A7, 0xffC2185B, 0xffF57C00, 0xff455A64
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category = extras.getInt("category_index");
        }
        SlidingPaneLayout spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

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
            getWindow().setStatusBarColor(noti_color[category]);
        }
        final RelativeLayout lay = (RelativeLayout) findViewById(R.id.header_poll_view);
        lay.setBackgroundColor(title_color[category]);
        populatePoll();
        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_view_poll);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linlay_view_poll);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = (int) (size.y - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 79,
                getResources().getDisplayMetrics()));
        ll.setMinimumHeight(height);
    }

    private void populatePoll() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_poll_view);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(layParams);
        for (int i = 0; i < 3; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(layParams);
            rb.setText("Yes!    ");
            rb.setTextColor(0xff666666);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            rb.setPadding(30, 10, 30, 10);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            View v = new View(this);
            v.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                    getResources().getDisplayMetrics())));
            v.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            radioGroup.addView(rb);
            radioGroup.addView(v);
        }
    }

}
