package com.example.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wxc on 2016/11/6.
 */

public class SimpleTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Task executed, time: " + new Date());
    }

    public static void main(String[] args) {
//        afterCurrent(true);
        // output:
//        Current time: Sun Nov 06 21:06:06 CST 2016
//
//        Process finished with exit code 0

//        afterCurrent(false); // Timer默认为非Deamon线程，导致进程未销毁
        // output:
//        Current time: Sun Nov 06 21:06:54 CST 2016
//        Task executed, time: Sun Nov 06 21:06:55 CST 2016

//        beforeCurrent(true);
        // output:
//        Current time: Sun Nov 06 21:09:43 CST 2016
//        Task executed, time: Sun Nov 06 21:09:43 CST 2016
//
//        Process finished with exit code 0

//        beforeCurrent(false);
        // output:
//        Current time: Sun Nov 06 21:10:17 CST 2016
//        Task executed, time: Sun Nov 06 21:10:17 CST 2016

//        multiTasks();
        // output:
//        Current time: Sun Nov 06 21:31:33 CST 2016
//        Plan time: Sun Nov 06 21:31:43 CST 2016
//        Plan time: Sun Nov 06 21:31:48 CST 2016
//        Task executed, time: Sun Nov 06 21:31:43 CST 2016
//        Task executed, time: Sun Nov 06 21:31:48 CST 2016

        testLoop();
        // output:
//        Current time: Sun Nov 06 21:32:17 CST 2016
//        Plan time: Sun Nov 06 21:32:27 CST 2016
//        Task executed, time: Sun Nov 06 21:32:27 CST 2016
//        Task executed, time: Sun Nov 06 21:32:31 CST 2016
//        Task executed, time: Sun Nov 06 21:32:35 CST 2016
//        ...

    }

    private static void afterCurrent(boolean isDeamon) {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef = Calendar.getInstance();
        calendarRef.add(Calendar.SECOND, 1);
        Date runDate = calendarRef.getTime();

        SimpleTask task = new SimpleTask();
        Timer timer = new Timer(isDeamon);
        timer.schedule(task, runDate);
    }

    // 执行任务的时间早于当前时间，则立即执行task任务
    private static void beforeCurrent(boolean isDeamon) {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef = Calendar.getInstance();
        calendarRef.add(Calendar.SECOND, -1);
        Date runDate = calendarRef.getTime();

        SimpleTask task = new SimpleTask();
        Timer timer = new Timer(isDeamon);
        timer.schedule(task, runDate);
    }

    private static void multiTasks() {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef1 = Calendar.getInstance();
        calendarRef1.add(Calendar.SECOND, 10);
        Date runDate1 = calendarRef1.getTime();
        System.out.println("Plan time: " + runDate1);

        Calendar calendarRef2 = Calendar.getInstance();
        calendarRef2.add(Calendar.SECOND, 15);
        Date runDate2 = calendarRef2.getTime();
        System.out.println("Plan time: " + runDate2);

        SimpleTask task1 = new SimpleTask();
        SimpleTask task2 = new SimpleTask();

        Timer timer = new Timer();
        timer.schedule(task1, runDate1);
        timer.schedule(task2, runDate2);
    }

    public static void testLoop() {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef = Calendar.getInstance();
        calendarRef.add(Calendar.SECOND, 10);
        Date runDate = calendarRef.getTime();
        System.out.println("Plan time: " + runDate);
        SimpleTask task = new SimpleTask();
        Timer timer = new Timer();
        timer.schedule(task, runDate, 4000);
    }
}
