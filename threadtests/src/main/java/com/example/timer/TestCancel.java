package com.example.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wxc on 2016/11/6.
 */

public class TestCancel {
    static class CancelSelfTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("CancelSelfTask run time: " + new Date());
            cancel();
            System.out.println("CancelSelfTask cancel itself");
        }
    }

    // output:
//    Current time: Sun Nov 06 21:50:20 CST 2016
//    Plan time: Sun Nov 06 21:50:20 CST 2016
//    CancelSelfTask run time: Sun Nov 06 21:50:20 CST 2016
//    CancelSelfTask cancel itself
//    Task executed, time: Sun Nov 06 21:50:20 CST 2016
//    Task executed, time: Sun Nov 06 21:50:22 CST 2016
//    Task executed, time: Sun Nov 06 21:50:24 CST 2016
//    Task executed, time: Sun Nov 06 21:50:26 CST 2016
//    Task executed, time: Sun Nov 06 21:50:28 CST 2016
//
//    Process finished with exit code 0
    public static void main(String[] main) throws InterruptedException {
        System.out.println("Current time: " + new Date());
        Calendar calendarRef1 = Calendar.getInstance();
        Date runDate1 = calendarRef1.getTime();
        System.out.println("Plan time: " + runDate1);
        CancelSelfTask cancelSelfTask = new CancelSelfTask();
        SimpleTask simpleTask = new SimpleTask();
        Timer timer = new Timer();
        timer.schedule(cancelSelfTask, runDate1, 2000);
        timer.schedule(simpleTask, runDate1, 2000);
        Thread.sleep(10000);
        timer.cancel(); // FIXME: 有时cancel()没有争抢到锁，就不会停止计划任务，而是正常执行
    }
}
