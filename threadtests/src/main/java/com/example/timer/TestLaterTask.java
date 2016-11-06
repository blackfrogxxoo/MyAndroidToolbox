package com.example.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created by wxc on 2016/11/6.
 *
 * 多个Task在Timer中是按队列排队执行的，后执行的计划时间如果早于排到队的时间，则立即执行
 */

public class TestLaterTask {
    // output:
//    Current time: Sun Nov 06 21:33:36 CST 2016
//    Later task plan time: Sun Nov 06 21:33:36 CST 2016
//    Immediate task plan time: Sun Nov 06 21:33:46 CST 2016
//    Later begin timer = Sun Nov 06 21:33:36 CST 2016
//    Later   end timer = Sun Nov 06 21:33:56 CST 2016
//    Immediate begin timer = Sun Nov 06 21:33:56 CST 2016
//    Immediate   end timer = Sun Nov 06 21:33:56 CST 2016

    public static void main(String[] args) {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef1 = Calendar.getInstance();
        Date runDate1 = calendarRef1.getTime();
        System.out.println("Later task plan time: " + runDate1);

        Calendar calendarRef2 = Calendar.getInstance();
        calendarRef2.add(Calendar.SECOND, 10);
        Date runDate2 = calendarRef2.getTime();
        System.out.println("Immediate task plan time: " + runDate2);

        LaterTask task1 = new LaterTask();
        ImmediateTask task2 = new ImmediateTask();

        Timer timer = new Timer();
        timer.schedule(task1, runDate1);
        timer.schedule(task2, runDate2);
    }
}
