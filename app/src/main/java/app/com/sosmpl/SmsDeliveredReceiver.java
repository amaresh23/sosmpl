package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsDeliveredReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case -1:
                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                return;
            case 0:
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }
}