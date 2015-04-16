package net.greenreceipt.greenreceipt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MotionEvent;

import com.telerik.widget.list.RadListView;

import java.io.IOException;
import java.util.ArrayList;


public class PictureListActivity extends ActionBarActivity {
ActionBar actionBar;
    ProgressDialog spinner;
    RadListView list;
    ArrayList<String> picturePaths;
    ReceiptImage[] images;
    PictureListAdapter adapter;
    ArrayList<byte[]> imageBytes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        int rId = getIntent().getIntExtra("id",0);
        if(rId > 0)
        {
            Model.getInstance().setGetReceiptImageListener(new Model.GetReceiptImageListener() {
                @Override
                public void onGetImageSuccess(ReceiptImage[] images) {
                    PictureListActivity.this.images = images;
                    spinner.dismiss();
                    if(images!=null)
                    {
                        for(ReceiptImage i : images)
                        {
                            byte[] decodedBytes = Base64.decode(i.Base64Image, Base64.NO_WRAP);
                            imageBytes.add(decodedBytes);
                            adapter.setItems(imageBytes);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onGetImageFailed(String error) {
                    spinner.dismiss();
                }
            });
            spinner = ProgressDialog.show(PictureListActivity.this, null, "Loading...");
            if(imageBytes.isEmpty())
            Model.getInstance().GetReceiptImages(rId);
        }
        picturePaths = getIntent().getStringArrayListExtra("paths");

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        list = (RadListView) findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        list.setLayoutManager(gridLayoutManager);

        if(picturePaths!=null)
        {
            for(String s: picturePaths)
            {
                try {
                    byte[] bytes = Model.getInstance().getByteArrayFromImage(s);
                    imageBytes.add(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter = new PictureListAdapter(imageBytes);
        list.setAdapter(adapter);
        list.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int i, MotionEvent motionEvent) {
                Intent full = new Intent(PictureListActivity.this,FullScreenImageActivity.class);
//
                if(picturePaths==null || i >= picturePaths.size())
                {
                    //Already on server, attach id
                    full.putExtra("id",images[i].Id);
                }
                else
                {
                    //Not yet uploaded, attach path
                    full.putExtra("path",picturePaths.get(i));
                }
                startActivity(full);
            }

            @Override
            public void onItemLongClick(int i, MotionEvent motionEvent) {

            }
        });
    }

}
