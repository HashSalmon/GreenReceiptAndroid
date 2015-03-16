package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;


public class LoginActivity extends Activity {

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
            Model._currentUser = new User();
            Model._currentUser.FirstName = pref.getString("FirstName","");
            Model._currentUser.LastName = pref.getString("LastName","");
            Model._currentUser.Email = pref.getString("Email","");
            Intent checkReturnIntent = new Intent(this,CheckReturnService.class);
            startService(checkReturnIntent);
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

            Model.getInstance().setOnLoginListener(new Model.OnLoginListener() {
                @Override
                public void onLoginSuccess() {
                    spinner.dismiss();
                    if(keepUser.isChecked())//keep user session if asked
                    {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("GreenReceipt", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token", Model._token);
                        editor.putString("FirstName", Model._currentUser.FirstName);
                        editor.putString("LastName", Model._currentUser.LastName);
                        editor.putString("Email", Model._currentUser.Email);
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
            ImageView logo = (ImageView) findViewById(R.id.logo);
            logo.setImageResource (R.drawable.logo);
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
}
