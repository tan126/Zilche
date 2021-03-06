package com.zilche.zilche;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SlideViewAdapter adapter;
    private ViewPager viewPager;
    private PagerTabStrip pts;
    private FloatingActionsMenu plusButton;
    private ImageView filter_bg;
    private boolean loginFlag;
    private HashMap<String, Integer> map;
    private byte[] image_ori;
    private String name_ori;
    private CircleImageView image;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().get("restart") != null) {
            Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }
        Zilche app = (Zilche) getApplication();

        int SELECTED_POSITION = 0;
        plusButton = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        adapter = new SlideViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        map = app.getMap();
        //v.setClickable(true);
        // Load Username
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && !ParseAnonymousUtils.isLinked(currentUser)) {
            // do stuff with the user
            String n = currentUser.getString("name");
            loginFlag = true;
            plusButton.setVisibility(View.VISIBLE);
        } else {
            // show the signup or login screen
            SELECTED_POSITION = 1;
            loginFlag = false;
            plusButton.setVisibility(View.GONE);
        }

        pts = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        pts.setTextColor(0xffffffff);
        pts.setTabIndicatorColor(0xFF3F51B5);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter_bg = (ImageView) findViewById(R.id.filter_bg);
        filter_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plusButton != null)
                    plusButton.collapse();
            }
        });

        FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setTitle(getString(R.string.new_poll));

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(MainActivity.this, CreatePollActivity2.class);
                startActivity(t);
                plusButton.collapse();
            }
        });



        final DrawerLayout myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawer.setStatusBarBackgroundColor(0xff303F9F);
        plusButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                filter_bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                filter_bg.setVisibility(View.GONE);
            }
        });

        ListView list=(ListView)findViewById(R.id.menulist);
        if (loginFlag) {
            String menu[] = {getString(R.string.browse_zilche), getString(R.string.my_poll), getString(R.string.my_profile), getString(R.string.Fav), getString(R.string.log_out)};
            Integer imgID[] = {R.drawable.dashgrey, R.drawable.assesgrey, R.drawable.persongrey,
                    R.drawable.favgrey, R.drawable.powergrey};

            View header = getLayoutInflater().inflate(R.layout.nav_header, null, false);
            list.addHeaderView(header);
            CustomListAdapter myadapter=new CustomListAdapter(this, menu, imgID, SELECTED_POSITION);
            list.setAdapter(myadapter);
            list.setSelection(0);

            name = (TextView) findViewById(R.id.name);
            TextView email = (TextView) findViewById(R.id.email);
            name_ori = currentUser.getString("name");
            if (name_ori != null) {
                name.setText(currentUser.getString("name"));
            }
            if (currentUser.getEmail() == null) {
                if (currentUser.getString("email_str") != null) {
                    email.setText(currentUser.getString("email_str"));
                }
            } else {
                email.setText(currentUser.getEmail());
            }
            image = (CircleImageView) findViewById(R.id.circleView);
            if (currentUser.getBytes("image") != null) {
                image_ori = currentUser.getBytes("image");
                Bitmap b = BitmapFactory.decodeByteArray(currentUser.getBytes("image"), 0, currentUser.getBytes("image").length);
                Bitmap bm = Bitmap.createScaledBitmap(b, (int) Util.convertDpToPixel(54, this), (int) Util.convertDpToPixel(54, this), true);
                image.setImageBitmap(bm);
            }
        } else {
            String menu[] = {getString(R.string.browse_zilche), getString(R.string.my_poll), getString(R.string.my_profile), getString(R.string.Fav)};
            Integer imgID[] = {R.drawable.dashgrey, R.drawable.assesgrey, R.drawable.persongrey,
                    R.drawable.favgrey};

            CustomListAdapter myadapter=new CustomListAdapter(this, menu, imgID, SELECTED_POSITION);
            View header = getLayoutInflater().inflate(R.layout.nav_header_not_login, null, false);
            list.addHeaderView(header);
            list.setAdapter(myadapter);
            list.setSelection(0);

            findViewById(R.id.full_lay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginAndSignup();
                }
            });
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.item);
                if (v == null) {
                    return;
                }
                if (v.getText() == getString(R.string.my_poll)) {
                    if (loginFlag) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(MainActivity.this, MyPollActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.right_to_left, 0);
                            }
                        }, 250);
                        myDrawer.closeDrawer(Gravity.LEFT);
                    } else {
                        loginAndSignup();
                    }
                }
                else if (v.getText() == getString(R.string.my_profile)) {
                    if (loginFlag) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(MainActivity.this, MyProfileActivity2.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                overridePendingTransition(R.anim.right_to_left, 0);
                            }
                        }, 250);
                        myDrawer.closeDrawer(Gravity.LEFT);
                    } else {
                        loginAndSignup();
                    }
                }
                else if (v.getText() == getString(R.string.log_out)) {
                    ParseUser u = new ParseUser();
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent i = new Intent(MainActivity.this, SplashScreenActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.connection_err), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (v.getText() == getString(R.string.Fav)) {
                    if (loginFlag) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(MainActivity.this, FavouriteActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                overridePendingTransition(R.anim.right_to_left, 0);
                            }
                        }, 250);
                        myDrawer.closeDrawer(Gravity.LEFT);
                    } else {
                        loginAndSignup();
                    }
                }
            }
        });

        myDrawer.setDrawerShadow(R.drawable.shadow, GravityCompat.START);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_list);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDrawer.openDrawer(Gravity.LEFT);
            }
        });

        myDrawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(loginFlag==true)
                    plusButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                if(loginFlag==true) {
                    plusButton.collapse();
                    plusButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(loginFlag==true)
                    if (slideOffset == 0) {
                        plusButton.setVisibility(View.VISIBLE);
                    } else if (plusButton.getVisibility() == View.VISIBLE) {
                        plusButton.collapse();
                        plusButton.setVisibility(View.GONE);
                    }
            }

        });

    }

    public void loginAndSignup() {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, 0);
    }

    @Override
    public void onBackPressed() {
        if (plusButton.isExpanded()) {
            plusButton.collapse();
        } else {
            moveTaskToBack(true);
        }
    }

    public class SlideViewAdapter extends FragmentPagerAdapter {

        String titles[] = {getString(R.string.categories), getString(R.string.newest_posts), getString(R.string.popular_posts)};

        public SlideViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if(position == 0){
                fragment = new CategoryFragment();
            }
            else if (position == 1){
                fragment = new NewestFragment();
            }
            else if (position == 2){
                fragment = new PopularFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

    private static class FadePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.90f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else {
                view.setAlpha(0);
            }
        }
    }

    public void newPoll(View v) {
        Intent i = new Intent(this, CreatePollActivity2.class);
        startActivity(i);
        plusButton.collapse();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (image_ori == null && ParseUser.getCurrentUser().getBytes("image") != null) {
            Bitmap b = BitmapFactory.decodeByteArray(ParseUser.getCurrentUser().getBytes("image"), 0, ParseUser.getCurrentUser().getBytes("image").length);
            Bitmap bm = Bitmap.createScaledBitmap(b, (int) Util.convertDpToPixel(54, this), (int) Util.convertDpToPixel(54, this), true);
            image.setImageBitmap(bm);
            image_ori = ParseUser.getCurrentUser().getBytes("image");
        } else if (image_ori != null && ParseUser.getCurrentUser().getBytes("image") == null) {
            image_ori = null;
            image.setImageResource(R.drawable.anon_54);
        } else if (image_ori != null && ParseUser.getCurrentUser().getBytes("image") != null) {
            if (image_ori != ParseUser.getCurrentUser().getBytes("image")) {
                Bitmap b = BitmapFactory.decodeByteArray(ParseUser.getCurrentUser().getBytes("image"), 0, ParseUser.getCurrentUser().getBytes("image").length);
                Bitmap bm = Bitmap.createScaledBitmap(b, (int) Util.convertDpToPixel(54, this), (int) Util.convertDpToPixel(54, this), true);
                image.setImageBitmap(bm);
                image_ori = ParseUser.getCurrentUser().getBytes("image");
            }
        }

        if (name != null && name_ori != null && ParseUser.getCurrentUser().getString("name") != null && name_ori.compareTo(ParseUser.getCurrentUser().getString("name")) != 0) {
            name_ori = ParseUser.getCurrentUser().getString("name");
            name.setText(name_ori);
        }
        Zilche app = (Zilche) getApplication();
        if (app.getFav() == null || app.getMap() == null) {
            Intent i = new Intent(this, SplashScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void openProfile(View v) {
        if (ParseUser.getCurrentUser() == null || ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, MyProfileActivity2.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_to_left, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LinearLayout searchBar = (LinearLayout) findViewById(R.id.my_search_bar);
        ImageView searchIcon = (ImageView) findViewById(R.id.searchicon);
        searchIcon.setColorFilter(0x66ffffff, PorterDuff.Mode.MULTIPLY);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchList.class);
                startActivity(i);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}