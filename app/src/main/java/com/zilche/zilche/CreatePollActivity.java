package com.zilche.zilche;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

public class CreatePollActivity extends FragmentActivity implements OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ThirdFragment thirdFrag;
    ParseObject poll;
    ViewPager pager;
    EditText question;
    EditText option1;
    EditText option2;
    EditText option3;
    EditText option4;
    EditText option5;
    EditText option6;
    EditText option7;
    EditText option8;
    EditText option9;
    EditText option10;
    EditText[] options = new EditText[10];
    boolean validQuestion = false;
    int totalOptionsAdded = 0;
    boolean validOptions = false;
    int numOptions = 2;
    static int category = -1;
    boolean[] visiblePollOptions = {true, true, false, false, false, false, false, false ,false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        onPageSelected(0);

        poll = new ParseObject("Poll");
/*        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TextView textView = (TextView) findViewById(R.id.createpollfootertext);x
                switch (position) {
                    case 0:
                        textView.setText("Next");
                        System.out.println("ONE");
                    case 1:
                        textView.setText("Next");
                        System.out.println("TWO");
                    case 2:
                        textView.setText("Submit");
                        System.out.println("THREE");
                    default:
                        textView.setText("Next");
                        System.out.println("DEFAULT");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        firstFrag = new FirstFragment();
        secondFrag = new SecondFragment();
        thirdFrag = new ThirdFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelCreatePoll(View v) {
        //finish();
        Intent i = new Intent(CreatePollActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        final TextView textView = (TextView) findViewById(R.id.createpollfootertext);
        switch (position) {
            case 0:
                textView.setText("Next");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*question = (EditText) findViewById(R.id.question);
                        if (question.getText().toString().trim().length() == 0) {
                            Context context = getApplicationContext();
                            CharSequence text = "You must input a question";
                            validQuestion = false;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else {
                            validQuestion = true;
                            poll.remove("question");
                            poll.put("question", question.getText().toString());
                            pager.setCurrentItem(1);
                        }*/
                        pager.setCurrentItem(1);
                    }
                });
                break;
            case 1:
                textView.setText("Next");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        poll.put("optionNum", numOptions);
                        poll.remove("options");
                        /*for (int i = 0; i < 10; i++) { // old adding to poll
                            if (visiblePollOptions[i] == true) {
                                int actualOptionID = i + 1;
                                String option = "option" + actualOptionID;
                                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                                EditText optionText = (EditText)findViewById(optionID);
                                poll.add("options", optionText.getText().toString());
                                poll.add("votes", 0);
                            }

                        }*/
                        for (int i = 0; i < numOptions; i++) {
                            int actualOptionID = i + 1;
                            String option = "option" + actualOptionID;
                            int optionID = getResources().getIdentifier(option, "id", getPackageName());
                            EditText optionText = (EditText) findViewById(optionID);
                            if (optionText.getText().toString().trim().length() == 0) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please make sure you have added at least two options";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                totalOptionsAdded = 0;
                                break;
                            }
                            else {
                                validOptions = true;
                                totalOptionsAdded++;
                            }
                        }
                        if (validOptions == true && totalOptionsAdded >= 2) {
                            for (int i = 0; i < numOptions; i++) {
                                int actualOptionID = i + 1;
                                String option = "option" + actualOptionID;
                                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                                EditText optionText = (EditText) findViewById(optionID);
                                poll.add("options", optionText.getText().toString());
                                poll.add("votes", 0);
                            }
                        }
                        /*switch (numOptions) {
                            case 2:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                break;
                            case 3:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                break;
                            case 4:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                break;
                            case 5:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                option5 = (EditText) findViewById(R.id.option5);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                poll.add("options", option5.getText().toString());
                                break;
                            case 6:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                option5 = (EditText) findViewById(R.id.option5);
                                option6 = (EditText) findViewById(R.id.option6);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                poll.add("options", option5.getText().toString());
                                poll.add("options", option6.getText().toString());
                                break;
                            case 7:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                option5 = (EditText) findViewById(R.id.option5);
                                option6 = (EditText) findViewById(R.id.option6);
                                option7 = (EditText) findViewById(R.id.option7);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                poll.add("options", option5.getText().toString());
                                poll.add("options", option6.getText().toString());
                                poll.add("options", option7.getText().toString());
                                break;
                            case 8:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                option5 = (EditText) findViewById(R.id.option5);
                                option6 = (EditText) findViewById(R.id.option6);
                                option7 = (EditText) findViewById(R.id.option7);
                                option8 = (EditText) findViewById(R.id.option8);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                poll.add("options", option5.getText().toString());
                                poll.add("options", option6.getText().toString());
                                poll.add("options", option7.getText().toString());
                                poll.add("options", option8.getText().toString());
                                break;
                            case 9:
                                option1 = (EditText) findViewById(R.id.option1);
                                option2 = (EditText) findViewById(R.id.option2);
                                option3 = (EditText) findViewById(R.id.option3);
                                option4 = (EditText) findViewById(R.id.option4);
                                option5 = (EditText) findViewById(R.id.option5);
                                option6 = (EditText) findViewById(R.id.option6);
                                option7 = (EditText) findViewById(R.id.option7);
                                option8 = (EditText) findViewById(R.id.option8);
                                option9 = (EditText) findViewById(R.id.option9);
                                poll.add("options", option1.getText().toString());
                                poll.add("options", option2.getText().toString());
                                poll.add("options", option3.getText().toString());
                                poll.add("options", option4.getText().toString());
                                poll.add("options", option5.getText().toString());
                                poll.add("options", option6.getText().toString());
                                poll.add("options", option7.getText().toString());
                                poll.add("options", option8.getText().toString());
                                poll.add("options", option9.getText().toString());
                                break;
                        }
                        for (int i = 0; i < numOptions; i ++ )
                            poll.add("votes", 0);*/
                        pager.setCurrentItem(2);
                    }
                });
                break;
            case 2:
                textView.setText("Submit");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (validQuestion == false) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please make sure you have added a question";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else*/
                        poll.put("optionNum", numOptions);
                        poll.remove("options");
                        for (int i = 0; i < numOptions; i++) {
                            int actualOptionID = i + 1;
                            String option = "option" + actualOptionID;
                            int optionID = getResources().getIdentifier(option, "id", getPackageName());
                            EditText optionText = (EditText) findViewById(optionID);
                            if (optionText.getText().toString().trim().length() == 0) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please make sure you have added at least two options";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                totalOptionsAdded = 0;
                                break;
                            }
                            else {
                                validOptions = true;
                                totalOptionsAdded++;
                            }
                        }
                        if (validOptions == true && totalOptionsAdded >= 2) {
                            for (int i = 0; i < numOptions; i++) {
                                int actualOptionID = i + 1;
                                String option = "option" + actualOptionID;
                                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                                EditText optionText = (EditText) findViewById(optionID);
                                poll.add("options", optionText.getText().toString());
                                poll.add("votes", 0);
                            }
                        }

                        if (totalOptionsAdded < 2) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please make sure you have added at least two options";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else if (category == -1) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please make sure you have selected a category";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else {
                            //Toast.makeText(CreatePollActivity.this, firstFrag.getQuestion(), Toast.LENGTH_SHORT).show();
                            textView.setClickable(false);
                            textView.setBackgroundColor(Color.parseColor("#11100000"));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("poll_id");
                            query.getInBackground("ioqSxO1iQY", new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        // object will be poll_id
                                        if ( firstFrag.getQuestion().trim().length() == 0 ) {
                                            Toast.makeText(CreatePollActivity.this, "Please make sure you have added at least two options", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        int pollID = object.getInt("value");
                                        object.increment("value");
                                        object.saveInBackground();
                                        poll.put("category", category);
                                        poll.put("question", firstFrag.getQuestion() );
                                        poll.put("author", ParseUser.getCurrentUser().getString("username"));
                                        poll.put("id", pollID + 1);
                                        poll.put("createTime", System.currentTimeMillis());
                                        poll.put("total", 0);
                                        poll.put("nickname", ParseUser.getCurrentUser().getString("name"));
                                        poll.put("lastUpdate", System.currentTimeMillis());
                                        poll.saveInBackground();
                                    } else {
                                        // something went wrong
                                    }
                                }
                            });
                            Context context = getApplicationContext();
                            CharSequence text = "New Poll Submitted";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            finish();
                        }
                    }
                });
                break;
            default:
                textView.setText("Next");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0:
                    return firstFrag;
                case 1:
                    return secondFrag;
                case 2:
                    return thirdFrag;
                default:
                    return firstFrag;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class FirstFragment extends Fragment {
        TextView question;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_poll_1, container, false);
