package net.greenreceipt.greenreceipt;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import abbyy.ocrsdk.android.MainActivity;


public class NewReceiptActivity extends Activity {


    final static int RESULT_CAPTURE = 10;
    ImageView icon;
    ImageView cam_icon;
    Bitmap myBit;//use this to hold image
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        icon = (ImageView) findViewById(R.id.icon);
        ImageView code = (ImageView) findViewById(R.id.code);
        icon.setImageResource(R.drawable.ic_action_new);
        code.setImageResource(R.drawable.code);
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
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAPTURE && null != data) {
            Uri image = data.getData();//uri to picture just taken.
         }
    }
}

