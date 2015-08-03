package com.zilche.zilche;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;


public class CropImageActivity extends ActionBarActivity {

    private ImageButton cancel;
    private ImageButton submit;
    private CropImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        Intent i = getIntent();
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xff000000);
        }
        if (i != null) {
            Bundle b= i.getExtras();
            uri = (Uri) b.get("uri");
        }
        if (uri == null) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }

        String path;
        if (getIntent().getExtras().get("filepath") != null) {
            path = (String) getIntent().getExtras().get("filepath");
            System.out.println(path);
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            int col = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            path = cursor.getString(col);
        }

        Bitmap bm = decodeFile(path);
        if (bm == null || bm.getWidth() == 0 || bm.getHeight() == 0)
            bm = decodeFile(path); // try again incase first pass thru failed

        cancel = (ImageButton) findViewById(R.id.cancelButton);
        submit = (ImageButton) findViewById(R.id.acceptButton);
        iv = (CropImageView) findViewById(R.id.fullImage);
        iv.setAspectRatio(1, 1);

        iv.setFixedAspectRatio(true);

        iv.setImageBitmap(bm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap ret = iv.getCroppedImage();
                if (ret.getWidth() < 300) {
                    Toast.makeText(CropImageActivity.this, "Image is too small.", Toast.LENGTH_SHORT).show();
                    iv.setFixedAspectRatio(false);
                    iv.setFixedAspectRatio(true);
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(CropImageActivity.this);
                builder.setTitle("Crop image?");
                AlertDialog dialog = null;
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Comfirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        ret.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        Intent i = new Intent();
                        i.putExtra("data", bytes.toByteArray());
                        setResult(RESULT_OK, i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    public Bitmap decodeFile(String path) {
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
        return bm;
    }


    public void reload_frame(View v) {
        iv.setFixedAspectRatio(false);
        iv.setFixedAspectRatio(true);
    }

}
