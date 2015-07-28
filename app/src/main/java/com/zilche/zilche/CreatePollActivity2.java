package com.zilche.zilche;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Handler;


public class CreatePollActivity2 extends AppCompatActivity {

    private TextView title;
    private ViewPager vp;
    private boolean exit = false;
    private FirstFragment frag1 = new FirstFragment();
    private SecondFragment frag2 = new SecondFragment();
    private ThirdFragment frag3 = new ThirdFragment();

    public void back_clicked(View v) {
        exit = true;
        onBackPressed();
    }

    public ParseObject makeObject() {
        ParseObject poll = new ParseObject("poll");
        poll.put("category", frag3.getCategory());
        String question = frag1.getQuestion();
        if (question == null) {
            vp.setCurrentItem(0);
            return null;
        }
        poll.put("question", question);
        poll.put("author", ParseUser.getCurrentUser().getString("username"));
        poll.put("createTime", System.currentTimeMillis());
        poll.put("total", 0);
        poll.put("nickname", ParseUser.getCurrentUser().getString("name"));
        poll.put("lastUpdate", System.currentTimeMillis());
        int anon = frag1.getSwitch()? 1 : 0;
        poll.put("anon", anon);
        String[] options = frag2.getText();
        if (options == null) {
            vp.setCurrentItem(1);
            return null;
        }
        poll.put("optionNum", options.length);
        poll.put("options", Arrays.asList(options));
        for (int i = 0; i < options.length; i++) {
            poll.put("votes" + Integer.toString(i), 0);
        }
        return poll;
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            return;
        }
        switch (vp.getCurrentItem()) {
            case 0:
                super.onBackPressed();
                break;
            case 1:
                vp.setCurrentItem(0);
                break;
            case 2:
                vp.setCurrentItem(1);
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll_activity2);