/*            TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
            tv.setText(getArguments().getString("msg"));*/
            question = (TextView) v.findViewById(R.id.question);
            return v;
        }

        public FirstFragment() {

/*            FirstFragment f = new FirstFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }

        public String getQuestion() {
            return question.getText().toString();
        }
    }

    public static class SecondFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_poll_2, container, false);

/*            TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
            tv.setText(getArguments().getString("msg"));*/

            return v;
        }

        public SecondFragment() {

/*            SecondFragment f = new SecondFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }

    }

    public static class ThirdFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //View v = inflater.inflate(R.layout.create_poll_3, container, false);

/*            TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
            tv.setText(getArguments().getString("msg"));*/

            //return v;
            View rootView = inflater.inflate(R.layout.create_poll_3, container, false);
            GridView grid = (GridView) rootView.findViewById(R.id.poll_categories_grid);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float px10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
            grid.setColumnWidth((int)((displayMetrics.widthPixels - px10 * 3) / 3));
            grid.setAdapter(new ImageAdapter(getActivity()));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // redirect to results activity
                    Toast.makeText(getActivity().getBaseContext(), Integer.toString((int) view.getTag()),
                            Toast.LENGTH_SHORT).show();
                    category = (int) view.getTag();
                }
            });
            return rootView;
        }
        public class ImageAdapter extends BaseAdapter {

            private Context c;
            private int[] thumbnails = {
                    R.drawable.category_all, R.drawable.category_auto, R.drawable.category_edu, R.drawable.category_enter,
                    R.drawable.category_fash, R.drawable.category_finance, R.drawable.category_food, R.drawable.category_games,
                    R.drawable.category_it, R.drawable.category_pets, R.drawable.category_sci, R.drawable.category_social,
                    R.drawable.category_sports, R.drawable.category_tech, R.drawable.category_travel
            };
            private int[] strings = {
                    R.string.category_all, R.string.category_auto, R.string.category_education, R.string.category_entertainment,
                    R.string.category_fashion, R.string.category_finance, R.string.category_food, R.string.category_games, R.string.category_it,
                    R.string.category_pet, R.string.category_science, R.string.category_social, R.string.category_sports, R.string.category_tech,
                    R.string.category_travel
            };
            private int[] bg_color = {
                    0xff42baff, 0xffff6259, 0xff835bd4, 0xffab48cf, 0xffced93b, 0xffff8554, 0xffffe53d, 0xffababab, 0xff6dcf71, 0xff997368,
                    0xff22b3a2, 0xff24dbf0, 0xffff4284, 0xffffa321, 0xff809dab
            };


            public ImageAdapter(Context c) {
                this.c = c;
            }

            @Override
            public int getCount() {
                return strings.length;
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
                    LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = vi.inflate(R.layout.categories, null);
                }
                ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
                TextView tv = (TextView) convertView.findViewById(R.id.category_text);
                ImageView bg = (ImageView) convertView.findViewById(R.id.imageViewBg);
                tv.setText(c.getResources().getString(strings[position]));
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                iv.setImageResource(thumbnails[position]);
                bg.setBackgroundColor(bg_color[position]);
                iv.setAdjustViewBounds(true);
                convertView.setTag(position);
                return convertView;
            }
        }
        public ThirdFragment() {

/*            ThirdFragment f = new ThirdFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }
    }

    public void addOption(View v) {
        if (numOptions >= 10) {
            Toast.makeText(getApplicationContext(), "Reached maximum of 10 options", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < 10; i++) {
            if (visiblePollOptions[i] == false) {
                visiblePollOptions[i] = true;
                numOptions++;
                int actualOptionID = i + 1;
                String option = "option" + actualOptionID;
                String optionR = "option" + actualOptionID + "remove";
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
                EditText editText = (EditText)findViewById(optionID);
                ImageButton imageButton = (ImageButton)findViewById(optionRID);
                editText.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void removeOption(View v) {
        int optionNum = 0;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[2] = false;
        editText.setText("");
    }


    public void uploadImage(View v) {

        Intent i = new Intent(this, UploadImage.class);
        startActivityForResult(i, 1);/*
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Bundle extras = getIntent().getExtras();
        //byte[] b = extras.getByteArray("uploadedImage");
        String stredittext=data.getStringExtra("edittextvalue");
        Toast.makeText(getApplicationContext(), stredittext, Toast.LENGTH_LONG).show();


    }

    public void removeOption3(View v) {
        //System.out.println("REMOVING OPTION 3");
        /*int optionNum = 3;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[2] = false;
        editText.setText("");*/
        int optionNum = 3;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 3; i < 10; i++) {
//            if (numOptions == 9) {

  //          }
             if (visiblePollOptions[i] == true) {
                 //System.out.println("VISIBLE POLL OPTION IS TRUE");
                 int optionToShift = i + 1; // option ids from xml 1-10 range
                 int previousOptionNum = i; // option ids from xml 1-10 range
                 String option = "option" + previousOptionNum;
                 int optionID = getResources().getIdentifier(option, "id", getPackageName());
                 EditText editText = (EditText) findViewById(optionID);
                 String optionShift = "option" + optionToShift;
                 String optionRShift = "option" + optionToShift + "remove";
                 int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                 int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                 EditText editTextShift = (EditText) findViewById(optionIDShift);
                 editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                 //if (i < 9) {
                 if (i == 9) {
                     ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                     editTextShift.setVisibility(View.GONE);
                     imageButtonShift.setVisibility(View.GONE);
                     visiblePollOptions[i] = false;
                     editTextShift.setText("");
                     shifted = true;
                     break;
                 }
                 else {
                     if (visiblePollOptions[i + 1] == false) {
                         ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                         editTextShift.setVisibility(View.GONE);
                         imageButtonShift.setVisibility(View.GONE);
                         visiblePollOptions[i] = false;
                         editTextShift.setText("");
                         shifted = true;
                         break;
                     }
                 }
                 //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption4(View v) {
        /*int optionNum = 4;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[3] = false;
        editText.setText("");*/
        int optionNum = 4;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 4; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption5(View v) {
        /*int optionNum = 5;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[4] = false;
        editText.setText("");*/
        int optionNum = 5;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 5; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption6(View v) {
        /*int optionNum = 6;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[5] = false;
        editText.setText("");*/
        int optionNum = 6;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 6; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption7(View v) {
        /*int optionNum = 7;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[6] = false;
        editText.setText("");*/
        int optionNum = 7;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 7; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption8(View v) {
        /*int optionNum = 8;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[7] = false;
        editText.setText("");*/
        int optionNum = 8;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 8; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption9(View v) {
        /*int optionNum = 9;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[8] = false;
        editText.setText("");*/
        int optionNum = 9;
        numOptions--;
        boolean shifted = false;
        //boolean needShifting = false;
        //ImageButton imageButton = (ImageButton)findViewById(optionRID);
        /*for (int i = 3; i < 10; i++) {
            if (visiblePollOptions[i] == true) {
                needShifting = true;
            }
        }*/
        for (int i = 9; i < 10; i++) {
//            if (numOptions == 9) {

            //          }
            if (visiblePollOptions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int optionToShift = i + 1; // option ids from xml 1-10 range
                int previousOptionNum = i; // option ids from xml 1-10 range
                String option = "option" + previousOptionNum;
                int optionID = getResources().getIdentifier(option, "id", getPackageName());
                EditText editText = (EditText) findViewById(optionID);
                String optionShift = "option" + optionToShift;
                String optionRShift = "option" + optionToShift + "remove";
                int optionIDShift = getResources().getIdentifier(optionShift, "id", getPackageName());
                int optionRIDShift = getResources().getIdentifier(optionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(optionIDShift);
                editText.setText(editTextShift.getText().toString());
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visiblePollOptions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
                    if (visiblePollOptions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(optionRIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visiblePollOptions[i] = false;
                        editTextShift.setText("");
                        shifted = true;
                        break;
                    }
                }
                //}
                 /*else if (i == 9) {
                     if (visiblePollOptions)

                 }*/
            }
        }
        if (shifted == false/* && needShifting == false*/) {
            //System.out.println("3 IS THE LAST OPTION");
            /*if (shifted == true) {
                break;
            }*/
            //else {
            String option = "option" + optionNum;
            String optionR = "option" + optionNum + "remove";
            int optionID = getResources().getIdentifier(option, "id", getPackageName());
            int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(optionID);
            ImageButton imageButton = (ImageButton) findViewById(optionRID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visiblePollOptions[optionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }
    public void removeOption10(View v) {
        int optionNum = 10;
        String option = "option" + optionNum;
        String optionR = "option" + optionNum + "remove";
        int optionID = getResources().getIdentifier(option, "id", getPackageName());
        int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
        EditText editText = (EditText)findViewById(optionID);
        ImageButton imageButton = (ImageButton)findViewById(optionRID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        numOptions--;
        visiblePollOptions[9] = false;
        editText.setText("");
    }
}
