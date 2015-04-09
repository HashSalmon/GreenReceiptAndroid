package Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.greenreceipt.greenreceipt.R;

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

}
