package com.zilche.zilche;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class CropImageActivity extends ActionBarActivity {

    private ImageButton cancel;
    private ImageButton submit;
    private CropImageView iv;
    private boolean edit_profile = false;

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
            if (b.get("edit_profile") == null) {
                edit_profile = false;
            } else if (b.getInt("edit_profile") == 1){
                edit_profile = true;
            }
        }
        if (uri == null) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }

        cancel = (ImageButton) findViewById(R.id.cancelButton);
        submit = (ImageButton) findViewById(R.id.acceptButton);
        iv = (CropImageView) findViewById(R.id.fullImage);
        iv.setAspectRatio(1, 1);
        iv.setVisibility(View.GONE);
        iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        iv.setFixedAspectRatio(true);
        String path;
        if (getIntent().getExtras().get("filepath") != null) {
            path = (String) getIntent().getExtras().get("filepath");
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            int col = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            path = cursor.getString(col);
        }

        BitmapWorker worker = new BitmapWorker(iv, path);
        worker.execute();

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
                if (ret.getWidth() < 200 || ret.getHeight() < 200) {
                    Toast.makeText(CropImageActivity.this, getString(R.string.image_small), Toast.LENGTH_SHORT).show();
                    iv.setFixedAspectRatio(false);
                    iv.setFixedAspectRatio(true);
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(CropImageActivity.this);
                builder.setTitle(getString(R.string.crop_image));
                AlertDialog dialog = null;
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        if (edit_profile) {
                            Bitmap bm50 = getScaled50(ret);
                            ByteArrayOutputStream bytes2 = new ByteArrayOutputStream();
                            bm50.compress(Bitmap.CompressFormat.PNG, 90, bytes2);
                            i.putExtra("data_50", bytes2.toByteArray());
                        } else {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            ret.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                            i.putExtra("data", bytes.toByteArray());
                        }
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
        opt.inJustDecodeBounds = false;
        //BitmapFactory.decodeFile(path, opt);
        final int size = 1024;
        int scale = 1;
        while (opt.outWidth / scale / 2 >= size || opt.outHeight / scale / 2 >= size) {
            scale *= 2;
        }
        opt.inSampleSize = scale;
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, opt);
        //if (bm == null || bm.) return decodeFile(path);
        return bm;
    }

    public class BitmapWorker extends AsyncTask<Integer, Void, Bitmap>  {

        private final WeakReference<CropImageView> imageViewReference;
        private String path2;

        public BitmapWorker(CropImageView iv, String data) {
            this.imageViewReference = new WeakReference<CropImageView>(iv);
            this.path2 = data;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (isCancelled()) return null;
            Bitmap b = Util.decodeFile(path2);
            return b;
            //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //b.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            //return BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.toByteArray().length);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) return;
            imageViewReference.get().setImageBitmap(bitmap);
            imageViewReference.get().setLayerType(View.LAYER_TYPE_HARDWARE, null);
            imageViewReference.get().setVisibility(View.VISIBLE);
        }
    }


    public Bitmap getScaled50(Bitmap bm) {
        return Bitmap.createScaledBitmap(bm, 108, 108, false);
    }


    public void reload_frame(View v) {
        iv.setFixedAspectRatio(false);
        iv.setFixedAspectRatio(true);
    }

    public void rotate_image(View v) {
        iv.rotateImage(90);
    }
}
