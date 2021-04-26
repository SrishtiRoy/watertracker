package com.shiva.hydra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static java.lang.Thread.sleep;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread myThread = new Thread(new Runnable() {
            public void run() {

                try {
                    sleep(1000);
                    Intent splashIntent = new Intent(getApplicationContext(),InitUserInfoActivity.class);
                    startActivity(splashIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



        myThread.start();



    }}