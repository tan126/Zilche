package com.zilche.zilche;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class FullScreenImage extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);


        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView image = (ImageView) findViewById(R.id.fullImage);

        image.setImageBitmap(bmp);





/*
        Intent mIntent = getIntent();
        int thisid = mIntent.getIntExtra("id", 0);

        ImageView imageView = (ImageView) findViewById(R.id.fullImage);

//        imageView.setLayoutParams( new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));

        imageView.setImageResource(thisid);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);*/

    }
}