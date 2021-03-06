package com.oliviaxie.simplediary.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.oliviaxie.simplediary.Data.DatabaseHandler;
import com.oliviaxie.simplediary.Models.Entry;
import com.oliviaxie.simplediary.R;

import java.text.SimpleDateFormat;

public class NewEntryActivity extends AppCompatActivity {

    private String entryDate;
    private long entryDateMillis;
    private long entryId;
    private TextView dateTextView;
    private EditText titleEditText;
    private EditText entryEditText;
    private DatabaseHandler dbHandler;
    private ImageButton backButton;
    private SharedPreferences prefs;
    private boolean backupOption;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setting color theme
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String themePref = prefs.getString("theme", "blue");
        setColorTheme(themePref);

        backupOption = prefs.getBoolean("backup?", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        entryDateMillis = getIntent().getLongExtra("date", 0);
        entryDate = dateFormat.format(entryDateMillis);

        dateTextView = findViewById(R.id.entryDateId);
        dateTextView.setText(entryDate);

        titleEditText = findViewById(R.id.titleEditTextId);
        entryEditText = findViewById(R.id.entryEditTextId);
        backButton = findViewById(R.id.newBackButtonId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHandler = new DatabaseHandler(NewEntryActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    // Opening Search alert dialog from menu Search button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_entry) {

            saveEntryToDb();
        }

        return super.onOptionsItemSelected(item);
    }

    // Save new entry to database
    private void saveEntryToDb() {

        Entry entry = new Entry();
        String title = titleEditText.getText().toString();
        String text = entryEditText.getText().toString();

        if (title.length() == 0 || text.length() == 0) {

            Toast.makeText(getApplicationContext(), "Entry or title cannot be empty.", Toast.LENGTH_LONG).show();

        } else {

            entryId = System.currentTimeMillis();
            entry.setEntryId(entryId);

            entry.setTitle(title);
            entry.setText(text);
            entry.setDate(entryDateMillis);

            dbHandler.addEntry(entry);
            dbHandler.close();

            // Clear the form
            titleEditText.setText("");
            entryEditText.setText("");
            dateTextView.setText("");

            // Backup entry to Firebase if backup option is enabled
            if (backupOption) {
                backupEntryToFirebase(entry);
            }

            // Exit new entry activity
            finish();
        }
    }

    public void backupEntryToFirebase(Entry entry) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();

        if (currUser != null) {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            String currUserUID = currUser.getUid();

            String entryId = Long.toString(entry.getEntryId());

            firebaseDatabase.getReference().child(currUserUID).child(entryId)
                    .child("title")
                    .setValue(entry.getTitle());

            firebaseDatabase.getReference().child(currUserUID).child(entryId)
                    .child("entryText")
                    .setValue(entry.getText());

            firebaseDatabase.getReference().child(currUserUID).child(entryId)
                    .child("date")
                    .setValue(entry.getDate());
        }
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
