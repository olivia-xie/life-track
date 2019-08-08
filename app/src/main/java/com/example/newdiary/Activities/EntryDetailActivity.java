package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;

public class EntryDetailActivity extends AppCompatActivity {

    private Entry clickedEntry;
    private int entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clickedEntry = (Entry) getIntent().getSerializableExtra("clickedEntry");
        entryId = clickedEntry.getEntryId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;

    }

    // Opening Search alert dialog from menu Search button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_entry) {

            AlertDialog.Builder alert = new AlertDialog.Builder(EntryDetailActivity.this);
            alert.setTitle("Delete?");
            alert.setMessage("Are you sure you want to delete this entry?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DatabaseHandler dba = new DatabaseHandler(getApplicationContext());
                    dba.deleteEntry(entryId);

                    startActivity(new Intent(EntryDetailActivity.this, MainActivity.class));

                    //remove this activity from activity stack
                    EntryDetailActivity.this.finish();


                }
            });

            alert.setNegativeButton("No", null);

            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }


}