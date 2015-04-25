package net.greenreceipt.greenreceipt;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CheckReturnService extends IntentService {



    public CheckReturnService() {
        super("CheckReturnService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d("Debug","Check service started");
        Model.getInstance().setReturnReceiptListener(new Model.ReturnReceiptListener() {
            @Override
            public void returnDetected() {
                //do notification here
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CheckReturnService.this);
                mBuilder.setSmallIcon(R.drawable.greenreceiptsmall);
                mBuilder.setContentTitle("Return Alert");
                mBuilder.setContentText("You have upcoming return deadlines");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Intent resultIntent = new Intent(getBaseContext(), ListReceiptActivity.class);
                resultIntent.putExtra(Model.RECEIPT_FILTER, Model.SHOW_RETURN_RECEIPTS);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(CheckReturnService.this);
                stackBuilder.addParentStack(ListReceiptActivity.class);

                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(Model.RETURN_ALERT_NOTIFICATION, mBuilder.build());
            }
        });
        Model.getInstance().GetReturnReceipts();

    }
}
