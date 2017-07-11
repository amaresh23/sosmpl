package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainPage extends Activity {
    Button btnUserSafetyStatus;
    Context context = this;
    Intent intent;
    boolean shouldIShowAgain = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        this.btnUserSafetyStatus = (Button) findViewById(R.id.btnTheUserIsSafeNow);
        this.intent = new Intent(this, UpdateService.class);
        startService(this.intent);
        boolean safetyStatus = EmergencyAppSingleTon.getDatabaseHelper().getSafetyStatus();
        this.shouldIShowAgain = getSharedPreferences("dont_panic_show", 1).getBoolean("show", false);
        if (!this.shouldIShowAgain) {
            howToUseTheApp();
        }

        if (!safetyStatus) {
            this.btnUserSafetyStatus.setVisibility(View.VISIBLE);
        }
    }

    public void showSettings(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showAboutUs(View v) {
        startActivity(new Intent(this, ContactUs.class));
    }

    public void iAmSafeNow(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().changeSafetyStatus("value", 1);
        this.btnUserSafetyStatus.setVisibility(View.GONE);
        stopService(this.intent);
        startService(this.intent);
    }

    public void callEmergencyNumber(View v) {
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + EmergencyAppSingleTon.getDatabaseHelper().getNumbers()[4])));
    }

    public void emergencyState(View v) {
        stopService(this.intent);
        Intent intenty = new Intent(getApplicationContext(), UpdateService.class);
        intenty.putExtra("power_four_times", true);
        getApplicationContext().startService(intenty);
        this.btnUserSafetyStatus.setVisibility(View.VISIBLE);
    }

    private void howToUseTheApp() {
        final Dialog dialog_first_use = new Dialog(this.context);
        dialog_first_use.requestWindowFeature(1);
        dialog_first_use.setContentView(R.layout.dialog_first_use);
        final ImageView img_to_show = (ImageView) dialog_first_use.findViewById(R.id.img_background);
        ((ImageView) dialog_first_use.findViewById(R.id.img_got_it)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SharedPreferences myPrefs = MainPage.this.context.getSharedPreferences("dont_panic_show", 1);
                int whichImageToShow = myPrefs.getInt("number_to_show", 1);
                Editor prefsEditor = myPrefs.edit();
                prefsEditor.putBoolean("dont_show", true);
                prefsEditor.putInt("number_to_show", whichImageToShow + 1);
                prefsEditor.commit();
                checkWhatImageToShow(whichImageToShow);
            }

            private void checkWhatImageToShow(int a) {
                switch (a) {
                    case 1:

                        img_to_show.setImageResource(R.drawable.screen1);
                        return;
                    case 2:

                        img_to_show.setImageResource(R.drawable.screen2);
                        return;
                    case 3:

                        img_to_show.setImageResource(R.drawable.screen3);
                        return;
                    default:

                        Editor editor = MainPage.this.context.getSharedPreferences("dont_panic_show", 1).edit();
                        editor.putBoolean("show", true);
                        editor.commit();
                        dialog_first_use.dismiss();
                        return;
                }
            }
        });
    }

}