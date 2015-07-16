package com.zilche.zilche;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {

    private TextView title;
    int category = 0;
    private int[] strings = {
            R.string.category_all_2, R.string.category_auto_2, R.string.category_education_2, R.string.category_entertainment_2,
            R.string.category_fashion_2, R.string.category_finance_2, R.string.category_food_2, R.string.category_games_2, R.string.category_it_2,
            R.string.category_pet_2, R.string.category_science_2, R.string.category_social_2, R.string.category_sports_2, R.string.category_tech_2,
            R.string.category_travel_2
    };
    private int[] title_color = {
            0xff2196F3, 0xffF44336, 0xff673AB7, 0xff9C27B0, 0xffCDDC39, 0xffFF5722, 0xffFDD835, 0xff9E9E9E, 0xff4CAF50, 0xff795548,
            0xff009688, 0xff00BCD4, 0xffE91E63, 0xffFF9800, 0xff607D8B
    };
    private  int[] noti_color = {
            0xff1976D2, 0xffD32F2F, 0xff512DA8, 0xff7B1FA2, 0xffAFB42B, 0xffE64A19, 0xffFBC02D, 0xff616161, 0xff388E3C, 0xff5D4037,
            0xff00796B, 0xff0097A7, 0xffC2185B, 0xffF57C00, 0xff455A64
    };
    private List<Poll> pollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Bundle extras = getIntent().getExtras();
        title = (TextView) findViewById(R.id.title_cat);
        if (extras != null) {
            category = extras.getInt("category_index");
        }
        title.setText(strings[category]);
        ((ImageButton)findViewById(R.id.back_button_cat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(noti_color[category]);
        }
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.header);
        lay.setBackgroundColor(title_color[category]);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_cat);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        Poll p = new Poll("What is 1 + 2?", new String[]{"1", "3", "4", "5"}, new int[]{1, 3, 4, 5},
                "3 minutes ago", "Aaron Kar Ee Hooooooooooooooooooo", 4, 2);
        p.setCategory_title("Education");
        pollList = new LinkedList<>();
        pollList.add(p);
        Poll p2 = new Poll("Should I go to work tomorrow?", new String[]{"1", "3", "4", "5"}, new int[]{1, 3, 4, 5},
                "3 minutes ago", "Aaron Kar Ee Hooooooooooooooooooo", 4, 0);
        //p2.setCategory_title("Other");
        pollList.add(new Poll("What is 1 + 5?", new String[]{"1", "3", "4", "5"}, new int[]{1, 3, 4, 5},
                "3 minutes ago", "Aaron Kar Ee Ha", 4, 2));
        pollList.add(p2);
        pollList.add(p2);
        pollList.add(p2);
        pollList.add(p2);
        RVadapter rva = new RVadapter(pollList);
        rv.setAdapter(rva);
    }

    public class RVadapter extends RecyclerView.Adapter<RVadapter.PollViewHolder> {

        List<Poll> polls;
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                Intent i = new Intent(CategoryActivity.this, PollViewActivity.class);
                i.putExtra("poll", polls.get(pos));
                startActivity(i);
                overridePendingTransition(R.anim.right_to_left, 0);
            }
        };

        public class PollViewHolder extends RecyclerView.ViewHolder{

            CardView cv;
            TextView question;
            TextView date;
            TextView category;

            public PollViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                question = (TextView) itemView.findViewById(R.id.question);
                date = (TextView) itemView.findViewById(R.id.date);
                category = (TextView) itemView.findViewById(R.id.category);
                cv.setOnClickListener(onclick);
            }
        }

        public RVadapter(List<Poll> pollList) {
            polls = pollList;
        }

        @Override
        public RVadapter.PollViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
            PollViewHolder pvh = new PollViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RVadapter.PollViewHolder pollViewHolder, int i) {
            Poll p = polls.get(i);
            if (p.getCategory() == 0) {
                pollViewHolder.category.setText(getString(R.string.other));
            } else {
                pollViewHolder.category.setText(strings[p.getCategory()]);
            }
            pollViewHolder.date.setText(p.getDate_added());
            pollViewHolder.question.setText(p.getQuestion());
            pollViewHolder.cv.setTag(i);
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


}
