package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import static com.zilche.zilche.R.array.menu_text_array;


public class MainActivity extends AppCompatActivity {
    // Global variable
    TextView welcomeText;
    ListView listView;
    SlidingPaneLayout slidingPane;
    String[] menuText = {"Poll", "Survey", "Settings", "Log Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slidingPane = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        listView = (ListView) findViewById(R.id.MenuList);
        //welcomeText = (TextView) findViewById(R.id.hello_world);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_text_array));
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        showActionBar();
        ImageButton menulist = (ImageButton) findViewById(R.id.menu_list);
        menulist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //welcomeText.setText("changed");
                if (slidingPane.isOpen() == true)
                    slidingPane.closePane();
                else
                    slidingPane.openPane();
            }
        });
        return true;
    }

    private void showActionBar() {
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_custom, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled (false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
