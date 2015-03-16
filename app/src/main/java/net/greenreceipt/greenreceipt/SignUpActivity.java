package net.greenreceipt.greenreceipt;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class SignUpActivity extends Activity {

    ProgressDialog spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        final EditText email = (EditText) findViewById(R.id.emailField);
        final EditText firstname = (EditText) findViewById(R.id.firstNameField);
        final EditText lastname = (EditText) findViewById(R.id.lastNameField);
        final EditText password = (EditText) findViewById(R.id.passwordField);
        final EditText confirm = (EditText) findViewById(R.id.comfirmField);
        final EditText username = (EditText) findViewById(R.id.usernameField);
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
                    spinner = ProgressDialog.show(SignUpActivity.this, null, "Registering...");
                    Model.getInstance().Register(email.getText().toString(),firstname.getText().toString(),
                            lastname.getText().toString(),password.getText().toString(),confirm.getText().toString(),username.getText().toString());
                }
            });

    }

}
