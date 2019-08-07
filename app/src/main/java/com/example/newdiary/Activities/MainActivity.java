package com.example.newdiary.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.newdiary.Data.DatabaseHandler;
import com.example.newdiary.Data.EntryRecyclerViewAdapter;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private Entry myEntry;
    private ArrayList<Entry> entriesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EntryRecyclerViewAdapter entryRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;

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

    // Shows calendar dialog when new entry button is pressed
    public void showCalendarDialog() {

        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);

        Button okButton = view.findViewById(R.id.okButtonId);
        final CalendarView calendarView = view.findViewById(R.id.calendarId);

        alertDialogBuilder.setView(view);
        dialog = alertDialogBuilder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateFormat dateFormat = DateFormat.getDateInstance();
                long calendarDate = calendarView.getDate();
                selectedDate = dateFormat.format(new Date(calendarDate));

                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                intent.putExtra("date", selectedDate);

                dialog.dismiss();
                startActivity(intent);
            }
        });

    }
}
