package com.one.sakap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Narinc on 26.8.2015.
 */
public class LoginScreen extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Thread zamanlayici = new Thread() {

            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(nextScreen);
                }
            }
        };

        zamanlayici.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
