package com.zilche.zilche;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;


public class CreatePollActivity2 extends AppCompatActivity {

    private TextView title;
    private ViewPager vp;
    private boolean exit = false;
    private ImageButton left;
    private ImageButton right;
    private FirstFragment frag1 = new FirstFragment();
    private SecondFragment frag2 = new SecondFragment();
    private ThirdFragment frag3 = new ThirdFragment();
    private View.OnClickListener left_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (vp.getCurrentItem() != 0) {
                vp.setCurrentItem(vp.getCurrentItem() - 1);
            }
        }
    };
    private View.OnClickListener right_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (vp.getCurrentItem() != 2) {
                vp.setCurrentItem(vp.getCurrentItem() + 1);
            }
        }
    };
    private View.OnClickListener close_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            back_clicked();
        }
    };
    final static private int REQUEST_CAMERA = 111;
    final static private int SELECT_FILE = 222;
    final static private int REMOVE_FILE = 333;
    final static private int CROP_IMAGE = 444;

    public void back_clicked() {
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
        poll.put("author_id", ParseUser.getCurrentUser().getObjectId());
        poll.put("question", question);
        if (ParseUser.getCurrentUser().getEmail() == null) {
            if (ParseUser.getCurrentUser().getString("email_str") != null) {
                poll.put("author", ParseUser.getCurrentUser().getString("email_str"));
            }
        } else {
            poll.put("author", ParseUser.getCurrentUser().getEmail());
        }
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

    public ParseFile createImage() {
        if (frag1.imageBound() && frag1.getImage() != null) {
            ParseFile file = new ParseFile("image.jpg", frag1.getImage());
            return file;
        }
        return null;
    }

    public byte[] getImage() {
        if (frag1.imageBound)
            return frag1.getImage();
        return null;
    }

    public Uri getUri() {
        return frag1.getUri();
    }

    public String getFilePath() {
        return frag1.getFilePath();
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
        left = (ImageButton) findViewById(R.id.back_button_create_poll);
        right = (ImageButton) findViewById(R.id.next);
        left.setOnClickListener(close_click);
        right.setOnClickListener(right_click);
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
                        title.setText(getString(R.string.new_poll));
                        break;
                    case 1:
                        title.setText(getString(R.string.options));
                        break;
                    case 2:
                        title.setText(getString(R.string.Category));
                        break;
                    default:
                        break;
                }
                if (position == 0) {
                    left.setImageResource(R.drawable.ic_close_white_24dp);
                    left.setOnClickListener(close_click);
                    right.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    left.setImageResource(R.drawable.ic_chevron_left_white_24dp);
                    left.setOnClickListener(left_click);
                    right.setVisibility(View.VISIBLE);
                } else {
                    right.setVisibility(View.GONE);
                    left.setImageResource(R.drawable.ic_chevron_left_white_24dp);
                    left.setOnClickListener(left_click);
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

        private File file;
        private String filePath;
        private Uri uri;
        private boolean imageBound = false;
        private TextView question;
        private SwitchCompat switchBtn;
        private TextView wordCount;
        private FloatingActionButton fab;
        private byte[] image;
        private View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.gallery), getString(R.string.cancel)};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.select_image));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            file = new File(appFolderCheckandCreate(), "." + System.currentTimeMillis() + ".jpg");
                            filePath = file.getPath();
                            uri = Uri.fromFile(file);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        };
        private View.OnClickListener onclick2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBound) {
                    if (image == null) {
                        Toast.makeText(getActivity(), getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(getActivity(), FullScreenImage.class);
                        i.putExtra("picture", image);
                        startActivityForResult(i, REMOVE_FILE);
                    }
                }
            }
        };

        private String appFolderCheckandCreate(){

            String appFolderPath="";
            File externalStorage = Environment.getExternalStorageDirectory();

            if (externalStorage.canWrite())
            {
                appFolderPath = externalStorage.getAbsolutePath() + "/Zilche/.imgfldr";
                File dir = new File(appFolderPath);

                if (!dir.exists())
                {
                    dir.mkdirs();
                }

            }

            return appFolderPath;
        }


        public Uri getUri() {
            return uri;
        }

        public String getFilePath() {
            return file.getAbsolutePath();
        }

        public byte[] getImage() {
            return image;
        }

        public boolean imageBound() {
            return imageBound;
        }

        public String getQuestion() {
            String ret;
            ret = question.getText().toString().trim();
            if (ret.length() == 0) {
                question.requestFocus();
                Toast.makeText(getActivity(), getString(R.string.question_empty), Toast.LENGTH_SHORT).show();
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
            File root = new File(appFolderCheckandCreate());
            File[] list = root.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    list[i].delete();
                }
            }
            question = (TextView) rootView.findViewById(R.id.question);
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(onclick);
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

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    if (!file.exists()) {
                        Toast.makeText(getActivity(), getString(R.string.image_load_err), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CreatePollActivity2 act = (CreatePollActivity2) getActivity();
                    Uri uri = act.getUri();
                    Intent i = new Intent(getActivity(), CropImageActivity.class);
                    i.putExtra("uri", uri);
                    i.putExtra("filepath", act.getFilePath());
                    startActivityForResult(i, CROP_IMAGE);
                } else if (requestCode == SELECT_FILE) {
                    Intent i = new Intent(getActivity(), CropImageActivity.class);
                    i.putExtra("uri", data.getData());
                    startActivityForResult(i, CROP_IMAGE);
                } else if (requestCode == REMOVE_FILE) {
                    fab.setImageResource(R.drawable.ic_camera_alt_white_24dp);
                    fab.setOnClickListener(onclick);
                    image = null;
                    imageBound = false;
                } else if (requestCode == CROP_IMAGE) {
                    fab.setOnClickListener(onclick2);
                    fab.setImageResource(R.drawable.ic_done_white_24dp);
                    imageBound = true;
                    byte[] bm = (byte[]) data.getExtras().get("data");
                    this.image = bm;
                    Toast.makeText(getActivity(), getString(R.string.click_button_view_image), Toast.LENGTH_SHORT).show();
                }
            }
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
                        Toast.makeText(getActivity(), getString(R.string.maximum_10), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), getString(R.string.option_empty), Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        break;
                    }
                }
            }
            if (ret.length < 2) {
                Toast.makeText(getActivity(), getString(R.string.two_options), Toast.LENGTH_SHORT).show();
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.create_poll_new3, container, false);
            lv = (ListView) rootView.findViewById(R.id.cat_list);
            fab = (FloatingActionButton) rootView.findViewById(R.id.submit_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setEnabled(false);
                    fab.setClickable(false);
                    final ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(getString(R.string.creating_poll));
                    dialog.setCancelable(false);
                    CreatePollActivity2 activity = (CreatePollActivity2) getActivity();
                    final ParseObject parseObject = activity.makeObject();
                    final ParseFile file = activity.createImage();
                    if (parseObject == null) {
                        fab.setEnabled(true);
                        fab.setClickable(true);
                        return;
                    }
                    if (file == null) {
                        parseObject.put("haveImage", 0);
                        dialog.show();
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Poll poll = Util.parsePollObject(parseObject, getActivity());
                                    Intent i = new Intent(getActivity(), PollViewActivity.class);
                                    i.putExtra("poll", poll);
                                    Toast.makeText(getActivity(), getString(R.string.create_success), Toast.LENGTH_SHORT).show();
                                    startActivity(i);
                                    getActivity().finish();
                                    dialog.dismiss();
                                } else {
                                    fab.setEnabled(true);
                                    fab.setClickable(true);
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        dialog.show();
                        parseObject.put("haveImage", 1);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    parseObject.put("image", file);
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Poll poll = Util.parsePollObject(parseObject, getActivity());
                                                Intent i = new Intent(getActivity(), PollViewActivity.class);
                                                i.putExtra("poll", poll);
                                                Toast.makeText(getActivity(), getString(R.string.create_success), Toast.LENGTH_SHORT).show();
                                                startActivity(i);
                                                dialog.dismiss();
                                                getActivity().finish();
                                            } else {
                                                dialog.dismiss();
                                                fab.setEnabled(true);
                                                fab.setClickable(true);
                                                Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                } else {
                                    dialog.dismiss();
                                    fab.setEnabled(true);
                                    fab.setClickable(true);
                                    Toast.makeText(getActivity(), getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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

    public class BitmapWorker extends AsyncTask<Integer, Void, byte[]> {

        private byte[] data2;
        private Uri uri;

        public BitmapWorker(byte[] data, Uri d2) {
            this.data2 = data;
            this.uri = d2;
        }


        @Override
        protected byte[] doInBackground(Integer... params) {
            Uri selectedImgUri = uri;
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImgUri, projection, null, null, null);
            int col = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(col);
            Bitmap bm;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opt);
            final int size = 1025;
            int scale = 1;
            while (opt.outWidth / scale / 2 >= size || opt.outHeight / scale / 2 >= size) {
                scale *= 2;
            }
            opt.inSampleSize = scale;
            opt.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, opt);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            data2 = bytes.toByteArray();
            return data2;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Zilche app = (Zilche) getApplication();
        if (frag1 == null || frag2 == null || frag3 == null || app.getFav() == null || app.getMap() == null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("restart", 1);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }
    }


}


