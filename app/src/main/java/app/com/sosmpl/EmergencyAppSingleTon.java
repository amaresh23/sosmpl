package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.app.Application;
import android.content.res.Configuration;
import android.database.SQLException;
import android.util.Log;
import java.io.IOException;

public class EmergencyAppSingleTon extends Application {
    private static DatabaseHelper dbHelper;
    private static EmergencyAppSingleTon singleton;

    public static EmergencyAppSingleTon getInstance() {
        return singleton;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;

        initialiseDatabase();
    }

    private void initialiseDatabase() {
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
            try {
                dbHelper.openDatabase();
            } catch (SQLException sqle) {
                throw sqle;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            throw new Error("Unable to create database");
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public void onTerminate() {
        super.onTerminate();
        dbHelper.close();
        dbHelper = null;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }
}
