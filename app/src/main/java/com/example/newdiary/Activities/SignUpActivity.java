package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newdiary.Data.SharedPrefs;
import com.example.newdiary.R;
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

    private AlertDialog.Builder signInAlertDialogBuilder;
    private AlertDialog signInDialog;

    private String email;
    private String password;

    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditTextId);
        passwordEditText = findViewById(R.id.passwordEditTextId);
        signInButton = findViewById(R.id.signInButtonId);
        signUpOptionButton = findViewById(R.id.signUpOptionButtonId);

        mAuth = FirebaseAuth.getInstance();

        sharedPrefs = new SharedPrefs(SignUpActivity.this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                signIn(email, password);
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

        if (sharedPrefs.getLoggedInState() && (mAuth.getCurrentUser() != null)) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            if (currentUser != null) {
                Toast.makeText(getApplicationContext(), "curr user: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
            finish();
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
                                sharedPrefs.setLoggedInState(true);
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
                                sharedPrefs.setLoggedInState(true);
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

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPass = confirmPassEditText.getText().toString();

                if (password.equals(confirmPass)) {
                    createAccount(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

}
