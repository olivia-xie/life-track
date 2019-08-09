package com.example.newdiary.Data;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.Serializable;

public class SharedPrefs implements Serializable {

    transient SharedPreferences sharedPreferences;

    public SharedPrefs(Activity activity) {
        sharedPreferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    // get/set if user wants passcode protection
    public void setPasscodeOption(boolean choice) {
        sharedPreferences.edit().putBoolean("passcode?", choice).apply();
    }

    public boolean getPasscodeOption() {
        return sharedPreferences.getBoolean("passcode?", false);
    }

    // get/set user chosen passcode
    public void setPasscode(String pin) {
        sharedPreferences.edit().putString("pin", pin).apply();
    }

    public String getPasscode() {
        return sharedPreferences.getString("pin", "0000");
    }

}
