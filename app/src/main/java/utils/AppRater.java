package utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.story.craftystudio.shortstory.R;

public class AppRater {
    private final static String APP_TITLE = "Short Story Name";// App Name
    private final static String APP_PNAME = "app.story.craftystudio.shortstory";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 7;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }


    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Thank You!");
            builder.setMessage("If you like the App, Give Review and support us")
                    .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.craftystudio.vocabulary.dailyeditorial&hl=en")));
                            if (editor != null) {
                                editor.putBoolean("dontshowagain", true);
                                editor.commit();
                            }
                            dialog.dismiss();

                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            if (editor != null) {
                                editor.putBoolean("dontshowagain", true);
                                editor.commit();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}