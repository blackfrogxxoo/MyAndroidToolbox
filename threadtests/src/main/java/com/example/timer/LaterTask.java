package com.example.timer;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by wxc on 2016/11/6.
 */

public class LaterTask extends TimerTask {
    @Override
    public void run() {
        try {
            System.out.println("Later begin timer = " + new Date());
            Thread.sleep(20000);
            System.out.println("Later   end timer = " + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
