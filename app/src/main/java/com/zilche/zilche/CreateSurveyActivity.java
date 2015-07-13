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

public class CreateSurveyActivity extends FragmentActivity implements OnPageChangeListener {

    FirstFragment firstFrag;
    SecondFragment secondFrag;
    ParseObject survey;
    ViewPager pager;
/*
    EditText question;
    EditText question1;
    EditText question2;
    EditText question3;
    EditText question4;
    EditText question5;
    EditText question6;
    EditText question7;
    EditText question8;
    EditText question9;
    EditText question10;*/
    EditText[] questions = new EditText[10];
    int numQuestions = 2;
    boolean[] visibleSurveyQuestions = {true, true, false, false, false, false, false, false ,false, false};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        onPageSelected(0);

        survey = new ParseObject("Survey");


        firstFrag = new FirstFragment();
        secondFrag = new SecondFragment();
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
                default:
                    return firstFrag;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    public static class FirstFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_survey_1, container, false);
/*            TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
            tv.setText(getArguments().getString("msg"));*/

            return v;
        }

        public FirstFragment() {

/*            FirstFragment f = new FirstFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;*/
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
                EditText editText = (EditText)findViewById(questionID);
                editText.setVisibility(View.VISIBLE);
                ImageButton imageButton = (ImageButton)findViewById(questionIID);
                imageButton.setVisibility(View.VISIBLE);
                imageButton = (ImageButton)findViewById(questionOID);
                imageButton.setVisibility(View.VISIBLE);
                imageButton = (ImageButton)findViewById(questionRID);
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
        EditText editText = (EditText)findViewById(questionID);
        editText.setVisibility(View.GONE);
        ImageButton imageButton = (ImageButton)findViewById(questionIID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton)findViewById(questionOID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton)findViewById(questionRID);
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

                if (i == 9) {
                    ImageButton imageButtonShift = (ImageButton) findViewById(questionRIDShift);
                    editTextShift.setVisibility(View.GONE);
                    imageButtonShift.setVisibility(View.GONE);
                    visibleSurveyQuestions[i] = false;
                    editTextShift.setText("");
                    shifted = true;
                    break;
                }
                else {
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
                }
                else {
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
                }
                else {
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
                }
                else {
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
                }
                else {
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
                }
                else {
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
                }
                else {
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
        EditText editText = (EditText)findViewById(questionID);
        ImageButton imageButton = (ImageButton)findViewById(questionIID);
        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton)findViewById(questionOID);
        imageButton.setVisibility(View.GONE);
        imageButton = (ImageButton)findViewById(questionRID);
        imageButton.setVisibility(View.GONE);
        numQuestions--;
        visibleSurveyQuestions[9] = false;
        editText.setText("");
    }
}









