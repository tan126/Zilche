package com.zilche.zilche;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

/**
 * Created by khe on 6/25/2015.
 */
public class signupActivity extends Activity {

    Button signup;
    String usernametxt;
    String passwordtxt;
    String emailtxt;
    EditText username;
    EditText password;
    EditText email;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById((R.id.password));
        email = (EditText)findViewById(R.id.email);

        signup = (Button)findViewById(R.id.signup);

        signup.setOnClickListener(new OnClickListener() {


            public void onClick(View arg0) {
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                emailtxt = email.getText().toString();

                if (usernametxt.equals("") && passwordtxt.equals("") && emailtxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.setEmail(emailtxt);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });


    }









}

