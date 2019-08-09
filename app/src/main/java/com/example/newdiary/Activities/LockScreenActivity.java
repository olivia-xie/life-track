package com.example.newdiary.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.newdiary.Data.SharedPrefs;
import com.example.newdiary.R;

import org.w3c.dom.Text;

public class LockScreenActivity extends AppCompatActivity {

    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private TextView promptText;
    private ViewGroup transitionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        final String actualPIN = getIntent().getStringExtra("actualPIN");

        pinLockView = findViewById(R.id.pinLockViewId);
        indicatorDots = findViewById(R.id.indicatorDotsId);
        promptText = findViewById(R.id.promptTextId);
        transitionsContainer = findViewById(R.id.transitionsContainer);

        // Attaching indicator dots to pin lock view
        pinLockView.attachIndicatorDots(indicatorDots);
        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        // Lock screen pin pad listener
        pinLockView.setPinLockListener(new PinLockListener() {

            boolean visible;

            @Override
            public void onComplete(String pin) {

                if (pin.equals(actualPIN)){
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
