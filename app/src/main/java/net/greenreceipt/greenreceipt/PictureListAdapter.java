package net.greenreceipt.greenreceipt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by Boya on 3/11/15.
 */
public class PictureListAdapter extends ListViewAdapter {
    List pictures;
    public PictureListAdapter(List paths) {
        super(paths);
        this.pictures = paths;
    }
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.picturelistitem, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        PictureViewHolder viewHolder = (PictureViewHolder)holder;
        String picturePath = (String)getItems().get(position);
        try {
            byte[] imageBytes = Model.getInstance().getByteArrayFromImage(picturePath);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
            viewHolder.pic.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static class PictureViewHolder extends ListViewHolder {

        ImageView pic;

        public PictureViewHolder(View itemView) {
            super(itemView);

            pic = (ImageView)itemView.findViewById(R.id.picture);
        }
    }

}
