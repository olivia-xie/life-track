package com.example.newdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;

public class NewEntryActivity extends AppCompatActivity {

    private String entryDate;
    private TextView dateTextView;
    private EditText titleEditText;
    private EditText entryEditText;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        entryDate = getIntent().getStringExtra("date");

        dateTextView = findViewById(R.id.entryDateId);
        dateTextView.setText(entryDate);

        titleEditText = findViewById(R.id.titleEditTextId);
        entryEditText = findViewById(R.id.entryEditTextId);

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
        String title = titleEditText.getText().toString().trim();
        String text = entryEditText.getText().toString().trim();

        if (title.length() == 0 || text.length() == 0) {

            Toast.makeText(getApplicationContext(), "Entry or title cannot be empty.", Toast.LENGTH_LONG).show();

        } else {

            entry.setTitle(title);
            entry.setText(text);

            dbHandler.addEntry(entry);
            dbHandler.close();

            // Clear the form
            titleEditText.setText("");
            entryEditText.setText("");

            // Exit new entry activity
            finish();
        }
    }

}
