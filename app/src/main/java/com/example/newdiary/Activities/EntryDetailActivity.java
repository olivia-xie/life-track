package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EntryDetailActivity extends AppCompatActivity {

    private Entry clickedEntry;
    private long entryId;
    private Entry editedEntry;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private AlertDialog calendarDialog;
    private AlertDialog.Builder calendarAlertDialogBuilder;
    private CalendarView calendarView;
    private long editedDate;

    private TextView detailDate, detailTitle, detailText;
    private ImageButton backButton;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        clickedEntry = (Entry) getIntent().getSerializableExtra("clickedEntry");
        entryId = clickedEntry.getEntryId();

        detailDate = findViewById(R.id.detailDateId);
        detailText = findViewById(R.id.detailTextId);
        detailTitle = findViewById(R.id.detailTitleId);
        backButton = findViewById(R.id.detailBackButtonId);

        detailTitle.setText(clickedEntry.getTitle());
        detailText.setText(clickedEntry.getText());
        detailDate.setText(dateFormat.format(clickedEntry.getDate()));

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // Back button action
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;

    }

    // Opening delete alert dialog from menu delete button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Show alert dialog confirming deletion of selected entry
        if (id == R.id.delete_entry) {

            AlertDialog.Builder alert = new AlertDialog.Builder(EntryDetailActivity.this);
            alert.setTitle("Delete?");
            alert.setMessage("Are you sure you want to delete this entry? Backups will be deleted as well if you have selected the backup option.");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DatabaseHandler dba = new DatabaseHandler(getApplicationContext());
                    dba.deleteEntry(entryId);

                    //remove this activity from activity stack
                    EntryDetailActivity.this.finish();

                    if (prefs.getBoolean("backup?", false)) {
                        deleteEntryFromFirebase(entryId);
                    }
                }
            });

            alert.setNegativeButton("No", null);

            alert.show();
        }

        // Creates dialog for user to edit their entry
        if (id == R.id.edit_entry) {

            AlertDialog.Builder editAlertDialogBuilder = new AlertDialog.Builder(EntryDetailActivity.this);

            View view = getLayoutInflater().inflate(R.layout.edit_dialog_view, null);

            editAlertDialogBuilder.setView(view);
            final Dialog editDialog = editAlertDialogBuilder.create();
            editDialog.show();

            final Button dateEdit = view.findViewById(R.id.editDateButtonId);
            final EditText titleEdit = view.findViewById(R.id.editTitleId);
            final EditText entryEdit = view.findViewById(R.id.editEntryId);
            final Button saveEditButton = view.findViewById(R.id.saveEditButtonId);

            dateEdit.setText(dateFormat.format(clickedEntry.getDate()));
            titleEdit.setText(clickedEntry.getTitle());
            entryEdit.setText(clickedEntry.getText());

            // Show calendar dialog when user clicks the date
            dateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendarAlertDialogBuilder = new AlertDialog.Builder(EntryDetailActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.calendar_dialog_view, null);

                    Button okButton = view.findViewById(R.id.okButtonId);
                    calendarView = view.findViewById(R.id.calendarId);

                    calendarAlertDialogBuilder.setView(view);
                    calendarDialog = calendarAlertDialogBuilder.create();
                    calendarDialog.show();

                    Calendar calendar = Calendar.getInstance();
                    editedDate = calendar.getTimeInMillis();

                    // Getting selected date from calendar calendarDialog
                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            editedDate = calendar.getTimeInMillis();
                        }
                    });

                    // Starting new entry activity when ok button is clicked
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dateEdit.setText(dateFormat.format(editedDate));
                            calendarDialog.dismiss();
                        }
                    });
                }
            });

            // Update the edited entry
            saveEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editedEntry = new Entry();
                    editedEntry.setDate(editedDate);
                    editedEntry.setTitle(titleEdit.getText().toString());
                    editedEntry.setText(entryEdit.getText().toString());
                    editedEntry.setEntryId(entryId);

                    DatabaseHandler dba = new DatabaseHandler(getApplicationContext());
                    dba.editEntry(editedEntry);

                    // Apply the update to the firebase data
                    if (prefs.getBoolean("backup?", false)) {
                        updateFirebaseEntry(editedEntry);
                    }

                    editDialog.dismiss();
                    EntryDetailActivity.this.finish();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateFirebaseEntry(Entry editedEntry) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        String currUserUID = currUser.getUid();

        FirebaseDatabase.getInstance().getReference().child(currUserUID)
                .child(Long.toString(editedEntry.getDate()))
                .child("date")
                .setValue(Long.toString(editedEntry.getDate()));

        FirebaseDatabase.getInstance().getReference().child(currUserUID)
                .child(Long.toString(editedEntry.getDate()))
                .child("entryText")
                .setValue(editedEntry.getText());

        FirebaseDatabase.getInstance().getReference().child(currUserUID)
                .child(Long.toString(editedEntry.getDate()))
                .child("title")
                .setValue(editedEntry.getTitle());
    }

    public void deleteEntryFromFirebase(long entryId) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        String currUserUID = currUser.getUid();

        FirebaseDatabase.getInstance().getReference().child(currUserUID)
                .child(Long.toString(entryId))
                .removeValue();
    }


}
