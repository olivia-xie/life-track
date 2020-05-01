package com.example.newdiary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.newdiary.R;

public class LockScreenActivity extends AppCompatActivity {

    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private TextView promptText;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

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

    // Prevents user from bypassing the lock screen activity
    @Override
    public void onBackPressed() {

    }

}
