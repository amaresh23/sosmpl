package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsSentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case -1:
                Toast.makeText(context, "SMS Sent", 0).show();
                return;
            case 1:
                Toast.makeText(context, "SMS generic failure", 0).show();
                return;
            case 2:
                Toast.makeText(context, "SMS radio off", 0).show();
                return;
            case 3:
                Toast.makeText(context, "SMS null PDU", 0).show();
                return;
            case 4:
                Toast.makeText(context, "SMS no service", 0).show();
                return;
            default:
                return;
        }
    }
}