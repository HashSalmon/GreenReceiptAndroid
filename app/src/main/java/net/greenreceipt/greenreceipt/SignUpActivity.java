package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class SignUpActivity extends ActionBarActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    ProgressDialog spinner;
    private ActionBar actionBar;
    EditText email;
    EditText firstname;
    EditText lastname;
    EditText password;
    EditText confirm;
    EditText username;

    public static final String REG_ID = "regId";
    GoogleCloudMessaging gcm;
    String regId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        email = (EditText) findViewById(R.id.emailField);
        firstname = (EditText) findViewById(R.id.firstNameField);
        lastname = (EditText) findViewById(R.id.lastNameField);
        password = (EditText) findViewById(R.id.passwordField);
        confirm = (EditText) findViewById(R.id.comfirmField);
        username = (EditText) findViewById(R.id.usernameField);
        Button button = (Button) findViewById(R.id.signupButton);

        Model.getInstance().setRegisterUserListener(new Model.RegisterUserListener() {
            @Override
            public void userRegistered() {
               spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setTitle("Congratulations!");
                builder.setMessage("You are registered! Please Login with your new account!");
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            @Override
            public void userRegisterFailed() {
                spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setTitle("Oops...");
                builder.setMessage("Registration failed! Please try again.");
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        if(button != null)
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean canRegister = checkInput();
                    spinner = ProgressDialog.show(SignUpActivity.this, null, "Registering...");
                    if(checkPlayServices()){

                            registerInBackground();

                    }
                    else
                    {
                        Model.getInstance().Register(email.getText().toString(),firstname.getText().toString(),
                            lastname.getText().toString(),password.getText().toString(),confirm.getText().toString(),username.getText().toString(),regId);

                    }

//                    Model.getInstance().Register(email.getText().toString(),firstname.getText().toString(),
//                            lastname.getText().toString(),password.getText().toString(),confirm.getText().toString(),username.getText().toString(),regId);
                }
            });

    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
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
                    storeRegIdinSharedPref(SignUpActivity.this,regId);
                    Model.getInstance().Register(email.getText().toString(),firstname.getText().toString(),
                            lastname.getText().toString(),password.getText().toString(),confirm.getText().toString(),username.getText().toString(),regId);
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

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    private boolean checkInput()
    {
        boolean canRegister = true;
        if(!password.getText().toString().equals(confirm.getText().toString()))
            canRegister = false;
        if(password.getText().toString().length()<8)
            canRegister = false;

        return canRegister;
    }
}
