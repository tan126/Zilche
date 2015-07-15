package com.zilche.zilche;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class AddSurveyOptions extends ActionBarActivity {

    int numOptions = 2;
    boolean[] visiblePollOptions = {true, true, false, false, false, false, false, false ,false, false};
    //int questionNum;
    String[] options = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_survey_options);

        Bundle extras = getIntent().getExtras();

        TextView newtext = (TextView) findViewById(R.id.optionsheader);
        String question = extras.getString("question");
        if(!question.equals(""))
            newtext.setText(question);


        //questionNum = extras.getInt("questionNum");
        //String key = "optionsForQuestion" + Integer.toString(questionNum);
        //System.out.println(key);
        options = getIntent().getStringArrayExtra("optionsForQuestion");

        for(int i = 0; i < 10; i++){
            if(options[i] != null){
                String optionIDName = "option" + Integer.toString(i+1);
                int questionID = getResources().getIdentifier(optionIDName, "id", getPackageName());
                TextView writtenOption = (TextView) findViewById(questionID);
                writtenOption.setText(options[i]);
                //numQuestions++;
                if(i > 1) {
                    if (options[i].isEmpty()) {
                        break;
                    }
                    visiblePollOptions[i] = true;
                    int actualQuestionID = i + 1;
                    String option = "option" + actualQuestionID;
                    String optionR = "option" + actualQuestionID + "remove";
                    int optionID = getResources().getIdentifier(option, "id", getPackageName());
                    int optionRID = getResources().getIdentifier(optionR, "id", getPackageName());
                    EditText editText = (EditText) findViewById(optionID);
                    editText.setVisibility(View.VISIBLE);
                    ImageButton imageButton = (ImageButton) findViewById(optionRID);
                    imageButton.setVisibility(View.VISIBLE);
                }


            }
            else
                break;

        }






    }

    public void finalize(View v){

/*
        int optionID = getResources().getIdentifier("option1", "id", getPackageName());
        EditText inputTxt = (EditText) findViewById(optionID);
        options[0] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option2", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[1] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option3", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[2] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option4", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[3] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option5", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[4] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option6", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[5] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option7", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[6] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option8", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[7] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option9", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[8] = inputTxt.getText().toString();

        optionID = getResources().getIdentifier("option10", "id", getPackageName());
        inputTxt = (EditText) findViewById(optionID);
        options[9] = inputTxt.getText().toString();
        */

        String optionname;
        int optionID;
        EditText inputTxt;

        for(int i = 0; i < numOptions; i++){
            optionname = "option" + Integer.toString(i+1);
            optionID = getResources().getIdentifier(optionname, "id", getPackageName());
            inputTxt = (EditText) findViewById(optionID);
            options[i] = inputTxt.getText().toString();
        }


        Intent result = new Intent();
        result.putExtra("resultOptions", options);
        setResult(Activity.RESULT_OK, result);
        finish();

    }

    public void cancelCreateSurveyOptions(View v){
        Intent result = new Intent();
        finish();
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
