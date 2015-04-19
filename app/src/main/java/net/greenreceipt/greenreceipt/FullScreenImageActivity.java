package net.greenreceipt.greenreceipt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Util.Helper;


public class FullScreenImageActivity extends ActionBarActivity {
    private static final String TAG = "Touch" ;
    // These matrices will be used to move and zoom image
    private ActionBar actionBar;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new  PointF();
    public static PointF mid = new PointF();

    // We can be in one of these 3 states
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static int mode = NONE;
    Bitmap decodedByte;
    int rId;

    float oldDist;
    ImageView view;
    ProgressDialog spinner;
    private float[] matrixValues = new float[9];
    int picId;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        view = (ImageView) findViewById(R.id.image);
        picId = getIntent().getIntExtra("id",0);
        rId = getIntent().getIntExtra("rId",0);
        index = getIntent().getIntExtra("index",-1);
        String path = getIntent().getStringExtra("path");
        if(path==null && picId > 0)
        {
            byte[] bytes = loadImageBytesFromCache(rId,picId);
            if(bytes!=null)
            {
                float degree = Exif.getOrientation(bytes);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                view.setImageBitmap(Helper.RotateBitmap(bitmap,degree));
                view.invalidate();
            }
            else {
                spinner = ProgressDialog.show(FullScreenImageActivity.this, null, "Loading...");
                new AsyncTask<Integer, Integer, ReceiptImage>() {

                    @Override
                    protected ReceiptImage doInBackground(Integer... params) {
                        Networking n = new Networking();
                        return n.getImageById(params[0]);
                    }

                    @Override
                    protected void onPostExecute(ReceiptImage receiptImage) {
                        super.onPostExecute(receiptImage);
                        spinner.dismiss();
                        byte[] decodedString = Base64.decode(receiptImage.Base64Image, Base64.NO_WRAP);
                        float degree = Exif.getOrientation(decodedString);
                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        view.setImageBitmap(Helper.RotateBitmap(decodedByte,degree));
                        view.invalidate();
                    }
                }.execute(picId);
            }
        }
        else {
            try {
                byte[] decodedString = Model.getInstance().getByteArrayFromImage(path);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                float degree = Exif.getOrientation(decodedString);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                view.setImageBitmap(Helper.RotateBitmap(decodedByte,degree));
                view.invalidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        view.setImageResource(getIntent().getIntExtra("resource", R.drawable.ic_action_camera));
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                finish();
//                return true;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.getInstance().setOnDeleteReceiptImageListener(new Model.OnDeleteReceiptImageListener() {
            @Override
            public void deleteSuccess() {
                spinner.dismiss();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("rid",rId);
                resultIntent.putExtra("id",picId);
                resultIntent.putExtra("index",index);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void deleteFialed(String error) {
                spinner.dismiss();
                Helper.AlertBox(FullScreenImageActivity.this,"Error",error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen_image, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.delete:
                spinner = ProgressDialog.show(this,null,"Deleting...");
                File image = new File(getCacheDir(),"Receipt"+rId+"Image"+picId);
                image.delete();
                Model.getInstance().DeleteReceiptImage(picId);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//        ImageView view = (ImageView) v;
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//            case MotionEvent.ACTION_DOWN:
//
//                savedMatrix.set(matrix);
//                start.set(event.getX(), event.getY());
//                Log.d(TAG, "mode=DRAG");
//                mode = DRAG;
//                break;
//
//            case MotionEvent.ACTION_POINTER_DOWN:
//
//                oldDist = spacing(event);
//                Log.d(TAG, "oldDist=" + oldDist);
//                if (oldDist > 10f) {
//
//                    savedMatrix.set(matrix);
//                    midPoint(mid, event);
//                    mode = ZOOM;
//                    Log.d(TAG, "mode=ZOOM" );
//                }
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//
//                if (mode == DRAG) {
//
//                    matrix.set(savedMatrix);
//                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
//                }
//                else if (mode == ZOOM) {
//
//                    float newDist = spacing(event);
//                    Log.d(TAG, "newDist=" + newDist);
//                    if (newDist > 10f) {
//
//                        matrix.set(savedMatrix);
//                        float scale = newDist / oldDist;
//                        matrix.postScale(scale, scale, mid.x, mid.y);
//                    }
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_POINTER_UP:
//
//                mode = NONE;
//                Log.d(TAG, "mode=NONE" );
//                finish();
//                break;
//        }
//
//        // Perform the transformation
//        view.setImageMatrix(matrix);
//
//        return true; // indicate event was handled
//    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(decodedByte!=null)
            decodedByte.recycle();
    }
    private Uri loadImageFromCache(int ReceiptId, int ImageId)
    {
        try
        {
            String filename = "Receipt"+ReceiptId+"Image"+ ImageId+".jpg";
            File image = new File(getCacheDir(),filename);
            Uri imageUri = Uri.fromFile(image);
//            byte[] imageBytes = new byte[(int)image.length()];
//            FileInputStream fis = new FileInputStream(image);
//            fis.read(imageBytes,0,(int)image.length());
//            fis.close();
            return imageUri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    private byte[] loadImageBytesFromCache(int ReceiptId, int ImageId)
    {
        try
        {
            String filename = "Receipt"+ReceiptId+"Image"+ ImageId+".jpg";
            File image = new File(getFilesDir(),filename);
            byte[] imageBytes = new byte[(int)image.length()];
            FileInputStream fis = new FileInputStream(image);
            fis.read(imageBytes,0,(int)image.length());
            fis.close();
            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
