package com.example.timer;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by wxc on 2016/11/6.
 */

public class ImmediateTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Immediate begin timer = " + new Date());
        System.out.println("Immediate   end timer = " + new Date());
    }
}
