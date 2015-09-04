package com.zilche.zilche;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ReplyCommentActivity extends ActionBarActivity {

    private HashMap<String, ParseUser> profile;
    private String replyTo;
    private boolean first = true;
    private boolean loading = false;
    private boolean complete = false;
    private LinearLayout content;
    private ProgressBar progress;
    private ImageButton submit;
    private EditText comment_text;
    private ImageButton back_button;
    private View background;
    private SlidingPaneLayout spl;
    private int category;
    private String comment_id;
    private LinkedList comment_list;
    private ListView listview;
    private LinearLayout reload;
    private ArrayList<String> replies;
    private View.OnClickListener load_user_profile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int val = (int) v.getTag();
            Intent t = new Intent(ReplyCommentActivity.this, RetrieveProfileActivity.class);
            if (val > comment_list.size() || comment_list.get(val) instanceof String) {
                return;
            }
            Comment c = (Comment) comment_list.get(val);
            t.putExtra("author_id", c.getAuthor_id());
            t.putExtra("authorRealName", c.getName());
            startActivity(t);
            overridePendingTransition(R.anim.right_to_left, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);
        profile = new HashMap<>();
        content = (LinearLayout) findViewById(R.id.content);
        reload = (LinearLayout) findViewById(R.id.reload_bg_full);
        progress = (ProgressBar) findViewById(R.id.progress_bar);
        listview = (ListView) findViewById(R.id.comment_list);
        listview.setSelector(android.R.color.transparent);
        comment_list = new LinkedList<>();
        Intent i = getIntent();
        if (i == null || i.getExtras() == null) {
            finish();
            return;
        }
        Bundle b = i.getExtras();
        category = b.getInt("category");
        comment_id = b.getString("comment_id");
        final int isAnon = b.getInt("isAnon");
        final String owner = b.getString("owner");
        final String pollId = b.getString("poll");
        replies = b.getStringArrayList("replies");
        if (replies == null) {
            replies = new ArrayList<>();
        }
        replies.add(comment_id);
        if (replies.size() > 20) {
            replies.remove(0);
        }
        back_button = (ImageButton) findViewById(R.id.back_button_reply);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Util.noti_color[category]);
        }
        header.setBackgroundColor(Util.title_color[category]);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        background = findViewById(R.id.background);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset < 0.1) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(back_button.getWindowToken(), 0);
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
                ReplyCommentActivity.this.finish();
                ReplyCommentActivity.this.overridePendingTransition(0, 0);
            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
        ListAdapter la = new ListAdapter(comment_list);
        listview.setAdapter(la);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && !complete && !loading && comment_list.size() > 0) {
                    Comment c = (Comment) comment_list.get(0);
                    loading = true;
                    getComments(c.getReplies());
                }
            }
        });
        getComments(replies);
        findViewById(R.id.reload_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first = true;
                reload.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                getComments(replies);
            }
        });
        submit = (ImageButton) findViewById(R.id.submit);
        comment_text = (EditText) findViewById(R.id.comment_text);
        comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_list.size() - 1 >= 0) {
                    listview.setSelection(comment_list.size() - 1);
                }
            }
        });
        replyTo = "@" + b.getString("author_name") + ": ";
        comment_text.setText(replyTo);
        comment_text.setSelection(replyTo.length());
        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(replyTo)) {
                    comment_text.setText(replyTo);
                    comment_text.setSelection(replyTo.length());
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(false);
                if (comment_text.getText().toString().trim().length() - replyTo.length() < 1) {
                    Toast.makeText(ReplyCommentActivity.this, getString(R.string.comment_err), Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                } else {
                    final ParseObject po = ParseObject.createWithoutData("poll", pollId);
                    if (!po.isDataAvailable()) {
                        final ProgressDialog dialog = new ProgressDialog(ReplyCommentActivity.this);
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
                                    comment_obj.put("replies", replies);
                                 //   if (ParseUser.getCurrentUser().getBytes("image") != null) {
                                  //      comment_obj.put("image", ParseUser.getCurrentUser().getBytes("image"));
                                  //  }
                                    comment_obj.put("author_id", ParseUser.getCurrentUser().getObjectId());
                                    if (ParseUser.getCurrentUser().getEmail() == null) {
                                        if (ParseUser.getCurrentUser().getString("email_str") != null) {
                                            comment_obj.put("email", ParseUser.getCurrentUser().getString("email_str"));
                                        }
                                    } else {
                                        comment_obj.put("email", ParseUser.getCurrentUser().getEmail());
                                    }
                                    comment_obj.put("author", ParseUser.getCurrentUser().get("name"));
                                    comment_obj.put("comment", comment_text.getText().toString().trim());
                                    if (isAnon != 1) {
                                        if (ParseUser.getCurrentUser().getObjectId().compareTo(owner) == 0) {
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
                                                Toast.makeText(ReplyCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    submit.setEnabled(true);
                                    Toast.makeText(ReplyCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        final ParseObject comment_obj = new ParseObject("Comment");
                        comment_obj.put("pollId", pollId);
                        comment_obj.put("replies", replies);
                       // if (ParseUser.getCurrentUser().getBytes("image") != null) {
                        //    comment_obj.put("image", ParseUser.getCurrentUser().getBytes("image"));
                        //}
                        comment_obj.put("author_id", ParseUser.getCurrentUser().getObjectId());
                        if (ParseUser.getCurrentUser().getEmail() == null) {
                            if (ParseUser.getCurrentUser().getString("email_str") != null) {
                                comment_obj.put("email", ParseUser.getCurrentUser().getString("email_str"));
                            }
                        } else {
                            comment_obj.put("email", ParseUser.getCurrentUser().getEmail());
                        }
                        comment_obj.put("author", ParseUser.getCurrentUser().get("name"));
                        comment_obj.put("comment", comment_text.getText().toString().trim());
                        if (isAnon != 1) {
                            if (ParseUser.getCurrentUser().getObjectId().compareTo(owner) == 0) {
                                comment_obj.put("op", 1);
                            } else if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                                comment_obj.put("mod", 1);
                            }
                        }
                        if (ParseUser.getCurrentUser().get("mod") != null && ParseUser.getCurrentUser().getInt("mod") == 1) {
                            comment_obj.put("mod", 1);
                        }
                        final ProgressDialog dialog = new ProgressDialog(ReplyCommentActivity.this);
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
                                    Toast.makeText(ReplyCommentActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    public void getComments(final ArrayList<String> id) {
        loading = true;
        comment_list.add(0, "progress");
        ListAdapter a = (ListAdapter) listview.getAdapter();
        a.notifyDataSetChanged();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comment");
        query.whereContainedIn("objectId", id);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (e == null) {
                    LinkedList<String> ids = new LinkedList<String>();
                    for (int i = 0; i < list.size(); i++) {
                        if (!profile.containsKey(list.get(i).getString("author_id"))) {
                            ids.add(list.get(i).getString("author_id"));
                        }
                    }
                    System.out.println(ids.size());
                    System.out.println(ids);
                    if (list.size() != 0) {
                        ParseQuery<ParseUser> q = ParseUser.getQuery();
                        q.whereContainedIn("objectId", ids);
                        q.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list2, ParseException e) {
                                if (e == null) {
                                    System.out.println(list2.size());
                                    for (ParseUser u_iter : list2) {
                                        if (!profile.containsKey(u_iter.getObjectId())) {
                                            profile.put(u_iter.getObjectId(), u_iter);
                                        }
                                    }
                                    if (list.size() < 20) {
                                        complete = true;
                                    }
                                    comment_list.remove(0);
                                    if (first) {
                                        comment_list.clear();
                                        first = false;
                                    }
                                    int index = list.size() + listview.getFirstVisiblePosition();
                                    View v = listview.getChildAt(listview.getHeaderViewsCount());
                                    int top = (v == null) ? 0 : v.getTop();
                                    for (int i = 0; i < list.size(); i++) {
                                        comment_list.addFirst(Util.parseComment(list.get(i), profile.get(list.get(i).getString("author_id"))));
                                    }
                                    ListAdapter la = (ListAdapter) listview.getAdapter();
                                    la.notifyDataSetChanged();
                                    if (comment_list.size() == list.size() && comment_list.size() != 0) {
                                        listview.setSelection(comment_list.size() - 1);
                                    } else {
                                        listview.setSelectionFromTop(index, top);
                                    }
                                    loading = false;
                                    if (content.getVisibility() == View.GONE) {
                                        content.setVisibility(View.VISIBLE);
                                    }
                                    if (progress.getVisibility() == View.VISIBLE) {
                                        progress.setVisibility(View.GONE);
                                    }
                                    if (reload.getVisibility() == View.VISIBLE) {
                                        reload.setVisibility(View.GONE);
                                    }
                                } else {
                                    comment_list.remove(0);
                                    ListAdapter la = (ListAdapter) listview.getAdapter();
                                    la.notifyDataSetChanged();
                                    if (id != replies) {
                                        comment_list.add(0, "reload");
                                    } else {
                                        content.setVisibility(View.GONE);
                                        progress.setVisibility(View.GONE);
                                        reload.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    } else {
                        if (list.size() < 20) {
                            complete = true;
                        }
                        comment_list.remove(0);
                        if (first) {
                            comment_list.clear();
                            first = false;
                        }
                        int index = list.size() + listview.getFirstVisiblePosition();
                        View v = listview.getChildAt(listview.getHeaderViewsCount());
                        int top = (v == null) ? 0 : v.getTop();
                        for (int i = 0; i < list.size(); i++) {
                            comment_list.addFirst(Util.parseComment(list.get(i), profile.get(list.get(i).getString("author_id"))));
                        }
                        ListAdapter la = (ListAdapter) listview.getAdapter();
                        la.notifyDataSetChanged();
                        if (comment_list.size() == list.size() && comment_list.size() != 0) {
                            listview.setSelection(comment_list.size() - 1);
                        } else {
                            listview.setSelectionFromTop(index, top);
                        }
                        loading = false;
                        if (content.getVisibility() == View.GONE) {
                            content.setVisibility(View.VISIBLE);
                        }
                        if (progress.getVisibility() == View.VISIBLE) {
                            progress.setVisibility(View.GONE);
                        }
                        if (reload.getVisibility() == View.VISIBLE) {
                            reload.setVisibility(View.GONE);
                        }
                    }
                } else {
                    comment_list.remove(0);
                    ListAdapter la = (ListAdapter) listview.getAdapter();
                    la.notifyDataSetChanged();
                    if (id != replies) {
                        comment_list.add(0, "reload");
                    } else {
                        content.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        reload.setVisibility(View.VISIBLE);
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
        imm.hideSoftInputFromWindow(back_button.getWindowToken(), 0);
        overridePendingTransition(0, R.anim.left_to_right);
    }

    public class ListAdapter extends BaseAdapter {

        LinkedList comment;

        public ListAdapter(LinkedList list) {
            comment = list;
        }

        @Override
        public int getCount() {
            return comment.size();
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
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = getLayoutInflater();
                v = vi.inflate(R.layout.comment_item, null);
            }
            if (comment.get(position) instanceof String) {
                if (((String) comment.get(position)).compareTo("progress") == 0) {
                    v.findViewById(R.id.progress_lay).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.reload_lay).setVisibility(View.GONE);
                    v.findViewById(R.id.comment_lay).setVisibility(View.GONE);
                    return v;
                } else if (((String) comment.get(position)).compareTo("reload") == 0) {
                    v.findViewById(R.id.progress_lay).setVisibility(View.GONE);
                    v.findViewById(R.id.reload_lay).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.comment_lay).setVisibility(View.GONE);
                    v.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            comment_list.remove(0);
                            if(comment_list.get(0) instanceof Comment) {
                                Comment c = (Comment) comment_list.get(0);
                                getComments(c.getReplies());
                            }
                        }
                    });
                    return v;
                }
                return null;
            }

            v.findViewById(R.id.progress_lay).setVisibility(View.GONE);
            v.findViewById(R.id.reload_lay).setVisibility(View.GONE);
            v.findViewById(R.id.comment_lay).setVisibility(View.VISIBLE);

            ImageView iv = (ImageView) v.findViewById(R.id.image);
            TextView author = (TextView) v.findViewById(R.id.author);
            TextView date = (TextView) v.findViewById(R.id.date);
            TextView comment = (TextView) v.findViewById(R.id.comment);
            TextView mod = (TextView) v.findViewById(R.id.mod);
            TextView op = (TextView) v.findViewById(R.id.op);
            Comment c = (Comment) this.comment.get(position);
            author.setText(c.getName());
            author.setTag(position);
            author.setOnClickListener(load_user_profile);
            date.setText(c.getDate_added());
            comment.setText(c.getComment_text());
            if (c.getOp() == 1) {
                op.setVisibility(View.VISIBLE);
            } else {
                op.setVisibility(View.GONE);
            }
            if (c.getMod() == 1) {
                mod.setVisibility(View.VISIBLE);
            } else {
                mod.setVisibility(View.GONE);
            }
            if (c.hasImage()) {
                iv.setTag(position);
                CommentBitmapWorker cbw = new CommentBitmapWorker(iv, c.getImage(), R.drawable.anon_30);
                cbw.execute();
            } else {
                iv.setTag(position);
                CommentBitmapWorker cbw = new CommentBitmapWorker(iv, null, R.drawable.anon_30);
                cbw.execute();
            }
            return v;
        }
    }

    public class CommentBitmapWorker extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;
        private byte[] data;
        private int anon;

        public CommentBitmapWorker(ImageView iv, byte[] data, int anon) {
            imageViewWeakReference = new WeakReference<ImageView>(iv);
            this.data = data;
            this.anon = anon;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (data == null) {
                return BitmapFactory.decodeResource(getResources(), anon);
            }
            Bitmap fac = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bm = Bitmap.createScaledBitmap(fac, (int) Util.convertDpToPixel(30, ReplyCommentActivity.this),
                    (int) Util.convertDpToPixel(30, ReplyCommentActivity.this), true);
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled() || bitmap == null || imageViewWeakReference.get() == null) return;
            imageViewWeakReference.get().setImageBitmap(bitmap);
            imageViewWeakReference.get().setOnClickListener(load_user_profile);
        }
    }

}
