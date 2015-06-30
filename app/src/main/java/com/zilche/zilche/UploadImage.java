package com.zilche.zilche;


import java.io.File;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.GridView;
import android.widget.Toast;

public class UploadImage extends Activity {

    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

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


}
