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
import android.widget.Switch;
import android.widget.Toast;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Data.EntryRecyclerViewAdapter;
import com.example.newdiary.Data.SharedPrefs;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder calendarAlertDialogBuilder;
    private AlertDialog.Builder settingsAlertDialogBuilder;
    private AlertDialog calendarDialog;
    private AlertDialog settingsDialog;
    private Entry myEntry;
    private ArrayList<Entry> entriesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EntryRecyclerViewAdapter entryRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    // Opening Search alert calendarDialog from menu Search button
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

    private void refreshData() {

        entriesList.clear();

        databaseHandler = new DatabaseHandler(getApplicationContext());

        ArrayList<Entry> entriesFromDB = databaseHandler.getEntries();

        for (int i = 0; i < entriesFromDB.size(); i++) {

            String title = entriesFromDB.get(i).getTitle();
            String date = entriesFromDB.get(i).getDate();
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

    // Shows settings calendarDialog where user can add passcode protection
    public void openSettingsDialog() {

        final SharedPrefs sharedPrefs = new SharedPrefs(MainActivity.this);
        boolean passcodeOption = sharedPrefs.getPasscodeOption();

        settingsAlertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.settings_dialog_view, null);

        settingsAlertDialogBuilder.setView(view);
        settingsDialog = settingsAlertDialogBuilder.create();
        settingsDialog.show();

        Switch passcodeSwitch = view.findViewById(R.id.passcodeSwitchId);
        passcodeSwitch.setChecked(passcodeOption);

        passcodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sharedPrefs.setPasscodeOption(true);
                } else {
                    sharedPrefs.setPasscodeOption(false);
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

        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        selectedDate = format.format(calendarView.getDate());

        // Getting selected date from calendar calendarDialog
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                long dateMilli = calendar.getTimeInMillis();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                selectedDate = simpleDateFormat.format(dateMilli);
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
