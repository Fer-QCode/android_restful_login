package com.fujitsu.tmmin.central;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Imam Ibnu on 23/04/2016.
 */
public class splashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Thread timerThread = new Thread(){
            public  void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(splashScreen.this,CENTRAL010102W.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
