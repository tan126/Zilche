package com.zilche.zilche;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Vector;


public class tmpNewPoll extends ActionBarActivity {

    Button b;
    EditText question_text;
    EditText options_text;
    EditText category_text;
    ImageButton newOpt;
    TextView option_chosen;
    ParseObject poll;
    int optNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_new_poll);
        poll = new ParseObject("poll");
        optNum = 0;
        b = (Button) findViewById(R.id.add);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoll();
            }
        });
        question_text = (EditText) findViewById(R.id.question);
        options_text = (EditText) findViewById(R.id.opt);
        category_text = (EditText) findViewById(R.id.category);
        newOpt = (ImageButton) findViewById(R.id.newopt);
        option_chosen = (TextView) findViewById(R.id.opts);
        Vector<String> v = new Vector<String>();

        //poll.saveInBackground();
        newOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOpt(options_text.getText().toString());
                optNum ++;
                options_text.setText("");
            }
        });
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

    public void addOpt(String s) {
        if (optNum == 0)
            option_chosen.setText(s);
        else
            option_chosen.setText(option_chosen.getText() + ", " + s);
        poll.add("options", s);
    }

    public void addPoll() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("poll_id");
        Log.v("aaa", "111");
        query.getInBackground("ioqSxO1iQY", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be poll_id
                    int pollID = object.getInt("value");
                    object.increment("value");
                    object.saveInBackground();

                    poll.put("question", question_text.getText().toString());
                    poll.put("optionNum", optNum);
                    //poll.addAllUnique("options", Arrays.asList("Oishi", "KFC", "Ichiban", "Red Lobster"));
                    for (int i = 0; i < optNum; i ++ )
                        poll.add("votes", 0);
                    poll.put("author", ParseUser.getCurrentUser().toString());
                    poll.put("id", pollID + 1);
                    poll.put("category", category_text.getText().toString());
                    question_text.setText("");
                    options_text.setText("");
                    category_text.setText("");
                    option_chosen.setText("");
                    optNum = 0;
                    poll.saveInBackground();
                } else {
                    // something went wrong
                }
            }
        });
    }

}
