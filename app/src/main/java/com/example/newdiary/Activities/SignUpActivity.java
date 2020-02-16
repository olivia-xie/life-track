package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private Button signUpButton;
    private Button signInOptionButton;

    private AlertDialog.Builder signInAlertDialogBuilder;
    private AlertDialog signInDialog;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditTextId);
        passwordEditText = findViewById(R.id.signInEmailEditTextId);
        signUpButton = findViewById(R.id.signUpButtonId);
        signInOptionButton = findViewById(R.id.signInOptionButtonId);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                createAccount(email, password);
            }
        });

        signInOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInDialog();
            }
        });
    }

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(getApplicationContext(), "curr user: " + currentUser.getEmail(), Toast.LENGTH_SHORT);
    }

    private void createAccount(String emailAddress, String password) {

        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "sign up successful", Toast.LENGTH_LONG);
                            FirebaseUser currUser = mAuth.getCurrentUser();
                            finish();
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
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSignInDialog() {
        signInAlertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.sign_in_dialog_view, null);

        signInAlertDialogBuilder.setView(v);
        signInDialog = signInAlertDialogBuilder.create();
        signInDialog.show();

        final EditText emailEditText, passwordEditText;
        Button signInButton;

        emailEditText = v.findViewById(R.id.signInEmailEditTextId);
        passwordEditText = v.findViewById(R.id.signInPasswordEditTextId);
        signInButton = v.findViewById(R.id.signInButtonId);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

}
