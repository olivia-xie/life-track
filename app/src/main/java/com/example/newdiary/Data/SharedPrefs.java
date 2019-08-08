package com.example.newdiary.Data;

import android.app.Activity;
import android.content.SharedPreferences;

public class SharedPrefs {

    SharedPreferences sharedPreferences;

    public SharedPrefs(Activity activity) {
        sharedPreferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void setPasscodeOption(boolean choice) {
        sharedPreferences.edit().putBoolean("passcode?", choice).apply();
    }

    public boolean getPasscodeOption() {
        return sharedPreferences.getBoolean("passcode?", false);
    }
}
