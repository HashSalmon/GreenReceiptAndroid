package net.greenreceipt.greenreceipt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.util.List;

import Util.Helper;

/**
 * Created by Boya on 3/11/15.
 */
public class PictureListAdapter extends ListViewAdapter {
    List pictures;
    public PictureListAdapter(List imageBytes) {
        super(imageBytes);
        this.pictures = imageBytes;
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
        byte[] bytes = (byte[]) getItems().get(position);
        int degree = Exif.getOrientation(bytes);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        bitmap = Helper.RotateBitmap(bitmap,degree);

            viewHolder.pic.setImageBitmap(bitmap);


    }
    public static class PictureViewHolder extends ListViewHolder {

        ImageView pic;

        public PictureViewHolder(View itemView) {
            super(itemView);

            pic = (ImageView)itemView.findViewById(R.id.picture);
            pic.setPadding(2,0,2,0);
        }
    }


}
