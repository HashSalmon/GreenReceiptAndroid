package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Util.Helper;


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
    String validationError = "";

    public static final String REG_ID = "regId";
    GoogleCloudMessaging gcm;
    String regId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //actionbar setup
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        email = (EditText) findViewById(R.id.emailField);
        firstname = (EditText) findViewById(R.id.firstNameField);
        lastname = (EditText) findViewById(R.id.lastNameField);
        password = (EditText) findViewById(R.id.passwordField);
        confirm = (EditText) findViewById(R.id.comfirmField);
        username = (EditText) findViewById(R.id.usernameField);
        Button button = (Button) findViewById(R.id.signupButton);
        //set listener
        Model.getInstance().setRegisterUserListener(new Model.RegisterUserListener() {
            @Override
            public void userRegistered() {
                if(spinner!=null)
               spinner.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
                        login.putExtra("email",email.getText().toString());
                        startActivity(login);
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
                builder.setMessage("Email already in use.");
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        //Handle sign up button
        if(button != null)
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean canRegister = checkInput();
                    if(canRegister) {
                        spinner = ProgressDialog.show(SignUpActivity.this, null, "Registering...");
                        if (checkPlayServices()) {

                            registerInBackground();

                        } else {
                            Model.getInstance().Register(email.getText().toString(), firstname.getText().toString(),
                                    lastname.getText().toString(), password.getText().toString(), confirm.getText().toString(), username.getText().toString(), regId);

                        }
                    }
                    else
                    {
                        Helper.AlertBox(SignUpActivity.this,"Invalid input",validationError);
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

    /**
     * Validation
     * @return
     */
    private boolean checkInput()
    {
        boolean canRegister = true;
        if(email.getText().toString().isEmpty())
        {
            canRegister = false;
            validationError = "Email is required!";
            return canRegister;
        }
        if(username.getText().toString().isEmpty())
        {
            canRegister = false;
            validationError = "Username is required!";
            return canRegister;
        }
        if(firstname.getText().toString().isEmpty())
        {
            canRegister = false;
            validationError = "Firstname is required!";
            return canRegister;
        }
        if(lastname.getText().toString().isEmpty())
        {
            canRegister = false;
            validationError = "Lastname is required!";
            return canRegister;
        }
        if(!password.getText().toString().equals(confirm.getText().toString())) {
            canRegister = false;
            validationError = "Passwords do not match!";
            return canRegister;
        }

        String pattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).{8,20})";

        Pattern r = Pattern.compile(pattern);
        Matcher match = r.matcher(password.getText().toString());

        if (!match.matches())
        {
            validationError = "Passwords must be 8-20 characters in length.\nMust contain at least one number, one capital,one lower case, and one special character";
            canRegister = false;
            return canRegister;

        }
        pattern = "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$";
        r = Pattern.compile(pattern);
        match = r.matcher(email.getText().toString());
        if(!match.find())
        {
            canRegister = false;
            validationError = "Please enter a valid email!";
            return canRegister;
        }
;        return canRegister;
    }
}
