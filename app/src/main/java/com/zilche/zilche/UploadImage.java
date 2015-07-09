package com.zilche.zilche;


import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.AdapterView;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Bitmap;




import android.graphics.BitmapFactory;




public class UploadImage extends Activity {

    private ImageAdapter imageAdapter;

    byte[] b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);


        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(position == 0){
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    String ExternalStorageDirectoryPath = Environment
                            .getExternalStorageDirectory()
                            .getAbsolutePath();



                    String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera";


                    Calendar c = Calendar.getInstance();
                    int seconds = c.get(Calendar.SECOND);


                    Uri uriSavedImage=Uri.fromFile(new File(targetPath + "/img" + Integer.toString(seconds) + ".png"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(intent, 1);


                }
                else{
                    ImageView thisview = (ImageView) v;

                    Bitmap bitmap = imageAdapter.getBitmapFromView(thisview);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    b = baos.toByteArray();

                    Intent intent = new Intent(UploadImage.this, FullScreenImage.class);
                    intent.putExtra("picture", b);



                    startActivityForResult(intent, 2);

                }


            }
        });





        String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera";

        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
        File targetDirector = new File(targetPath);

        //wrong
        imageAdapter.add("Drawable//image");
        File[] files = null;
        files = targetDirector.listFiles();
        if(files != null) {

            for (File file : files) {
                imageAdapter.add(file.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            GridView gridview = (GridView) findViewById(R.id.gridview);
            imageAdapter = new ImageAdapter(this);
            gridview.setAdapter(imageAdapter);

            String ExternalStorageDirectoryPath = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath();

            String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera";

            Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
            File targetDirector = new File(targetPath);

            //wrong
            imageAdapter.add("Drawable//image");
            File[] files = null;
            files = targetDirector.listFiles();
            if(files != null) {

                for (File file : files) {
                    imageAdapter.add(file.getAbsolutePath());
                }
            }
        }
        if (requestCode == 2) {
            //pass picture back to create poll
            Toast.makeText(getApplicationContext(), Integer.toString(b.length), Toast.LENGTH_LONG).show();
        }
    }

}
