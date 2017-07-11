package app.com.sosmpl;

/**
 * Created by Amaresh on 7/9/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenPanic extends Activity {

    class C00031 implements Runnable {
        C00031() {
        }

        public void run() {
            SplashScreenPanic.this.startActivity(new Intent(SplashScreenPanic.this.getApplicationContext(), MainPage.class));
            SplashScreenPanic.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new C00031(), 2500);
    }
}