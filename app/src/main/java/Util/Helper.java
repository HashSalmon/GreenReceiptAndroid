package Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

import net.greenreceipt.greenreceipt.R;

import java.io.IOException;

/**
 * Created by Boya on 2/12/15.
 */
public class Helper
{
    public static void AlertBox(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void PictureBox(Context context, String title, int resource)
    {

        Dialog dialog = new Dialog(context);
        dialog.setTitle("Receipt Image");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.image_dialog);
        ImageView large = (ImageView) dialog.findViewById(R.id.image);
        large.setImageResource(resource);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }
    public static Bitmap rotatePicture(String filePath, Bitmap bitmap)
    {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = BitmapFactory.decodeFile(filePath);
                return RotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = BitmapFactory.decodeFile(filePath);
                return RotateBitmap(bitmap, 180);
            // etc.
        }
        return bitmap;
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