        title = (TextView) findViewById(R.id. title_create_poll);
        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.pager_tab_strip);
        vp = (ViewPager) findViewById(R.id.viewpager_createpoll);
        stl.setCustomTabView(R.layout.create_poll_footer, 0);
        stl.setDistributeEvenly(true);
        stl.setSelectedIndicatorColors(0xffff5722);
        vp.setOffscreenPageLimit(3);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(vp.getWindowToken(), 0);
                switch (position) {
                    case 0:
                        title.setText("New Poll");
                        break;
                    case 1:
                        title.setText("Options");
                        break;
                    case 2:
                        title.setText("Categories");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setAdapter(new CustomAdapter(getSupportFragmentManager()));
        vp.setPageTransformer(true, new DepthPageTransformer());
        stl.setViewPager(vp);
    }


    private class CustomAdapter extends FragmentPagerAdapter {

        public CustomAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return frag1;
            if (position == 1)
                return frag2;
            if (position == 2)
                return frag3;
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class FirstFragment extends Fragment {

        private TextView question;
        private SwitchCompat switchBtn;
        private TextView wordCount;
        private FloatingActionButton fab;

        public String getQuestion() {
            String ret;
            ret = question.getText().toString().trim();
            if (ret.length() == 0) {
                question.requestFocus();
                Toast.makeText(getActivity(), "Question should not be empty", Toast.LENGTH_SHORT).show();
                return null;
            }
            return ret;
        }

        public boolean getSwitch() {
            return switchBtn.isChecked();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.create_poll_new1, container, false);
            question = (TextView) rootView.findViewById(R.id.question);
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo
                }
            });
            switchBtn = (SwitchCompat) rootView.findViewById(R.id.switch_btn);
            wordCount = (TextView) rootView.findViewById(R.id.text_count);
            question.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    wordCount.setText(Integer.toString(question.getText().length()) + " / 150");
                    if (question.getText().length() > 145) {
                        wordCount.setTextColor(0xffDD2C00);
                    } else {
                        wordCount.setTextColor(0xff333333);
                    }
                }
            });
            return rootView;
        }

    }

    public static class SecondFragment extends Fragment {

        private android.support.design.widget.FloatingActionButton fab;
        private LinearLayout optionsList;
        private LinkedList<LinearLayout> layouts = new LinkedList<LinearLayout>();
        private boolean[] checkers = new boolean[10];
        private View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layouts.size() == 1) {
                    if (layouts.get(0).getChildAt(0) instanceof EditText) {
                        EditText etext = (EditText)(layouts.get(0).getChildAt(0));
                        etext.setText("");
                        return;
                    }
                }
                int tag = (int) v.getTag();
                for (int i = 0; i < layouts.size(); i++) {
                    if ((int)(layouts.get(i).getTag()) == tag) {
                        layouts.remove(i);
                        checkers[tag] = false;
                        break;
                    }
                }
                for (int i = 0; i < optionsList.getChildCount(); i++) {
                    if ((int)(optionsList.getChildAt(i).getTag()) == tag) {
                        fab.setVisibility(View.GONE);
                        optionsList.removeViewAt(i);
                        AlphaAnimation anim1 = new AlphaAnimation(0, 1);
                        anim1.setDuration(1000);
                        anim1.setFillAfter(true);
                        fab.setAnimation(anim1);
                        fab.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.create_poll_new2, container, false);
            optionsList = (LinearLayout) rootView.findViewById(R.id.options_list);
            addOption();
            fab = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.fab2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layouts.size() >= 10) {
                        Toast.makeText(getActivity(), "You reached the maximum of 10 options", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fab.setVisibility(View.GONE);
                    addOption();
                    AlphaAnimation anim1 = new AlphaAnimation(0, 1);
                    anim1.setDuration(1000);
                    anim1.setFillAfter(true);
                    fab.setAnimation(anim1);
                    fab.setVisibility(View.VISIBLE);
                }
            });
            return rootView;
        }

        private void addOption() {
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setBackgroundColor(0xffffffff);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setLayoutParams(params);
            EditText et = (EditText) View.inflate(getActivity(), R.layout.edittext_material, null);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params2.setMargins(px, px, 2, 2);
            et.setLayoutParams(params2);
            et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
            et.setSingleLine(false);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params3.gravity = Gravity.CENTER_VERTICAL;
            ImageButton ib = new ImageButton(getActivity());
            ib.setImageResource(R.drawable.ic_clear_grey_18dp);
            ib.setBackgroundColor(0x00ffffff);
            ib.setLayoutParams(params3);
            ll.addView(et);
            ll.addView(ib);
            int tag = 0;
            for (int i = 0; i < 10; i++) {
                if (!checkers[i]) {
                    tag = i;
                    checkers[i] = true;
                    break;
                }
            }
            ll.setTag(tag);
            ib.setTag(tag);
            ib.setOnClickListener(onclick);
            layouts.add(ll);
            optionsList.addView(ll);
            et.requestFocus();
        }

        public String[] getText() {
            int len = layouts.size();
            String[] ret = new String[len];
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < layouts.get(i).getChildCount(); j++) {
                    if (layouts.get(i).getChildAt(j) instanceof EditText) {
                        EditText et = (EditText) layouts.get(i).getChildAt(0);
                        ret[i] = et.getText().toString().trim();
                        if (ret[i].length() == 0) {
                            et.requestFocus();
                            Toast.makeText(getActivity(), "Option should not be empty", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        break;
                    }
                }
            }
            if (ret.length < 2) {
                Toast.makeText(getActivity(), "You should provide at least 2 options", Toast.LENGTH_SHORT).show();
                return null;
            }
            return ret;
        }

    }

    public static class ThirdFragment extends Fragment {

        private ListView lv;
        private FloatingActionButton fab;
        private int[] strings = {
                R.string.category_auto_2, R.string.category_education_2, R.string.category_entertainment_2,
                R.string.category_fashion_2, R.string.category_finance_2, R.string.category_food_2, R.string.category_games_2, R.string.category_it_2,
                R.string.other, R.string.category_pet_2, R.string.category_science_2, R.string.category_social_2, R.string.category_sports_2, R.string.category_tech_2,
                R.string.category_travel_2
        };

        private int[] drawable = {
                R.drawable.ic_directions_car_grey_24dp, R.drawable.ic_school_grey_24dp, R.drawable.ic_mic_grey_24dp,
                R.drawable.ic_watch_grey_24dp, R.drawable.ic_local_atm_grey_24dp, R.drawable.ic_restaurant_menu_grey_24dp,
                R.drawable.ic_gamepad_grey_24dp, R.drawable.ic_code_grey_24dp, R.drawable.ic_assessment_grey_24dp,
                R.drawable.category_pets_grey, R.drawable.ic_wb_incandescent_grey_24dp, R.drawable.ic_people_grey_24dp,
                R.drawable.ic_directions_bike_grey_24dp, R.drawable.ic_laptop_windows_grey_24dp, R.drawable.ic_flight_takeoff_grey_24dp
        };

        private int[] cat_pos = {
                1, 2, 3, 4, 5, 6, 7, 8, 0, 9, 10, 11, 12, 13, 14
        };

        public int getCategory() {
            return cat_pos[lv.getCheckedItemPosition()];
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        private Poll parsePollObject(ParseObject thisPoll){
            String id = thisPoll.getObjectId();
            String question = thisPoll.getString("question");
            int options_count = thisPoll.getInt("optionNum");
            JSONArray tmpOptions = thisPoll.getJSONArray("options");
            String[] options = new String[options_count];
            for( int i = 0; i < options_count; i ++ ) {
                try{
                    options[i] = tmpOptions.getString(i);
                } catch ( JSONException e ){
                    Log.d("JSON", "Array index out of bound");
                }
            }
            int[] votes = new int[options_count];
            for( int i = 0; i < options_count; i ++ ) {
                votes[i] = thisPoll.getInt("votes" + Integer.toString(i));
            }
            String tmp = "";
            long updatedTime = thisPoll.getLong("createTime");
            long diffMS = System.currentTimeMillis() - updatedTime;
            long diffS = diffMS / 1000;
            if ( diffS > 60 ) {
                long diffM = diffS / 60;
                if ( diffM > 60 ){
                    long diffH = diffM / 60;
                    if ( diffH > 24 ) {
                        long diffD = diffH / 24;
                        tmp +=  diffD + " days ago";
                    }
                    else
                        tmp +=  + diffH + " hours ago";;
                }
                else
                    tmp += + diffM + " minutes ago";
            }
            else
                tmp += " 1 minute ago";
            String date_added = tmp;
            String author = thisPoll.getString("nickname");
            int categorty = thisPoll.getInt("category");
            Poll newPoll = new Poll(id, question, options, votes, date_added, author, options_count, categorty);
            if (categorty ==  0) {
                categorty = 8;
            } else if (categorty < 9) {
                categorty--;
            }
            newPoll.setCategory_title(getString(strings[categorty]));
            newPoll.setAnon(thisPoll.getInt("anon"));
            return newPoll;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.create_poll_new3, container, false);
            lv = (ListView) rootView.findViewById(R.id.cat_list);
            fab = (FloatingActionButton) rootView.findViewById(R.id.submit_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatePollActivity2 activity = (CreatePollActivity2) getActivity();
                    final ParseObject parseObject = activity.makeObject();
                    if (parseObject == null) return;
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Poll poll = parsePollObject(parseObject);
                                Intent i = new Intent(getActivity(), PollViewActivity.class);
                                i.putExtra("poll", poll);
                                Toast.makeText(getActivity(), "Create poll successful.", Toast.LENGTH_SHORT).show();
                                startActivity(i);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), "Unsuccessful. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            lv.setAdapter(new ListAdapter());
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                }
            });
            lv.setItemChecked(8, true);
            return rootView;
        }

        private class ListAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return cat_pos.length;
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
                LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View root = convertView;
                if (convertView == null) {
                    root = inf.inflate(R.layout.listitems, parent, false);
                }
                TextView tv = (TextView) root.findViewById(R.id.text);
                tv.setText(getString(strings[position]));
                ImageView iv = (ImageView) root.findViewById(R.id.image);
                iv.setImageResource(drawable[position]);
                return root;
            }
        }

    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.95f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}


