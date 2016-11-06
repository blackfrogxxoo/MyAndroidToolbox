package com.example.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created by wxc on 2016/11/6.
 */

public class TestScheduleAtFixedRate {
    public static void main(String[] args) {
//        testFixedRate(false);
        // output:Current time: Sun Nov 06 22:05:48 CST 2016
//        Plan time: Sun Nov 06 22:05:28 CST 2016
//        Immediate begin timer = Sun Nov 06 22:05:48 CST 2016
//        Immediate   end timer = Sun Nov 06 22:05:48 CST 2016
//        Immediate begin timer = Sun Nov 06 22:05:50 CST 2016
//        Immediate   end timer = Sun Nov 06 22:05:50 CST 2016
//        Immediate begin timer = Sun Nov 06 22:05:52 CST 2016
//        Immediate   end timer = Sun Nov 06 22:05:52 CST 2016
//        Immediate begin timer = Sun Nov 06 22:05:54 CST 2016
//        Immediate   end timer = Sun Nov 06 22:05:54 CST 2016
//        Immediate begin timer = Sun Nov 06 22:05:56 CST 2016
//        Immediate   end timer = Sun Nov 06 22:05:56 CST 2016
//        ...

        testFixedRate(true);
        // output:
//        Current time: Sun Nov 06 22:06:31 CST 2016
//        Plan time: Sun Nov 06 22:06:11 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:31 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:33 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:33 CST 2016
//        Immediate begin timer = Sun Nov 06 22:06:35 CST 2016
//        Immediate   end timer = Sun Nov 06 22:06:35 CST 2016
//        ...
    }

    private static void testFixedRate(boolean fix) {
        ImmediateTask task = new ImmediateTask();
        System.out.println("Current time: " + new Date());
        Calendar calendarRef = Calendar.getInstance();
        calendarRef.add(Calendar.SECOND, -20);
        Date runDate = calendarRef.getTime();
        System.out.println("Plan time: " + runDate);
        Timer timer = new Timer();
        if(fix) {
            timer.scheduleAtFixedRate(task, runDate, 2000);
            // FIXME: scheduleAtFixedRate具有追赶性，
            // 即本来要在某个时间执行的任务要强行补上，最终保证每个任务都尽量在对应的时间执行
        } else {
            timer.schedule(task, runDate, 2000);
        }
    }
}
