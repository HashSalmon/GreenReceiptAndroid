package net.greenreceipt.greenreceipt;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Boya on 4/14/15.
 */
public class MyApplication extends Application{
    public void onCreate ()
    {
//        Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });
    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        System.out.println(e.getMessage());
        Intent intent = new Intent(this,LoginActivity.class); // required when starting from Application
        intent.setAction ("android.intent.action.MAIN"); // see step 5.
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);
        System.exit(1); // kill off the crashed app
    }
}
