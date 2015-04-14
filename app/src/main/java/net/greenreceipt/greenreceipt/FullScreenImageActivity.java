package net.greenreceipt.greenreceipt;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;


public class FullScreenImageActivity extends ActionBarActivity implements View.OnTouchListener {
    private static final String TAG = "Touch" ;
    // These matrices will be used to move and zoom image
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

    float oldDist;
    ImageView view;
    ProgressDialog spinner;
    private float[] matrixValues = new float[9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        view = (ImageView) findViewById(R.id.image);
        int picId = getIntent().getIntExtra("id",0);
        String path = getIntent().getStringExtra("path");
        if(path==null && picId > 0)
        {
            spinner = ProgressDialog.show(FullScreenImageActivity.this, null, "Loading...");
            new AsyncTask<Integer,Integer,ReceiptImage>(){

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
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    view.setImageBitmap(decodedByte);
                    view.invalidate();
                }
            }.execute(picId);
        }
        else {
            try {
                byte[] bytes = Model.getInstance().getByteArrayFromImage(path);
                decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                view.setImageBitmap(decodedByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        view.setImageResource(getIntent().getIntExtra("resource", R.drawable.ic_action_camera));
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {

                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {

                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }
                else if (mode == ZOOM) {

                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {

                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                finish();
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

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
}
