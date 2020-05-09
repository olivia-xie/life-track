package com.oliviaxie.simplediary.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oliviaxie.simplediary.Data.DatabaseHandler;
import com.oliviaxie.simplediary.Data.EntryRecyclerViewAdapter;
import com.oliviaxie.simplediary.Models.Entry;
import com.oliviaxie.simplediary.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder calendarAlertDialogBuilder;
    private AlertDialog.Builder settingsAlertDialogBuilder;
    private AlertDialog.Builder passcodeAlertDialogBuilder;
    private AlertDialog.Builder changeThemeAlertDialogBuilder;

    private AlertDialog calendarDialog;
    private AlertDialog settingsDialog;
    private AlertDialog passcodeDialog;
    private AlertDialog changeThemeDialog;

    private Entry myEntry;
    private ArrayList<Entry> entriesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EntryRecyclerViewAdapter entryRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private CalendarView calendarView;
    private long selectedDate;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private TextView noEntriesTextView;
    private ImageButton changeThemeButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();

        // Setting color theme
        String themePref = sharedPrefs.getString("theme", "blue");
        setColorTheme(themePref);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(currUser.getUid());

        // Retrieving any existing user entries from firebase
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot entry : dataSnapshot.getChildren()){

                        Entry newEntry = new Entry();

                        // Entry ID
                        newEntry.setEntryId(Long.parseLong(entry.getKey()));

                        int count = 1;

                        for (DataSnapshot entryField : entry.getChildren()) {
                            if (count == 1) {
                                newEntry.setDate(Long.parseLong(entryField.getValue().toString()));
                            }

                            if (count == 2) {
                                newEntry.setText(entryField.getValue().toString());
                            }

                            if (count == 3) {
                                newEntry.setTitle(entryField.getValue().toString());
                            }

                            count++;
                        }

                        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                        dbHandler.addEntry(newEntry);
                        dbHandler.close();

                        refreshData();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        changeThemeButton = findViewById(R.id.paletteButtonId);
        changeThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeThemeDialog();
            }
        });

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
            long entryId = entriesFromDB.get(i).getEntryId();

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

    // Shows settings alert dialog where user can add passcode protection and log out
    public void openSettingsDialog() {

        // Getting switch on/off preferences
        boolean passcodeOption = sharedPrefs.getBoolean("passcode?", false);

        // Getting backup preferences
        boolean backupOption = sharedPrefs.getBoolean("backup?", false);

        // Showing alert dialog
        settingsAlertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.settings_dialog_view, null);

        settingsAlertDialogBuilder.setView(view);
        settingsDialog = settingsAlertDialogBuilder.create();
        settingsDialog.show();

        Switch passcodeSwitch = view.findViewById(R.id.passcodeSwitchId);
        passcodeSwitch.setChecked(passcodeOption);

        Switch backupSwitch = view.findViewById(R.id.backUpSwitchId);
        backupSwitch.setChecked(backupOption);

        Button signOutButton = view.findViewById(R.id.signOutButtonId);

        // Saving password toggle button preferences
        passcodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingsDialog.dismiss();
                    createPinCodeDialog();

                } else {
                    prefsEditor.putBoolean("passcode?", false);
                    prefsEditor.putString("pin", null);
                    prefsEditor.apply();
                }
            }
        });

        // Saving backup toggle button preferences
        backupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefsEditor.putBoolean("backup?", true);
                    prefsEditor.apply();
                    backupAllDataToFirebase();

                } else {
                    prefsEditor.putBoolean("backup?", false);
                    prefsEditor.apply();
                }
            }
        });

        // Signing user out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth != null) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Log Out?");
                    alert.setMessage("Are you sure you want to log out? Remember to back up your data if you would like to import your entries once you login again.");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();

                            // Remove PIN on sign out
                            prefsEditor.putBoolean("passcode?", false);
                            prefsEditor.putBoolean("backup?", false);
                            prefsEditor.putString("pin", null);
                            prefsEditor.apply();

                            // Clear local database on sign out
                            DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                            dbHandler.clearTable();

                            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                            startActivity(intent);
                            settingsDialog.dismiss();

                            finish();
                        }
                    });

                    alert.setNegativeButton("No", null);

                    alert.show();
                }
            }
        });
    }

    // Creates dialog for user to set a new pin code
    public void createPinCodeDialog() {

        passcodeAlertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.passcode_dialog_view, null);

        passcodeAlertDialogBuilder.setView(v);
        passcodeDialog = passcodeAlertDialogBuilder.create();
        passcodeDialog.show();

        final EditText passcodeEditText, confirmEditText, passcodeHintEditText;
        Button setPasscodeButton;

        passcodeEditText = v.findViewById(R.id.pinEditTextId);
        confirmEditText = v.findViewById(R.id.confirmPinEditTextId);
        passcodeHintEditText = v.findViewById(R.id.pinHintEditTextId);
        setPasscodeButton = v.findViewById(R.id.signInButtonId);

        // Verify if the new PIN is a valid PIN and sets the PIN if so
        setPasscodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((passcodeEditText.getText().length() != 0) && (confirmEditText.getText().length() != 0) &&
                        (passcodeEditText.getText().toString().equals(confirmEditText.getText().toString())) &&
                        (passcodeHintEditText.getText().length() != 0)) {

                    String PIN = passcodeEditText.getText().toString();
                    String pinHint = passcodeHintEditText.getText().toString();

                    prefsEditor.putBoolean("passcode?", true);
                    prefsEditor.putString("pin", PIN);
                    prefsEditor.putString("pinHint", pinHint);
                    prefsEditor.apply();

                    passcodeEditText.setText(null);
                    confirmEditText.setText(null);
                    passcodeHintEditText.setText(null);

                    passcodeDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "PIN successfully set.", Toast.LENGTH_LONG).show();
                } else if ((passcodeEditText.getText().length() == 0) || (confirmEditText.getText().length() == 0)) {

                    Toast.makeText(getApplicationContext(), "PIN cannot be empty.", Toast.LENGTH_LONG).show();
                } else if (passcodeHintEditText.getText().length() == 0) {

                    Toast.makeText(getApplicationContext(), "Enter a hint to remember your PIN", Toast.LENGTH_LONG).show();
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

    public void backupAllDataToFirebase() {

        databaseHandler = new DatabaseHandler(getApplicationContext());
        ArrayList<Entry> entriesFromDB = databaseHandler.getEntries();

        if (currUser != null) {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            String currUserUID = currUser.getUid();

            for (int i = 0; i < entriesFromDB.size(); i++) {

                String entryId = Long.toString(entriesFromDB.get(i).getEntryId());

                firebaseDatabase.getReference().child(currUserUID).child(entryId)
                        .child("title")
                        .setValue(entriesFromDB.get(i).getTitle());

                firebaseDatabase.getReference().child(currUserUID).child(entryId)
                        .child("entryText")
                        .setValue(entriesFromDB.get(i).getText());

                firebaseDatabase.getReference().child(currUserUID).child(entryId)
                        .child("date")
                        .setValue(entriesFromDB.get(i).getDate());
            }
        }

    }

    // Creates dialog to change application's color theme
    public void openChangeThemeDialog() {
        changeThemeAlertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.theme_change_dialog_view, null);

        changeThemeAlertDialogBuilder.setView(v);
        changeThemeDialog = changeThemeAlertDialogBuilder.create();
        changeThemeDialog.show();

        Button blueTheme, pinkTheme, purpleTheme, greenTheme, yellowTheme, orangeTheme;

        blueTheme = v.findViewById(R.id.blueThemeButtonId);
        blueTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "blue");
                prefsEditor.apply();
                recreate();

            }
        });

        pinkTheme = v.findViewById(R.id.pinkThemeButtonId);
        pinkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "pink");
                prefsEditor.apply();
                recreate();
            }
        });

        purpleTheme = v.findViewById(R.id.purpleThemeButtonId);
        purpleTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "purple");
                prefsEditor.apply();
                recreate();
            }
        });

        greenTheme = v.findViewById(R.id.greenThemeButtonId);
        greenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "green");
                prefsEditor.apply();
                recreate();
            }
        });

        yellowTheme = v.findViewById(R.id.yellowThemeButtonId);
        yellowTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "yellow");
                prefsEditor.apply();
                recreate();
            }
        });

        orangeTheme = v.findViewById(R.id.orangeThemeButtonId);
        orangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeDialog.dismiss();
                prefsEditor.putString("theme", "orange");
                prefsEditor.apply();
                recreate();
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
