package com.zilche.zilche;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;


public class tmpNewPoll extends ActionBarActivity {

    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_new_poll);
        b = (Button) findViewById(R.id.add);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoll();
            }
        });

        //poll.saveInBackground();

        //ParseObject poll_id = new ParseObject("poll_id");
        //poll_id.put("value", 0);
        //poll_id.saveInBackground();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tmp_new_poll, menu);
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
        //finish();
        Intent i = new Intent(tmpNewPoll.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void addPoll() {
        b.setText("Clicked");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("poll_id");
        query.getInBackground("ioqSxO1iQY", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be poll_id
                    int pollID = object.getInt("value");
                    object.increment("value");
                    object.saveInBackground();
                    ParseObject poll = new ParseObject("poll");
                    poll.put("question", "When shall we have dinner tonight?");
                    poll.put("optionNum", 4);
                    poll.addAllUnique("options", Arrays.asList("Oishi", "KFC", "Ichiban", "Red Lobster"));
                    for (int i = 0; i < 4; i ++ )
                        poll.add("votes", 0);
                    poll.put("author", ParseUser.getCurrentUser().toString());
                    poll.put("id", pollID + 1);
                    poll.saveInBackground();
                } else {
                    // something went wrong
                }
            }
        });
    }

}
