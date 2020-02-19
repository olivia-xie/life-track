package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Data.EntryRecyclerViewAdapter;
import com.example.newdiary.Data.SharedPrefs;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder calendarAlertDialogBuilder;
    private AlertDialog.Builder settingsAlertDialogBuilder;
    private AlertDialog.Builder passcodeAlertDialogBuilder;
    private AlertDialog calendarDialog;
    private AlertDialog settingsDialog;
    private AlertDialog passcodeDialog;
    private Entry myEntry;
    private ArrayList<Entry> entriesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EntryRecyclerViewAdapter entryRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private CalendarView calendarView;
    private long selectedDate;
    private SharedPrefs sharedPrefs;
    private TextView noEntriesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefs = new SharedPrefs(MainActivity.this);

        // Checks if lock screen has been enabled by user and starts lock screen activity if true
        if (sharedPrefs.getPasscodeOption()) {
                Intent intent = new Intent(MainActivity.this, LockScreenActivity.class);
                intent.putExtra("actualPIN", sharedPrefs.getPasscode());
                startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button to create new diary entry
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog();
            }
        });

        noEntriesTextView = findViewById(R.id.noEntriesId);
        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    // Opening calendar dialog from menu Search button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {

            openSettingsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    // Refresh database to reflect changes such as added/deleted entries
    private void refreshData() {

        entriesList.clear();

        databaseHandler = new DatabaseHandler(getApplicationContext());

        ArrayList<Entry> entriesFromDB = databaseHandler.getEntries();

        // Adding message if there are no entries yet
        if (entriesFromDB.size() == 0) {
            noEntriesTextView.setVisibility(View.VISIBLE);
        } else {
            noEntriesTextView.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < entriesFromDB.size(); i++) {

            String title = entriesFromDB.get(i).getTitle();
            long date = entriesFromDB.get(i).getDate();
            String entryText = entriesFromDB.get(i).getText();
            int entryId = entriesFromDB.get(i).getEntryId();

            myEntry = new Entry();
            myEntry.setTitle(title);
            myEntry.setDate(date);
            myEntry.setText(entryText);
            myEntry.setEntryId(entryId);

            entriesList.add(myEntry);
        }

        databaseHandler.close();

        entryRecyclerViewAdapter = new EntryRecyclerViewAdapter(this, entriesList);
        recyclerView.setAdapter(entryRecyclerViewAdapter);
        entryRecyclerViewAdapter.notifyDataSetChanged();

    }

    // Shows settings alert dialog where user can add passcode protection
    public void openSettingsDialog() {

        // Getting switch on/off preferences
        boolean passcodeOption = sharedPrefs.getPasscodeOption();

        // Showing alert dialog
        settingsAlertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.settings_dialog_view, null);

        settingsAlertDialogBuilder.setView(view);
        settingsDialog = settingsAlertDialogBuilder.create();
        settingsDialog.show();

        Switch passcodeSwitch = view.findViewById(R.id.passcodeSwitchId);
        passcodeSwitch.setChecked(passcodeOption);

        Button signOutButton = view.findViewById(R.id.signOutButtonId);

        // Saving toggle button preferences
        passcodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    settingsDialog.dismiss();

                    createPasswordDialog();

                } else {
                    sharedPrefs.setPasscodeOption(false);
                }
            }
        });

        // Signing user out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    // Creates dialog for user to set a new pin code
    public void createPasswordDialog() {

        passcodeAlertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.passcode_dialog_view, null);

        passcodeAlertDialogBuilder.setView(v);
        passcodeDialog = passcodeAlertDialogBuilder.create();
        passcodeDialog.show();

        final EditText passcodeEditText, confirmEditText;
        Button setPasscodeButton;

        passcodeEditText = v.findViewById(R.id.passwordEditTextId);
        confirmEditText = v.findViewById(R.id.signInPasswordEditTextId);
        setPasscodeButton = v.findViewById(R.id.signInButtonId);

        // Verify if the new PIN is a valid PIN and sets the PIN if so
        setPasscodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((passcodeEditText.getText().length() != 0) && (confirmEditText.getText().length() != 0) &&
                        (passcodeEditText.getText().toString().equals(confirmEditText.getText().toString()))) {

                    String PIN = passcodeEditText.getText().toString();

                    sharedPrefs.setPasscodeOption(true);
                    sharedPrefs.setPasscode(PIN);

                    passcodeEditText.setText(null);
                    confirmEditText.setText(null);

                    passcodeDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "PIN successfully set.", Toast.LENGTH_LONG).show();

                } else if ((passcodeEditText.getText().length() == 0) || (confirmEditText.getText().length() == 0)) {

                    Toast.makeText(getApplicationContext(), "PIN cannot be empty.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "PINs do not match.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Shows calendar calendarDialog when new entry button is pressed
    public void showCalendarDialog() {

        calendarAlertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.calendar_dialog_view, null);

        Button okButton = view.findViewById(R.id.okButtonId);
        calendarView = view.findViewById(R.id.calendarId);

        calendarAlertDialogBuilder.setView(view);
        calendarDialog = calendarAlertDialogBuilder.create();
        calendarDialog.show();

        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.getTimeInMillis();

        // Getting selected date from calendar calendarDialog
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = calendar.getTimeInMillis();

            }
        });

        // Starting new entry activity when ok button is clicked
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                intent.putExtra("date", selectedDate);

                calendarDialog.dismiss();
                startActivity(intent);
            }
        });

    }
}
