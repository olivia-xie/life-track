package com.oliviaxie.simplediary.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.oliviaxie.simplediary.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LockScreenActivity extends AppCompatActivity {

    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private TextView promptText;
    private Button useFingerprintButton;
    private CancellationSignal cancellationSignal;
    private Executor executor;
    private BiometricPrompt biometricPrompt;

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

        executor = Executors.newSingleThreadExecutor();

        pinLockView = findViewById(R.id.pinLockViewId);
        indicatorDots = findViewById(R.id.indicatorDotsId);
        promptText = findViewById(R.id.promptTextId);
        useFingerprintButton = findViewById(R.id.useBiometricButtonId);

        // Attaching indicator dots to pin lock view
        pinLockView.attachIndicatorDots(indicatorDots);
        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        // Disabling button if biometric scanning is not supported by device
        if (!checkBiometricSupport()) {
            useFingerprintButton.setEnabled(false);
        }

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

        // Use biometric authentication
        useFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                biometricPrompt = new BiometricPrompt.Builder(LockScreenActivity.this)
                        .setTitle("Authenticate Using Fingerprint")
                        .setSubtitle("")
                        .setDescription("")
                        .setNegativeButton("Cancel", executor,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .build();

                biometricPrompt.authenticate(getCancellationSignal(), executor,
                        getAuthenticationCallback());
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

    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        PackageManager packageManager = this.getPackageManager();

        if (!keyguardManager.isKeyguardSecure()) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
        {
            return true;
        }

        return true;
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {

        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(
                    BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                // Start main activity after successful authentication
                Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                    }
                });
        return cancellationSignal;
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
