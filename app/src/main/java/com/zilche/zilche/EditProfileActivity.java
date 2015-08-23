package com.zilche.zilche;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EditProfileActivity extends ActionBarActivity {

    private TextView email;
    private TextView user;
    private TextView birthday;
    private TextView intro;
    private TextView age;
    private TextView gender;
    private TextView country;
    private android.support.design.widget.FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        email = (TextView) findViewById(R.id.email_pi);
        user = (TextView) findViewById(R.id.name_pi);
        birthday = (TextView) findViewById(R.id.bday_pi);
        intro = (TextView) findViewById(R.id.intro_pi);
        age = (TextView) findViewById(R.id.age_pi);
        gender = (TextView) findViewById(R.id.gender_pi);
        country = (TextView) findViewById(R.id.country_pi);
        fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);
        ParseUser u = ParseUser.getCurrentUser();
        email.setText(u.getEmail());
        user.setText(u.getString("name"));
        if (u.getString("message") != null) {
            intro.setText(u.getString("message"));
        } else {
            intro.setText("unspecified");
        }
        if (u.getString("gender") != null) {
            gender.setText(u.getString("gender"));
        } else {
            gender.setText("unspecified");
        }
        if (u.getString("country") != null) {
            country.setText(u.getString("country"));
        } else {
            country.setText("unspecified");
        }
        if (u.getDate("birthday") != null) {
            Date d = u.getDate("birthday");
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int age_val = Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
            if (age_val < 1) age_val = 0;
            age.setText(Integer.toString(age_val));
            DateFormat df = new SimpleDateFormat("M-dd-yyyy");
            birthday.setText(df.format(d));
        } else {
            birthday.setText("unspecified");
            age.setText("unspecified");
        }
    }

    public void backButton(View v) {
        onBackPressed();
    }

    public void upload(View v) {

    }

    public void edit_name(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");
        final EditText et = (EditText) View.inflate(this, R.layout.edittext_material2, null);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setText(user.getText().toString());
        et.setSelection(user.getText().toString().length());
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                getResources().getDisplayMetrics());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = et.getText().toString().trim();
                if (text.length() < 2) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.fl_name), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    user.setText(text);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = builder.create();
        ad.setView(et, pad, pad, pad, pad);
        ad.show();
    }

    public void edit_bday(View v) {

    }

    public void edit_intro(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit intro");
        final EditText et = (EditText) View.inflate(this, R.layout.edittext_material2, null);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        if (ParseUser.getCurrentUser().getString("message") != null) {
            et.setText(intro.getText().toString());
            et.setSelection(intro.getText().toString().length());
        }
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                getResources().getDisplayMetrics());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = et.getText().toString().trim();
                intro.setText(text);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = builder.create();
        ad.setView(et, pad, pad, pad, pad);
        ad.show();
    }

    public void edit_gender(View v) {
        AlertDialog dialog;
        final CharSequence[] items = {"Male", "Female", "Other"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int choice = -1;
        for (int i = 0; i < items.length; i++) {
            if (gender.getText().toString().trim().compareTo(items[i].toString()) == 0) {
                choice = i;
                break;
            }
        }
        builder.setSingleChoiceItems(items, choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        gender.setText(items[0]);
                        break;
                    case 1:
                        gender.setText(items[1]);
                        break;
                    case 2:
                        gender.setText(items[2]);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void edit_country(View v) {
        AlertDialog dialog;
        final CharSequence[] items = {"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",

                "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria",

                "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",

                "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",

                "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria",

                "Burkina Faso", "Burma (Myanmar)", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",

                "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island",

                "Cocos (Keeling) Islands", "Colombia", "Comoros", "Cook Islands", "Costa Rica",

                "Croatia", "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo",

                "Denmark", "Djibouti", "Dominica", "Dominican Republic",

                "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",

                "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia",

                "Gabon", "Gambia", "Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",

                "Greenland", "Grenada", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",

                "Haiti", "Holy See (Vatican City)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India",

                "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Ivory Coast", "Jamaica",

                "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait",

                "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",

                "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia",

                "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mayotte", "Mexico",

                "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",

                "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia",

                "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",

                "Northern Mariana Islands", "Norway", "Oman", "Other", "Pakistan", "Palau", "Panama",

                "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland",

                "Portugal", "Puerto Rico", "Qatar", "Republic of the Congo", "Romania", "Russia", "Rwanda",

                "Saint Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin",

                "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino",

                "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",

                "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea",

                "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland",

                "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau",

                "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",

                "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "US Virgin Islands", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",

                "Wallis and Futuna", "West Bank", "Yemen", "Zambia", "Zimbabwe"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int choice = -1;
        for (int i = 0; i < items.length; i++) {
            if (country.getText().toString().trim().compareTo(items[i].toString()) == 0) {
                choice = i;
                break;
            }
        }
        builder.setSingleChoiceItems(items, choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                country.setText(items[which]);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

}
