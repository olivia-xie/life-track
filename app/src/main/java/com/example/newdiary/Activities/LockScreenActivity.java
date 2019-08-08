package com.example.newdiary.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newdiary.Data.SharedPrefs;
import com.example.newdiary.R;

public class LockScreenActivity extends AppCompatActivity {

    private EditText password;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        password = findViewById(R.id.passwordEnterId);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String actualPIN = getIntent().getStringExtra("actualPIN");
                String enteredPIN = password.getText().toString();

                if (enteredPIN.equals(actualPIN)) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect PIN", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
