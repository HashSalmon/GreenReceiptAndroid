package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import abbyy.ocrsdk.android.ResultsActivity;


public class NewReceiptActivity extends ActionBarActivity {


    final static int RESULT_CAPTURE = 10;
    ImageView icon;
    ImageView cam_icon;
    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;

    private String resultUrl = "result.txt";
    ActionBar actionBar;
    Bitmap myBit;//use this to hold image
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        Toolbar actionBar = (Toolbar) findViewById(R.id.action_bar);
//        setSupportActionBar(actionBar);
//        actionBar.set
        icon = (ImageView) findViewById(R.id.icon);
//        ImageView code = (ImageView) findViewById(R.id.code);
        icon.setImageResource(R.drawable.ic_action_new);
//        code.setImageResource(R.drawable.code);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, RESULT_CAPTURE);
                Intent intent = new Intent(getBaseContext(), ManualReceiptActivity.class);
                startActivity(intent);
            }
        });
        cam_icon = (ImageView) findViewById(R.id.cam_icon);
        cam_icon.setImageResource(R.drawable.ic_action_camera);
        cam_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        TextView greenReceiptNumber = (TextView) findViewById(R.id.greenReceiptNumber);
        greenReceiptNumber.setText(Model.getInstance()._currentUser.UserAccountId);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_CAPTURE && null != data) {
//            Uri image = data.getData();//uri to picture just taken.
//         }
//    }





    public static final int MEDIA_TYPE_IMAGE = 1;

    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GreenReceipt");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg" );

        return mediaFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        String imageFilePath = null;

        switch (requestCode) {
            case TAKE_PICTURE:
                imageFilePath = getOutputMediaFileUri().getPath();
                break;

        }

        //Remove output file
        deleteFile(resultUrl);

        Intent results = new Intent( this, ResultsActivity.class);
        results.putExtra("IMAGE_PATH", imageFilePath);
        results.putExtra("RESULT_PATH", resultUrl);
        startActivity(results);
    }

}

