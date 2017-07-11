package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyReceiver extends BroadcastReceiver {
    private boolean itsTheFirstTime = true;
    private int noOfPowerClicks = 0;
    private boolean screenOff;
    private long unixOfThatTime;
    private long unixTime;

    public void onReceive(Context context, Intent intent) {

        if (this.itsTheFirstTime) {
            resetStartingTime();
            this.itsTheFirstTime = false;
            this.noOfPowerClicks++;
        }
        if (checkValidity(this.unixTime, System.currentTimeMillis() / 1000)) {
            this.itsTheFirstTime = false;
            this.noOfPowerClicks++;
            if (this.noOfPowerClicks == 4) {

                this.noOfPowerClicks = 0;
                Intent intenty = new Intent(context, UpdateService.class);
                intenty.putExtra("power_four_times", true);
                context.startService(intenty);
            }
        } else {
            this.itsTheFirstTime = true;
            resetStartingTime();
            this.noOfPowerClicks = 0;
        }

    }

    private boolean checkValidity(long firstClick, long thisClick) {
        if (thisClick - firstClick < 6) {
            return true;
        }
        return false;
    }

    private void resetStartingTime() {

        this.unixTime = System.currentTimeMillis() / 1000;
    }
}
