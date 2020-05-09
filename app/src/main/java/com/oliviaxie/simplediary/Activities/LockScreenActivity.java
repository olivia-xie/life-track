package com.oliviaxie.simplediary.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.oliviaxie.simplediary.R;

public class LockScreenActivity extends AppCompatActivity {

    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private TextView promptText;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setting color theme
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String themePref = prefs.getString("theme", "blue");
        setColorTheme(themePref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String actualPIN = prefs.getString("pin", null);

        pinLockView = findViewById(R.id.pinLockViewId);
        indicatorDots = findViewById(R.id.indicatorDotsId);
        promptText = findViewById(R.id.promptTextId);

        // Attaching indicator dots to pin lock view
        pinLockView.attachIndicatorDots(indicatorDots);
        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        // Lock screen pin pad listener
        pinLockView.setPinLockListener(new PinLockListener() {

            @Override
            public void onComplete(String pin) {

                if (pin.equals(actualPIN)) {
                    Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    promptText.setText("Incorrect PIN. Please try again.");
                    pinLockView.resetPinLockView();
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lock_screen, menu);
        return true;
    }

    // Opening calendar dialog from menu Search button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.hint) {

            // Showing alert dialog
            AlertDialog.Builder hintAlertDialogBuilder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.pin_hint_dialog_view, null);

            TextView hintTextView = view.findViewById(R.id.hintTextViewId);
            hintTextView.setText(prefs.getString("pinHint", " "));

            hintAlertDialogBuilder.setView(view);
            AlertDialog hintDialog = hintAlertDialogBuilder.create();
            hintDialog.show();
        }

        return super.onOptionsItemSelected(item);
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
