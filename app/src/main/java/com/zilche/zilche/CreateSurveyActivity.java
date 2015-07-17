package com.zilche.zilche;

import java.util.Arrays;
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

public class CreateSurveyActivity extends FragmentActivity implements OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ThirdFragment thirdFrag;
    ParseObject survey;
    ViewPager pager;


    //String[] questions = new String[10];
/*
    String[] options1 = new String[10];
    String[] options2 = new String[10];
    String[] options3 = new String[10];
    String[] options4 = new String[10];
    String[] options5 = new String[10];
    String[] options6 = new String[10];
    String[] options7 = new String[10];
    String[] options8 = new String[10];
    String[] options9 = new String[10];
    String[] options10 = new String[10];*/


    int totalQuestionsAdded = 0;
    boolean validQuestions = false;
    String[][] questionOptions = new String[10][10];

    //String[][] options = new String[][]{options1, options2, options3, options4, options5, options6, options7, options8, options9, options10};


    int numQuestions = 2;
    boolean[] visibleSurveyQuestions = {true, true, false, false, false, false, false, false, false, false};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        onPageSelected(0);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                questionOptions[i][j] = ""; // initialize to all blank Strings
            }
        }

        survey = new ParseObject("Survey");


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

    public void cancelCreateSurvey(View v) {
        //finish();
        Intent i = new Intent(CreateSurveyActivity.this, MainActivity.class);
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
        final TextView textView = (TextView) findViewById(R.id.createsurveyfootertext);
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
            case 1: // storing the questions of the survey
                textView.setText("Next");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        survey.put("questionNum", numQuestions);
                        survey.remove("question");
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
                        for (int i = 0; i < numQuestions; i++) {
                            int actualQuestionID = i + 1;
                            String question = "question" + actualQuestionID;
                            int questionID = getResources().getIdentifier(question, "id", getPackageName());
                            EditText questionText = (EditText) findViewById(questionID);
                            if (questionText.getText().toString().trim().length() == 0) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please make sure you have added at least two questions";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                totalQuestionsAdded = 0;
                                break;
                            } else {
                                validQuestions = true;
                                totalQuestionsAdded++;
                            }
                        }
                        if (validQuestions == true && totalQuestionsAdded >= 2) {
                            for (int i = 0; i < numQuestions; i++) {
                                int actualQuestionID = i + 1;
                                String question = "question" + actualQuestionID;
                                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                                EditText questionText = (EditText) findViewById(questionID);
                                survey.add("questions", questionText.getText().toString());
                                //survey.add("votes", 0);
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
                        if (totalQuestionsAdded < 2) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please make sure you have added at least two questions";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            //Toast.makeText(CreatePollActivity.this, firstFrag.getQuestion(), Toast.LENGTH_SHORT).show();
                            textView.setClickable(false);
                            textView.setBackgroundColor(Color.parseColor("#11100000"));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("survey_id");
                            query.getInBackground("KpTUEunDWx", new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        // object will be poll_id
                                        if (firstFrag.getTitle().trim().length() == 0) {
                                            Toast.makeText(CreateSurveyActivity.this, "Please make sure you have added at least two questions", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        int surveyID = object.getInt("value");
                                        object.increment("value");
                                        object.saveInBackground();
                                        for (int i = 1; i < 11; i++) {
                                            String columnOption = "question" + i + "options";
                                            survey.put(columnOption, Arrays.asList(questionOptions[i - 1]));
                                        }
                                        survey.put("title", firstFrag.getTitle());
                                        survey.put("author", ParseUser.getCurrentUser().getString("username"));
                                        survey.put("id", surveyID + 1);
                                        survey.put("category", 0);
                                        survey.put("createTime", System.currentTimeMillis());
                                        survey.put("total", 0);
                                        survey.put("nickname", ParseUser.getCurrentUser().getString("name"));
                                        survey.put("lastUpdate", System.currentTimeMillis());
                                        survey.saveInBackground();
                                    } else {
                                        // something went wrong
                                    }
                                }
                            });
                            Context context = getApplicationContext();
                            CharSequence text = "New Survey Submitted";
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
            switch (pos) {
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
        TextView title;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_survey_1, container, false);
/*            TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
            tv.setText(getArguments().getString("msg"));*/
            title = (TextView) v.findViewById(R.id.title);

            return v;
        }

        public FirstFragment() {

/*            FirstFragment f = new FirstFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
        }

        public String getTitle() {
            return title.getText().toString();
        }

    }

    public static class SecondFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_survey_2, container, false);

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


    public void addQuestion(View v) {
        if (numQuestions >= 10) {
            Toast.makeText(getApplicationContext(), "Reached maximum of 10 questions", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < 10; i++) {
            if (visibleSurveyQuestions[i] == false) {
                visibleSurveyQuestions[i] = true;
                numQuestions++;
                int actualQuestionID = i + 1;
                String question = "question" + actualQuestionID;
                String questionI = "question" + actualQuestionID + "image";
                String questionO = "question" + actualQuestionID + "options";
                String questionR = "question" + actualQuestionID + "remove";
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
                int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
                int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                editText.setVisibility(View.VISIBLE);
                ImageButton imageButton = (ImageButton) findViewById(questionIID);
                imageButton.setVisibility(View.VISIBLE);
                imageButton = (ImageButton) findViewById(questionOID);
                imageButton.setVisibility(View.VISIBLE);
                imageButton = (ImageButton) findViewById(questionRID);
                imageButton.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void removeQuestion(View v) {
        int questionNum = 0;
        String question = "question" + questionNum;
        String questionI = "question" + questionNum + "image";
        String questionO = "question" + questionNum + "options";
        String questionR = "question" + questionNum + "remove";
        int questionID = getResources().getIdentifier(question, "id", getPackageName());
        int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
        int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
        int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
        EditText editText = (EditText) findViewById(questionID);
        editText.setVisibility(View.GONE);
        ImageButton imageButton = (ImageButton) findViewById(questionIID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton) findViewById(questionOID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton) findViewById(questionRID);
        imageButton.setVisibility(View.GONE);
        questionNum--;
        visibleSurveyQuestions[2] = false;
        editText.setText("");
    }


    public void removeQuestion3(View v) {
        int questionNum = 3;
        numQuestions--;
        boolean shifted = false;

        for (int i = 3; i < 10; i++) {

            if (visibleSurveyQuestions[i] == true) {
                int questionToShift = i + 1; // question ids from xml 1-10 range
                int previousQuestionNum = i; // question ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                //System.out.println("test: " + Integer.toString(questionToShift));

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);



                //questionToShift
                //questionOptions[i-1] = questionOptions[i];

                //Arrays.fill(questionOptions[i-1], questionOptions[i]);
                //Arrays.fill(questionOptions[i], null);
                //Arrays.fill( questionOptions[previousQuestionNum], null );


                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    //Arrays.fill(questionOptions[questionToShift], null);
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
                        editTextShift.setText("");
                        //Arrays.fill(questionOptions[questionToShift], null);
                        shifted = true;
                        break;
                    }
                }

            }
        }
        if (shifted == false) {

            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            editText.setVisibility(View.GONE);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);

            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");

        }

    }

    public void removeQuestion4(View v) {

        int questionNum = 4;
        numQuestions--;
        boolean shifted = false;

        for (int i = 4; i < 10; i++) {

            if (visibleSurveyQuestions[i] == true) {

                int questionToShift = i + 1; // question ids from xml 1-10 range
                int previousQuestionNum = i; // question ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());


                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);

                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion5(View v) {

        int questionNum = 5;
        numQuestions--;
        boolean shifted = false;

        for (int i = 5; i < 10; i++) {

            if (visibleSurveyQuestions[i] == true) {
                //System.out.println("VISIBLE POLL question IS TRUE");
                int questionToShift = i + 1; // question ids from xml 1-10 range
                int previousQuestionNum = i; // option ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion6(View v) {

        int questionNum = 6;
        numQuestions--;
        boolean shifted = false;

        for (int i = 6; i < 10; i++) {

            if (visibleSurveyQuestions[i] == true) {
                //System.out.println("VISIBLE POLL QUESTION IS TRUE");
                int questionToShift = i + 1; // question ids from xml 1-10 range
                int previousQuestionNum = i; // question ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion7(View v) {

        int questionNum = 7;
        numQuestions--;
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
            if (visibleSurveyQuestions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int questionToShift = i + 1; // question ids from xml 1-10 range
                int previousQuestionNum = i; // question ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion8(View v) {
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
        int questionNum = 8;
        numQuestions--;
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
            if (visibleSurveyQuestions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int questionToShift = i + 1; // option ids from xml 1-10 range
                int previousQuestionNum = i; // option ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion9(View v) {
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
        int questionNum = 9;
        numQuestions--;
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
            if (visibleSurveyQuestions[i] == true) {
                //System.out.println("VISIBLE POLL OPTION IS TRUE");
                int questionToShift = i + 1; // option ids from xml 1-10 range
                int previousQuestionNum = i; // option ids from xml 1-10 range
                String question = "question" + previousQuestionNum;
                int questionID = getResources().getIdentifier(question, "id", getPackageName());
                EditText editText = (EditText) findViewById(questionID);
                String questionShift = "question" + questionToShift;
                String questionIShift = "question" + questionToShift + "image";
                String questionOShift = "question" + questionToShift + "options";
                String questionRShift = "question" + questionToShift + "remove";
                int questionIDShift = getResources().getIdentifier(questionShift, "id", getPackageName());
                int questionIIDShift = getResources().getIdentifier(questionIShift, "id", getPackageName());
                int questionOIDShift = getResources().getIdentifier(questionOShift, "id", getPackageName());
                int questionRIDShift = getResources().getIdentifier(questionRShift, "id", getPackageName());
                EditText editTextShift = (EditText) findViewById(questionIDShift);
                editText.setText(editTextShift.getText().toString());

                for (int j = 0; j < 10; j++)
                    questionOptions[previousQuestionNum -1 ][j] = questionOptions[previousQuestionNum][j];
                Arrays.fill(questionOptions[previousQuestionNum], null);
                 /*if (i == 9) {
                     shifted = true;
                 }*/
                //if (i < 9) {
                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                } else {
                    if (visibleSurveyQuestions[i + 1] == false) {
                        ImageButton imageButtonShift = (ImageButton) findViewById(questionIIDShift);
                        editTextShift.setVisibility(View.GONE);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionOIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                        imageButtonShift.setVisibility(View.GONE);
                        visibleSurveyQuestions[i] = false;
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
            String question = "question" + questionNum;
            String questionI = "question" + questionNum + "image";
            String questionO = "question" + questionNum + "options";
            String questionR = "question" + questionNum + "remove";
            int questionID = getResources().getIdentifier(question, "id", getPackageName());
            int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
            int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
            int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
            EditText editText = (EditText) findViewById(questionID);
            ImageButton imageButton = (ImageButton) findViewById(questionIID);
            editText.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionOID);
            imageButton.setVisibility(View.GONE);
            imageButton = (ImageButton) findViewById(questionRID);
            imageButton.setVisibility(View.GONE);
            //numOptions--;
            visibleSurveyQuestions[questionNum - 1] = false;
            editText.setText("");
            //break;
            //}
        }
    }

    public void removeQuestion10(View v) {
        int questionNum = 10;
        String question = "question" + questionNum;
        String questionI = "question" + questionNum + "image";
        String questionO = "question" + questionNum + "options";
        String questionR = "question" + questionNum + "remove";
        int questionID = getResources().getIdentifier(question, "id", getPackageName());
        int questionIID = getResources().getIdentifier(questionI, "id", getPackageName());
        int questionOID = getResources().getIdentifier(questionO, "id", getPackageName());
        int questionRID = getResources().getIdentifier(questionR, "id", getPackageName());
        EditText editText = (EditText) findViewById(questionID);
        ImageButton imageButton = (ImageButton) findViewById(questionIID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton) findViewById(questionOID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton) findViewById(questionRID);
        imageButton.setVisibility(View.GONE);
        numQuestions--;
        visibleSurveyQuestions[9] = false;
        editText.setText("");
        Arrays.fill(questionOptions[9], null);
    }

    public void optionsQuestion1(View v) {
        //int questionNum = 1;


        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 1);
        intent.putExtra("optionsForQuestion", questionOptions[0]);

        int questionID = getResources().getIdentifier("question1", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        //intent.putExtra("question", "Question 1:");

        startActivityForResult(intent, 1);

    }

    public void optionsQuestion2(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 2);
        intent.putExtra("optionsForQuestion", questionOptions[1]);

        int questionID = getResources().getIdentifier("question2", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        //intent.putExtra("question", "Question 2:");

        startActivityForResult(intent, 2);
    }

    public void optionsQuestion3(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 3);
        intent.putExtra("optionsForQuestion", questionOptions[2]);

        int questionID = getResources().getIdentifier("question3", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        //intent.putExtra("question", "Question 3:");

        startActivityForResult(intent, 3);

    }

    public void optionsQuestion4(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 4);
        intent.putExtra("optionsForQuestion", questionOptions[3]);

        int questionID = getResources().getIdentifier("question4", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
         intent.putExtra("question", question);

        //intent.putExtra("question", "Question 4:");


        startActivityForResult(intent, 4);
    }

    public void optionsQuestion5(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 5);
        intent.putExtra("optionsForQuestion", questionOptions[4]);

        int questionID = getResources().getIdentifier("question5", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
         intent.putExtra("question", question);

        //intent.putExtra("question", "Question 5:");

        startActivityForResult(intent, 5);

    }

    public void optionsQuestion6(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 6);
        intent.putExtra("optionsForQuestion", questionOptions[5]);

        int questionID = getResources().getIdentifier("question6", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        startActivityForResult(intent, 6);

    }

    public void optionsQuestion7(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 7);
        intent.putExtra("optionsForQuestion", questionOptions[6]);

        int questionID = getResources().getIdentifier("question7", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        startActivityForResult(intent, 7);

    }

    public void optionsQuestion8(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 8);
        intent.putExtra("optionsForQuestion", questionOptions[7]);

        int questionID = getResources().getIdentifier("question8", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        startActivityForResult(intent, 8);

    }

    public void optionsQuestion9(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 9);
        intent.putExtra("optionsForQuestion", questionOptions[8]);

        int questionID = getResources().getIdentifier("question9", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        startActivityForResult(intent, 9);

    }

    public void optionsQuestion10(View v) {
        Intent intent = new Intent(CreateSurveyActivity.this, AddSurveyOptions.class);
        //intent.putExtra("questionNum", 10);
        intent.putExtra("optionsForQuestion", questionOptions[9]);

        int questionID = getResources().getIdentifier("question10", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(questionID);
        String question = inputTxt.getText().toString();
        intent.putExtra("question", question);

        startActivityForResult(intent, 10);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Bundle extras = getIntent().getExtras();
                questionOptions[0] = data.getStringArrayExtra("resultOptions");
                //System.out.println(options1[1]);
                //System.out.println(options1[2]);
                //System.out.println(options1[3]);
                //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {

                //Bundle extras = getIntent().getExtras();
                questionOptions[1] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {

                //Bundle extras = getIntent().getExtras();
                questionOptions[2] = data.getStringArrayExtra("resultOptions");
               // Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {

                //Bundle extras = getIntent().getExtras();
                questionOptions[3] = data.getStringArrayExtra("resultOptions");
               // Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {

                //Bundle extras = getIntent().getExtras();
                questionOptions[4] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "5", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 6) {
            if (resultCode == RESULT_OK) {

                //Bundle extras = getIntent().getExtras();
                questionOptions[5] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "6", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {

                questionOptions[6] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "7", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 8) {
            if (resultCode == RESULT_OK) {

                questionOptions[7] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "8", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 9) {
            if (resultCode == RESULT_OK) {

                questionOptions[8] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "9", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                questionOptions[9] = data.getStringArrayExtra("resultOptions");
                //Toast.makeText(getApplicationContext(), "10", Toast.LENGTH_SHORT).show();
            }
        }

    }
}









