package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bunny on 27/09/17.
 */

public class SettingManager {


    public static void setThemeMode(Context mContext , int themeMode) {
        SharedPreferences prefs = mContext.getSharedPreferences("nightmodemanager", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putInt("thememode", themeMode);



        editor.apply();
    }

    public static int getThemeMode(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("nightmodemanager", 0);

        return prefs.getInt("thememode", 0);

    }



}
