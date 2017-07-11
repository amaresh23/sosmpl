package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CALLING_NUMBER = "calling_number";
    private static final String COLUMN_PRIMARY_KEY = "_id";
    private static final String COLUMN_SAFETY_STATUS = "forced_status";
    private static final String COLUMN_SAFETY_VALUE = "value";
    private static final String COLUMN_TEXT1 = "texting_number1";
    private static final String COLUMN_TEXT2 = "texting_number2";
    private static final String COLUMN_TEXT3 = "texting_number3";
    private static final String DATA_TABLE_NAME = "tbl_details";
    private static final String DB_NAME = "panic.db";
    private static String DB_PATH = "/data/data/app.com.sosmpl/databases/";
    private static final String EMERGENCY_MESSAGE = "emergency_message";
    private static final String EMERGENCY_NUMBER = "emergency_number";
    private static final String RESEND_DURATION = "duration_to_wait";
    private static final String STATUS_TABLE_NAME = "tbl_status";
    private final Context myContext;
    private SQLiteDatabase myDataBase = null;
    String myPath = (DB_PATH + DB_NAME);

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDataBase() throws IOException {
        if (!checkDataBase()) {
            getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(this.myPath, null, 0);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        if (checkDB != null) {
            return true;
        }
        return false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = this.myContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] buffer = new byte[1024];
        while (true) {
            int length = myInput.read(buffer);
            if (length <= 0) {
                myOutput.flush();
                myOutput.close();
                myInput.close();
                return;
            }
            myOutput.write(buffer, 0, length);
        }
    }

    public void openDatabase() throws IOException {
        this.myDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, 0);
    }

    public void updateThisThing(String whatCategory, String withThisData) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(whatCategory, withThisData);
        this.myDataBase.update(DATA_TABLE_NAME, contentValues, "_id = '1'", null);

    }

    public void changeSafetyStatus(String theRow, int the_value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SAFETY_VALUE, Integer.valueOf(the_value));
        this.myDataBase.update(STATUS_TABLE_NAME, contentValues, "_id=1", null);

    }

    public void forcedEmergency(String theRow, int the_value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SAFETY_STATUS, Integer.valueOf(the_value));
        this.myDataBase.update(STATUS_TABLE_NAME, contentValues, "_id=1", null);

    }

    public Cursor getData() throws SQLException {
        Cursor thisCursor = this.myDataBase.rawQuery("SELECT * from tbl_details;", null);

        return thisCursor;
    }

    public boolean getSafetyStatus() {
        Cursor myCursor = this.myDataBase.rawQuery("SELECT * from tbl_status;", null);
        int status = 0;
        if (myCursor != null) {
            myCursor.moveToFirst();
            do {
                status = myCursor.getInt(myCursor.getColumnIndex(COLUMN_SAFETY_VALUE));
            } while (myCursor.moveToNext());
        }
        myCursor.close();
        if (status == 0) {
            return false;
        }
        return true;
    }

    public boolean getForcedEmergencyStatus() {
        Cursor myCursor = this.myDataBase.rawQuery("SELECT * from tbl_status;", null);
        int status = 0;
        if (myCursor != null) {
            myCursor.moveToFirst();
            do {
                status = myCursor.getInt(myCursor.getColumnIndex(COLUMN_SAFETY_STATUS));
            } while (myCursor.moveToNext());
        }
        myCursor.close();
        if (status == 0) {
            return false;
        }
        return true;
    }

    public String[] getNumbers() {
        DatabaseHelper dbHelper = EmergencyAppSingleTon.getDatabaseHelper();
        Cursor myCursor = dbHelper.getData();
        String[] array_of_data = new String[7];
        if (myCursor != null) {
            myCursor.moveToFirst();
            do {
                array_of_data[0] = myCursor.getString(myCursor.getColumnIndex(COLUMN_TEXT1));
                array_of_data[1] = myCursor.getString(myCursor.getColumnIndex(COLUMN_TEXT2));
                array_of_data[2] = myCursor.getString(myCursor.getColumnIndex(COLUMN_TEXT3));
                array_of_data[3] = myCursor.getString(myCursor.getColumnIndex(CALLING_NUMBER));
                array_of_data[4] = myCursor.getString(myCursor.getColumnIndex(EMERGENCY_NUMBER));
                array_of_data[5] = myCursor.getString(myCursor.getColumnIndex("duration_to_call"));
                array_of_data[6] = myCursor.getString(myCursor.getColumnIndex(EMERGENCY_MESSAGE));
            } while (myCursor.moveToNext());
        }
        myCursor.close();
        dbHelper.close();
        return array_of_data;
    }
}
