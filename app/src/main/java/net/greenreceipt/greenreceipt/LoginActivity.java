package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class LoginActivity extends Activity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String REG_ID = "regId";
    GoogleCloudMessaging gcm;
    String regId = "";

    private Button login;
    private EditText username;
    private EditText password;
    private ProgressDialog spinner;
    private CheckBox keepUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("GreenReceipt", 0); // 0 - for private mode
        if(pref.getString("token","")!="")//user is logged in
        {
            Model._token = pref.getString("token","");
            Model.getInstance()._currentUser = new User();
            Model.getInstance()._currentUser.FirstName = pref.getString("FirstName","");
            Model.getInstance()._currentUser.LastName = pref.getString("LastName","");
            Model.getInstance()._currentUser.Email = pref.getString("Email","");
            Model.getInstance()._currentUser.UserAccountId=pref.getString("Account","");
            Intent checkReturnIntent = new Intent(this,CheckReturnService.class);
            startService(checkReturnIntent);
            if(checkPlayServices())
            {
                registerInBackground();
            }
            Intent home = new Intent(getBaseContext(),HomeActivity.class);
            startActivity(home);
            finish();
        }

//        Model instance = Model.getInstance ();

//        if(instance.getReceiptFile() == null){
//            File file = new File(getFilesDir(), "ReceiptFile1.txt");
//            instance.setReceiptFile(file);
//        }

//        if (instance.userLoggedIn())
//        {
//            Intent home = new Intent(getBaseContext(),HomeActivity.class);
//            startActivity(home);
//            finish();
//        }
        else
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            if(checkPlayServices())
            {
                registerInBackground();
            }
            Model.getInstance().setOnLoginListener(new Model.OnLoginListener() {
                @Override
                public void onLoginSuccess() {
                    spinner.dismiss();
                    Model.getInstance().GetUserAccountId();
                    Model.getInstance().setGetAccountIdListener(new Model.GetAccountIdListener() {
                        @Override
                        public void onGetAccountIdSuccess(String id) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("GreenReceipt", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Account",Model.getInstance()._currentUser.UserAccountId);
                            editor.commit();
                        }
                    });
                    if(keepUser.isChecked())//keep user session if asked
                    {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("GreenReceipt", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token", Model._token);
                        editor.putString("FirstName", Model.getInstance()._currentUser.FirstName);
                        editor.putString("LastName", Model.getInstance()._currentUser.LastName);
                        editor.putString("Email", Model.getInstance()._currentUser.Email);

                        editor.commit();
                    }
                    Intent checkReturnIntent = new Intent(LoginActivity.this,CheckReturnService.class);
                    startService(checkReturnIntent);
                    Intent home = new Intent(getBaseContext(),HomeActivity.class);
                    startActivity(home);
                    finish();

                }

                @Override
                public void onLoginFailed(String error) {
                    spinner.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Authentication Error");
                    builder.setMessage("Login failed!");
                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
//            ImageView logo = (ImageView) findViewById(R.id.logo);
//            logo.setImageResource (R.drawable.logo);
            login = (Button) findViewById(R.id.loginButton);
            username = (EditText) findViewById(R.id.usernameField);
            password = (EditText) findViewById(R.id.passwordField);
            keepUser = (CheckBox) findViewById(R.id.keeplogin);
            Button signup = (Button) findViewById(R.id.signupButton);

            if(login!=null)
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinner = ProgressDialog.show(LoginActivity.this, null, "Logging in...");
                    Model.getInstance().Login(username.getText().toString(), password.getText().toString());
//                    Intent home = new Intent(getBaseContext(),HomeActivity.class);
//                    startActivity(home);
//                    finish();

                }
            });

            if(signup != null)
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(),SignUpActivity.class);
                        startActivity(intent);

                    }
                });
        }
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    // AsyncTask to register Device in GCM Server
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging
                                .getInstance(getApplicationContext());
                    }
                    regId = gcm
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(LoginActivity.this,regId);
                    Model.getInstance().UpdatePushNotificationId(regId);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }
    // Store  RegId and Email entered by User in SharedPref
    private void storeRegIdinSharedPref(Context context, String regId) {
        SharedPreferences prefs = getSharedPreferences("GreenReceipt",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
//        editor.putString(EMAIL_ID, emailID);
        editor.commit();
//        storeRegIdinServer();

    }

}
