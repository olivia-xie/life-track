package com.oliviaxie.simplediary.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oliviaxie.simplediary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Button signUpOptionButton;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private AlertDialog.Builder signInAlertDialogBuilder;
    private AlertDialog signInDialog;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Setting color theme
        String themePref = preferences.getString("theme", "blue");
        setColorTheme(themePref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditTextId);
        passwordEditText = findViewById(R.id.passwordEditTextId);
        signInButton = findViewById(R.id.signInButtonId);
        signUpOptionButton = findViewById(R.id.signUpOptionButtonId);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emailEditText.getText() != null &&
                        passwordEditText.getText() != null) {

                    email = emailEditText.getText().toString().trim();
                    password = passwordEditText.getText().toString().trim();

                    signIn(email, password);
                }

            }
        });

        signUpOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is already logged in
        if (currentUser != null) {
            // Checks if lock screen has been enabled by user and starts lock screen activity if true
            boolean passcodeOption = preferences.getBoolean("passcode?", false);
            if (passcodeOption) {
                Intent passcodeIntent = new Intent(SignUpActivity.this, LockScreenActivity.class);
                startActivity(passcodeIntent);
                finish();
            } else {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void createAccount(String emailAddress, String password) {

        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currUser = mAuth.getCurrentUser();
                            if (currUser != null) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String emailAddress, String password) {

        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currUser = mAuth.getCurrentUser();
                            if (currUser != null) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSignUpDialog() {
        signInAlertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.sign_up_dialog_view, null);

        signInAlertDialogBuilder.setView(v);
        signInDialog = signInAlertDialogBuilder.create();
        signInDialog.show();

        final EditText emailEditText, passwordEditText, confirmPassEditText;
        Button signUpButton;

        emailEditText = v.findViewById(R.id.signUpEmailEditTextId);
        passwordEditText = v.findViewById(R.id.signUpPasswordEditTextId);
        confirmPassEditText = v.findViewById(R.id.confirmPasswordEditTextId);
        signUpButton = v.findViewById(R.id.signInButtonId);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable email = emailEditText.getText();
                Editable password = passwordEditText.getText();
                Editable confirmPass = confirmPassEditText.getText();

                if (password.equals(confirmPass) && email != null && password != null &&
                        confirmPass != null) {
                    createAccount(email.toString().trim(), password.toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "Error signing up.", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    public void setColorTheme(String themePref) {

        switch (themePref) {
            case "blue":
                setTheme(R.style.BlueTheme);
                break;
            case "pink":
                setTheme(R.style.PinkTheme);
                break;
            case "purple":
                setTheme(R.style.PurpleTheme);
                break;
            case "yellow":
                setTheme(R.style.YellowTheme);
                break;
            case "green":
                setTheme(R.style.GreenTheme);
                break;
            case "orange":
                setTheme(R.style.OrangeTheme);
                break;
        }
    }
}
