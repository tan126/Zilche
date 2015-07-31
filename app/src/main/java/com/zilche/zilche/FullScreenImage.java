package com.zilche.zilche;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView.ScaleType;


public class FullScreenImage extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xff000000);
        }
        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView image = (ImageView) findViewById(R.id.fullImage);
        image.setImageBitmap(bmp);
        image.setScaleType(ScaleType.FIT_XY);

    }
    public void acceptPhotoSelection(View v) {
        setResult(RESULT_OK);
        finish();
    }

    public void cancelPhotoSelection(View v) {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}