package com.zilche.zilche;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.View;

public class MyPollActivity extends ActionBarActivity {
    GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_poll);
        gv = (GridView) findViewById(R.id.gridv);
        gv.setAdapter(new PollListAdapter(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void backButtonLogin(View v) {
        finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public class PollListAdapter extends BaseAdapter {

        private Context c;
        private String[] polls = {
                "What's is the color?", "What is the number?"
        };
        private String[] times= {
                "2hr ago", "1day ago"
        };


        public PollListAdapter(Context c) {
            this.c = c;
        }

        @Override
        public int getCount() {
            return polls.length;
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
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.mypolls, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.poll_name);
            tv.setText(polls[position]);
            TextView timev = (TextView) convertView.findViewById(R.id.time);
            timev.setText(times[position]);
            convertView.setTag(position);
            ImageView iv = (ImageView) convertView.findViewById(R.id.assignment);
            iv.setImageResource(R.mipmap.ic_assessment_white_24dp);
            iv.setColorFilter(Color.parseColor("#11110000"));
            return convertView;
        }
    }

}
