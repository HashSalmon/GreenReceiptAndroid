package net.greenreceipt.greenreceipt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;

import com.telerik.widget.list.RadListView;

import java.util.ArrayList;


public class PictureListActivity extends ActionBarActivity {
ActionBar actionBar;
    RadListView list;
    ArrayList<String> picturePaths;
    PictureListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
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
        adapter = new PictureListAdapter(picturePaths);
        list.setAdapter(adapter);
        list.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int i, MotionEvent motionEvent) {
                Intent full = new Intent(PictureListActivity.this,FullScreenImageActivity.class);
                full.putExtra("path",picturePaths.get(i));
                startActivity(full);
            }

            @Override
            public void onItemLongClick(int i, MotionEvent motionEvent) {

            }
        });
    }

}
