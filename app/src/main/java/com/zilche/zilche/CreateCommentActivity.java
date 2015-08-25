package com.zilche.zilche;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.prefs.BackingStoreException;


public class CreateCommentActivity extends ActionBarActivity {

    private SlidingPaneLayout spl;
    private EditText comment;
    private RadioButton choice;
    private TextView question;
    private RelativeLayout lay;
    private ImageButton back;
    private Button submit;
    private int category;
    private String pollId;
    private View background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        comment = (EditText) findViewById(R.id.comment);
        choice = (RadioButton) findViewById(R.id.choice);
        question = (TextView) findViewById(R.id.question);
        lay = (RelativeLayout) findViewById(R.id.header);
        submit = (Button) findViewById(R.id.submit);
        background = findViewById(R.id.background);
        back = (ImageButton) findViewById(R.id.back_button_cat);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent i = getIntent();
        if (i == null || i.getExtras() == null) {
            finish();
            return;
        }
        Bundle b = i.getExtras();
        category = b.getInt("category");
        choice.setText(b.getString("choice"));
        question.setText(b.getString("question"));
        pollId = b.getString("poll");
        final int isAnon = b.getInt("isAnon");
        final String owner = b.getString("owner");

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Util.noti_color[category]);
        }
        lay.setBackgroundColor(Util.title_color[category]);


        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset < 0.1) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int off = (int) ((1 - slideOffset) * 250);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor((Util.noti_color[category] & 0x00ffffff) | (off << 24));
                }
                int color = (int) ((1 - slideOffset) * 170);
                background.setBackgroundColor(0x00000000 | (color << 24));
            }

            @Override
            public void onPanelOpened(View panel) {
                setResult(RESULT_CANCELED);
                CreateCommentActivity.this.finish();
                CreateCommentActivity.this.overridePendingTransition(0, 0);
            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(false);
                if (comment.getText().toString().trim().length() < 1) {
                    Toast.makeText(CreateCommentActivity.this, getString(R.string.comment_err), Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                } else {
                    final ParseObject po = ParseObject.createWithoutData("poll", pollId);
                    if (!po.isDataAvailable()) {
                        final ProgressDialog dialog = new ProgressDialog(CreateCommentActivity.this);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.upload_comment));
                        dialog.show();
                        po.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    final ParseObject comment_obj = new ParseObject("Comment");
                                    comment_obj.put("pollId", pollId);
                                    if (ParseUser.getCurrentUser().getBytes("image") != null) {
                                        comment_obj.put("image", ParseUser.getCurrentUser().getBytes("image"));
                                    }
                                    comment_obj.put("email", ParseUser.getCurrentUser().getEmail());
                                    comment_obj.put("author", ParseUser.getCurrentUser().get("name"));
                                    comment_obj.put("comment", comment.getText().toString().trim());
                                    if (isAnon != 1) {
                                        if (ParseUser.getCurrentUser().getEmail().compareTo(owner) == 0) {
                                            comment_obj.put("op", 1);
                                        } else if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                                            comment_obj.put("mod", 1);
                                        }
                                    }
                                    if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                                        comment_obj.put("mod", 1);
                                    }
                                    comment_obj.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                po.increment("comments_count");
                                                po.saveInBackground(new SaveCallback() {

                                                    @Override
                                                    public void done(ParseException e) {
                                                        dialog.dismiss();
                                                        Intent i = new Intent();
                                                        i.putExtra("total", po.getInt("comments_count"));
                                                        setResult(RESULT_OK, i);
                                                        finish();
                                                        overridePendingTransition(0, R.anim.left_to_right);
                                                    }
                                                });
                                            } else {
                                                dialog.dismiss();
                                                submit.setEnabled(true);
                                                Toast.makeText(CreateCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    submit.setEnabled(true);
                                    Toast.makeText(CreateCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        final ParseObject comment_obj = new ParseObject("Comment");
                        comment_obj.put("pollId", pollId);
                        if (ParseUser.getCurrentUser().getBytes("image") != null) {
                            comment_obj.put("image", ParseUser.getCurrentUser().getBytes("image"));
                        }
                        comment_obj.put("email", ParseUser.getCurrentUser().getEmail());
                        comment_obj.put("author", ParseUser.getCurrentUser().get("name"));
                        comment_obj.put("comment", comment.getText().toString().trim());
                        if (isAnon != 1) {
                            if (ParseUser.getCurrentUser().getEmail().compareTo(owner) == 0) {
                                comment_obj.put("op", 1);
                            } else if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                                comment_obj.put("mod", 1);
                            }
                        }
                        if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                            comment_obj.put("mod", 1);
                        }
                        final ProgressDialog dialog = new ProgressDialog(CreateCommentActivity.this);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.upload_comment));
                        dialog.show();
                        comment_obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    po.increment("comments_count");
                                    po.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            dialog.dismiss();
                                            Intent i = new Intent();
                                            i.putExtra("total", po.getInt("comments_count"));
                                            setResult(RESULT_OK, i);
                                            finish();
                                            overridePendingTransition(0, R.anim.left_to_right);
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    submit.setEnabled(true);
                                    Toast.makeText(CreateCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
        overridePendingTransition(0, R.anim.left_to_right);
    }

}
