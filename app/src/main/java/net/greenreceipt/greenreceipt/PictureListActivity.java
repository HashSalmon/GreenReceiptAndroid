package net.greenreceipt.greenreceipt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MotionEvent;

import com.telerik.widget.list.RadListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    int rId;
    ArrayList<Integer> imageIds;
    static int IS_DELETED = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        if(savedInstanceState==null)
        {
            rId = getIntent().getIntExtra("id", 0);
            picturePaths = getIntent().getStringArrayListExtra("paths");
            imageIds = getIntent().getIntegerArrayListExtra("imageIds");
        }
        Model.getInstance().setGetReceiptImageListener(new Model.GetReceiptImageListener() {
            @Override
            public void onGetImageSuccess(ReceiptImage[] images) {
                PictureListActivity.this.images = images;
                if(spinner!=null)
                spinner.dismiss();
                if (images != null) {
                    for (ReceiptImage i : images) {
                        byte[] decodedBytes = Base64.decode(i.Base64Image, Base64.NO_WRAP);
                        cachePicture(decodedBytes, i.ReceiptId, i.Id);
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


        if (picturePaths != null) {
            for (String s : picturePaths) {
                try {
                    byte[] bytes = Model.getInstance().getByteArrayFromImage(s);
                    imageBytes.add(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        if (rId > 0 && imageIds!= null && imageIds.size()>0)//already cached, load from cache
        {
            for (Integer imageId : imageIds) {
                byte[] bytes = loadImageBytesFromCache(rId, imageId);
                if (bytes != null) {
                    imageBytes.add(bytes);

                }
//                else {
//
//                }
            }
//            adapter.setItems(imageBytes);
        }
        adapter = new PictureListAdapter(imageBytes);
        if (imageBytes.isEmpty()) {
            spinner = ProgressDialog.show(PictureListActivity.this, null, "Loading...");

            Model.getInstance().GetReceiptImages(rId);
        }


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        list = (RadListView) findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        adapter = new PictureListAdapter(imageBytes);
        list.setLayoutManager(gridLayoutManager);
        list.setAdapter(adapter);
        list.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int i, MotionEvent motionEvent) {
                Intent full = new Intent(PictureListActivity.this, FullScreenImageActivity.class);
//
                if (picturePaths == null || i >= picturePaths.size()) {
                    //Already on server, attach id
                    if(images!=null)
                        full.putExtra("id", images[i].Id);
                    else
                        full.putExtra("id",imageIds.get(i));
                    full.putExtra("rId", rId);
                } else {
                    //Not yet uploaded, attach path
                    full.putExtra("path", picturePaths.get(i));
                }
                full.putExtra("index",i);
                startActivityForResult(full, IS_DELETED);
            }

            @Override
            public void onItemLongClick(int i, MotionEvent motionEvent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IS_DELETED)
        {
            int index = data.getIntExtra("index",-1);
            int recieptId = data.getIntExtra("rId",0);
            int imageId = data.getIntExtra("id",0);
            if(index > -1) {
                imageBytes.remove(index);
                adapter.setItems(imageBytes);
                adapter.notifyDataSetChanged();
            }
            if(recieptId > 0 && imageId > 0)
            {
                File image = new File(getFilesDir(),"Receipt"+recieptId+"Image"+imageId+".jpg");
                image.delete();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("rId", rId);
        outState.putStringArrayList("picturePaths",picturePaths);
        outState.putIntegerArrayList("imageIds",imageIds);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rId = savedInstanceState.getInt("rId");
        picturePaths = savedInstanceState.getStringArrayList("picturePaths");
        imageIds = savedInstanceState.getIntegerArrayList("imageIds");
    }

    private String cachePicture(byte[] imageBytes, int receiptId, int imageId)
    {
        try
        {
            String FILENAME = "Receipt"+receiptId+"Image"+imageId+".jpg";

            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(imageBytes);
            fos.close();

            File file = new File(getFilesDir(),FILENAME);
            return file.getPath();

        }
        catch (Exception e)
        {

        }
        return null;
    }
    private byte[] loadImageBytesFromCache(int ReceiptId, int ImageId)
    {
        try
        {
            String filename = "Receipt"+ReceiptId+"Image"+ ImageId+".jpg";
            File image = new File(getFilesDir(),filename);
            boolean canRead = image.canRead();
            Uri imageUri = Uri.fromFile(image);
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
