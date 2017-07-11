package app.com.sosmpl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;


import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    private static final String CALLING_NUMBER = "calling_number";
    private static final String COLUMN_TEXT1 = "texting_number1";
    private static final String COLUMN_TEXT2 = "texting_number2";
    private static final String COLUMN_TEXT3 = "texting_number3";
    private static final String EMERGENCY_MESSAGE = "emergency_message";
    private static final String EMERGENCY_NUMBER = "emergency_number";
    private static final String RESEND_DURATION = "duration_to_call";
    public static final String SHARED_PREFS = "spPanicSms";
    final int CALL_NUMBER = 4;
    final int TEXT1 = 1;
    final int TEXT2 = 2;
    final int TEXT3 = 3;
    Button btnSaveNumbers;
    Context context;
    EditText etCallingDuration;
    EditText etCallingNumber;
    EditText etEmergencyNumber;
    EditText etMessage;
    EditText etTextingNumber1;
    EditText etTextingNumber2;
    EditText etTextingNumber3;
    Intent intent;
    ToggleButton tbSafe;
    TextView tvTextingNumberEmpty;
    TextView tvWarningEmergencyNumberEmpty;
    TextView tvWarningNumberEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);

        this.etTextingNumber1 = (EditText) findViewById(R.id.etFirstNumber);
        this.etTextingNumber2 = (EditText) findViewById(R.id.etSecondTextingNumber);
        this.etTextingNumber3 = (EditText) findViewById(R.id.etThirdTextingNumber);
        this.etCallingDuration = (EditText) findViewById(R.id.etDurationToWaitBeforeResend);
        this.etCallingNumber = (EditText) findViewById(R.id.etCallingNumber);
        this.etEmergencyNumber = (EditText) findViewById(R.id.etEmergencyNumber);
        this.etMessage = (EditText) findViewById(R.id.etMessage);
        this.btnSaveNumbers = (Button) findViewById(R.id.saveNumbers);
        this.tvWarningNumberEmpty = (TextView) findViewById(R.id.tvWarningEmptyCallingNumber);
        this.tvWarningEmergencyNumberEmpty = (TextView) findViewById(R.id.tvWarningEmptyEmergencyNumber);
        this.tvTextingNumberEmpty = (TextView) findViewById(R.id.tvWarningEmptyTextingNumber);
        publishThePresentDatabase();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }

        required_fields();
        return true;
    }

    private void publishThePresentDatabase() {
        String[] all_numbers = EmergencyAppSingleTon.getDatabaseHelper().getNumbers();
        this.etTextingNumber1.setText(all_numbers[0]);
        this.etTextingNumber2.setText(all_numbers[1]);
        this.etTextingNumber3.setText(all_numbers[2]);
        this.etCallingNumber.setText(all_numbers[3]);
        this.etEmergencyNumber.setText(all_numbers[4]);
        this.etCallingDuration.setText(all_numbers[5]);
        this.etMessage.setText(all_numbers[6]);
    }

    public void fetchFirstNumber(View v) {
        Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 1);
    }

    public void fetchSecondNumber(View v) {
        Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 2);
    }

    public void fetchThirdNumber(View v) {
        Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 3);
    }

    public void fetchCallingNumber(View v) {
        Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 4);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 1:
                    this.etTextingNumber1.setText(getMeTheNumber(data));
                    return;
                case 2:
                    this.etTextingNumber2.setText(getMeTheNumber(data));
                    return;
                case 3:
                    this.etTextingNumber3.setText(getMeTheNumber(data));
                    return;
                case 4:
                    String number = getMeTheNumber(data);
                    this.etCallingNumber.setText(number);

                    return;
                default:
                    return;
            }
        }
    }

    public String getMeTheNumber(Intent data) {
        Cursor cursor = getContentResolver().query(data.getData(), new String[]{"data1"}, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("data1"));
    }

    public void iAmSafe(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().changeSafetyStatus("value", 1);
        restartTheService();
    }

    public void restartTheService() {

        stopService(this.intent);

        startService(this.intent);
    }

    public void clearTextingNumber1(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(COLUMN_TEXT1, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void clearTextingNumber2(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(COLUMN_TEXT2, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void clearTextingNumber3(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(COLUMN_TEXT3, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void clearCallingNumber(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(CALLING_NUMBER, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void clearWaitingDuration(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(RESEND_DURATION, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void clearEmergencyNumber(View v) {
        EmergencyAppSingleTon.getDatabaseHelper().updateThisThing(EMERGENCY_NUMBER, "");
        publishThePresentDatabase();
        Toast.makeText(getApplicationContext(), "Changes were saved", Toast.LENGTH_SHORT).show();
    }

    public void saveNumbers(View v) {
        saveNumbers();
    }

    private void saveNumbers() {
        this.tvTextingNumberEmpty.setVisibility(8);
        this.tvWarningNumberEmpty.setVisibility(8);
        this.tvWarningEmergencyNumberEmpty.setVisibility(8);
        if (!IsEditBoxEmpty(this.etTextingNumber1)) {
            this.tvTextingNumberEmpty.setVisibility(0);
            this.etTextingNumber1.requestFocus();
        } else if (!IsEditBoxEmpty(this.etCallingNumber)) {
            this.tvWarningNumberEmpty.setVisibility(0);
            this.etCallingNumber.requestFocus();
        } else if (IsEditBoxEmpty(this.etEmergencyNumber)) {
            DatabaseHelper dbHelper = EmergencyAppSingleTon.getDatabaseHelper();
            dbHelper.updateThisThing(CALLING_NUMBER, this.etCallingNumber.getText().toString());
            dbHelper.updateThisThing(COLUMN_TEXT1, this.etTextingNumber1.getText().toString());
            dbHelper.updateThisThing(COLUMN_TEXT2, this.etTextingNumber2.getText().toString());
            dbHelper.updateThisThing(COLUMN_TEXT3, this.etTextingNumber3.getText().toString());
            dbHelper.updateThisThing(EMERGENCY_NUMBER, this.etEmergencyNumber.getText().toString());
            dbHelper.updateThisThing(RESEND_DURATION, this.etCallingDuration.getText().toString());
            dbHelper.updateThisThing(EMERGENCY_MESSAGE, this.etMessage.getText().toString());
            String[] numbers_are = dbHelper.getNumbers();
            for (int index = 0; index < numbers_are.length; index++) {

            }
            publishThePresentDatabase();
            Toast.makeText(getApplicationContext(), "Changes were saved", 0).show();
            finish();
        } else {
            this.tvWarningEmergencyNumberEmpty.setVisibility(0);
            this.etEmergencyNumber.requestFocus();
        }
    }

    private void required_fields() {
        boolean all_okay;
        if (!IsEditBoxEmpty(this.etTextingNumber1)) {
            all_okay = false;
        } else if (!IsEditBoxEmpty(this.etCallingNumber)) {
            all_okay = false;
        } else if (IsEditBoxEmpty(this.etEmergencyNumber)) {
            all_okay = true;
        } else {
            all_okay = false;
        }
        if (all_okay) {
            finish();
        } else {
            tellUserRequiredFieldsAreMissing();
        }
    }

    protected void tellUserRequiredFieldsAreMissing() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.havent_filled_required_fields);
        ((Button) dialog.findViewById(R.id.btn_yes)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean IsEditBoxEmpty(EditText view) {
        if (view.getText().toString().trim().length() < 1) {
            return false;
        }
        return true;
    }
}
