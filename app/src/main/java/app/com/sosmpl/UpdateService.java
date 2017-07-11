package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateService extends Service {
    public static final String SHARED_PREFS = "spPanicSms";
    String addressOfVictim = "";
    String call_on;
    Context context = this;
    Geocoder geocoder;
    GPSTracker gps;
    protected LocationManager locationManager;
    Context mContext = this;
    BroadcastReceiver mReceiver;
    private NotificationManager nm;
    int noOfClicks = 0;
    String smsMessag = "";
    String text_on1;
    String text_on2;
    String text_on3;
    String time;
    long unixTime;

    class C00041 implements Runnable {
        C00041() {
        }

        public void run() {
            if (EmergencyAppSingleTon.getDatabaseHelper().getSafetyStatus()) {
                UpdateService.this.iShouldNotSendSmsAgain();
                Log.d("EMERGENCY APP: sms not sent because user is fine", "ok");
                return;
            }
            UpdateService.this.showNotification();
            new SendText().execute(new String[]{""});
            UpdateService.this.iShouldSendSmsAgain();
        }
    }

    public class GPSTracker implements LocationListener {
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        private static final long MIN_TIME_BW_UPDATES = 60000;
        boolean canGetLocation = false;
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        double latitude;
        Location location;
        protected LocationManager locationManager;
        double longitude;
        private final Context mContext;

        class C00051 implements OnClickListener {
            C00051() {
            }

            public void onClick(DialogInterface dialog, int which) {
                GPSTracker.this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        }

        class C00062 implements OnClickListener {
            C00062() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }

        public GPSTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                this.isGPSEnabled = this.locationManager.isProviderEnabled("gps");
                this.isNetworkEnabled = this.locationManager.isProviderEnabled("network");
                if (this.isGPSEnabled || this.isNetworkEnabled) {
                    this.canGetLocation = true;
                    if (this.isNetworkEnabled) {
                        this.locationManager.requestLocationUpdates("network", MIN_TIME_BW_UPDATES, 10.0f, this);
                      ;
                        if (this.locationManager != null) {
                            this.location = this.locationManager.getLastKnownLocation("network");
                            if (this.location != null) {
                                this.latitude = this.location.getLatitude();
                                this.longitude = this.location.getLongitude();
                                getAddressOfThatPlace(this.latitude, this.longitude);
                            }
                        }
                    }
                    if (this.isGPSEnabled && this.location == null) {
                        this.locationManager.requestLocationUpdates("gps", MIN_TIME_BW_UPDATES, 10.0f, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (this.locationManager != null) {
                            this.location = this.locationManager.getLastKnownLocation("gps");
                            if (this.location != null) {
                                this.latitude = this.location.getLatitude();
                                this.longitude = this.location.getLongitude();
                                getAddressOfThatPlace(this.latitude, this.longitude);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.location;
        }

        public void stopUsingGPS() {
            if (this.locationManager != null) {
                this.locationManager.removeUpdates(this);
            }
        }

        public double getLatitude() {
            if (this.location != null) {
                this.latitude = this.location.getLatitude();
            }
            return this.latitude;
        }

        public double getLongitude() {
            if (this.location != null) {
                this.longitude = this.location.getLongitude();
            }
            return this.longitude;
        }

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        public void showSettingsAlert() {
            Builder alertDialog = new Builder(this.mContext);
            alertDialog.setTitle("GPS is settings");
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new C00051());
            alertDialog.setNegativeButton("Cancel", new C00062());
            alertDialog.show();
        }

        public void onLocationChanged(Location location) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        private void getAddressOfThatPlace(double latitude, double longitude) {
            try {
                List<Address> addresses = UpdateService.this.geocoder.getFromLocation(latitude, longitude, 10);
                UpdateService.this.addressOfVictim = "";
                for (Address address : addresses) {
                    UpdateService updateService = UpdateService.this;
                    updateService.addressOfVictim += address.getAddressLine(0) + " ";
                }

            } catch (IOException e) {

            }
        }
    }

    private class SendText extends AsyncTask<String, Void, String> {
        private SendText() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            String[] numbers_ = EmergencyAppSingleTon.getDatabaseHelper().getNumbers();
            UpdateService.this.text_on1 = numbers_[0];
            UpdateService.this.text_on2 = numbers_[1];
            UpdateService.this.text_on3 = numbers_[2];
            if (UpdateService.this.text_on1.length() != 0) {

                UpdateService.this.sendSMS(UpdateService.this.text_on1, UpdateService.this.smsMessag);
            }
            if (UpdateService.this.text_on2 != "") {
                UpdateService.this.sendSMS(UpdateService.this.text_on2, UpdateService.this.smsMessag);

            }
            if (UpdateService.this.text_on3 != "") {
                UpdateService.this.sendSMS(UpdateService.this.text_on3, UpdateService.this.smsMessag);
                Log.d("3 Texting Number", UpdateService.this.text_on3);
            }
        }

        protected String doInBackground(String... arg0) {
            return null;
        }
    }

    public void onCreate() {
        super.onCreate();
        this.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.mReceiver = new MyReceiver();
        registerReceiver(this.mReceiver, filter);
    }

    public void onDestroy() {
        unregisterReceiver(this.mReceiver);

        super.onDestroy();
    }

    public void onStart(Intent intent, int startId) {
        if (intent.getBooleanExtra("power_four_times", false)) {
            makeThingsHappen();
        }
        this.geocoder = new Geocoder(this.mContext);
    }

    public void makeThingsHappen() {
        String[] numbers_ = EmergencyAppSingleTon.getDatabaseHelper().getNumbers();
        shouldSendSmsAgain();

        DatabaseHelper dbHelper = EmergencyAppSingleTon.getDatabaseHelper();
        dbHelper.changeSafetyStatus("value", 0);

        showNotification();
        this.noOfClicks = 0;
        this.gps = new GPSTracker(this);
        if (this.gps.canGetLocation()) {
            double latitude = this.gps.getLatitude();
            double longitude = this.gps.getLongitude();

            String firstPart = dbHelper.getNumbers()[6];

            if (this.addressOfVictim.length() < 1) {
                this.smsMessag = new StringBuilder(String.valueOf(firstPart)).append(" I am on http://maps.google.com/maps?q=").append(latitude).append(",").append(longitude).append(".").toString();
            } else {
                this.smsMessag = new StringBuilder(String.valueOf(firstPart)).append(" I am on http://maps.google.com/maps?q=").append(latitude).append(",").append(longitude).append(" . Hurry to ").append(this.addressOfVictim).toString();
            }

        } else {
            this.gps.showSettingsAlert();
        }
        new SendText().execute(new String[]{""});
        Intent intentToCall = new Intent("android.intent.action.CALL", Uri.parse("tel:" + numbers_[3]));
        intentToCall.addFlags(268435456);
        startActivity(intentToCall);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSMS(String phoneNumber, String message) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList();
        PendingIntent sentPI = PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.mContext, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.mContext, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage, sentPendingIntents, deliveredPendingIntents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shouldSendSmsAgain() {
        new Handler().postDelayed(new C00041(), (long) ((Integer.valueOf(EmergencyAppSingleTon.getDatabaseHelper().getNumbers()[5]).intValue() * 1000) * 60));
    }

    public void iShouldSendSmsAgain() {

        shouldSendSmsAgain();
    }

    public void iShouldNotSendSmsAgain() {

        stopService(new Intent(this, UpdateService.class));
        startService(new Intent(this, UpdateService.class));
    }

    private void showNotification() {
        CharSequence text = getText(R.string.notification_bar_text);
        Notification notification = new Notification(R.drawable.notification, text, System.currentTimeMillis());
        try {
            RingtoneManager.getRingtone(this.context, RingtoneManager.getDefaultUri(2)).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.flags |= 16;
        this.nm.notify(R.string.detail_of_notificaiton, notification);
    }
}
