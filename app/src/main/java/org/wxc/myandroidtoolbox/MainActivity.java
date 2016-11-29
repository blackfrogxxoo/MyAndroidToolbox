package org.wxc.myandroidtoolbox;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.wxc.myandroidtoolbox.model.ModelParcelable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MediaPlayer s1Player;
    MediaPlayer s2Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        s1Player = MediaPlayer.create(this, R.raw.high_prio);
        s2Player = MediaPlayer.create(this, R.raw.mid_prio);
        testModelParcelable();
        s1Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                new Thread("s1Player"){
                    @Override
                    public void run() {
                        if(readyToPauseS1) {
                            readyToPauseS1 = false;
                            return;
                        }
                        s1Waiting = true;
                        for(int i=0;i<3;i++) {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(readyToPauseS1) {
                                s1Waiting = false;
                                readyToPauseS1 = false;
                                return;
                            }
                        }

                        if(MainActivity.this.isFinishing() || !s1Waiting) {
                            return;
                        }
                        s1Waiting = false;
                        s1Player.start();
                    }
                }.start();
            }
        });
        s2Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                new Thread("s2Player"){
                    @Override
                    public void run() {
                        if(readyToPauseS2) {
                            readyToPauseS2 = false;
                            return;
                        }
                        s2Waiting = true;
                        for(int i=0;i<6;i++) {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(readyToPauseS2) {
                                s2Waiting = false;
                                readyToPauseS2 = false;
                                return;
                            }
                        }

                        if(MainActivity.this.isFinishing() || !s2Waiting) {
                            return;
                        }
                        s2Waiting = false;

                        s2Player.start();
                    }
                }.start();
            }
        });
    }

    private boolean readyToPauseS1;
    private boolean readyToPauseS2;
    private boolean s1Waiting;
    private boolean s2Waiting;
    private boolean isInCalling;

    public void repeatS1(View view) {
        if(isInCalling) {
            return;
        }
        if(s1Waiting || s1Player.isPlaying()) {
            return;
        }

        if(s2Waiting || s2Player.isPlaying()) {
            s2Waiting = false;
            if(s2Player.isPlaying()) {
                s2Player.seekTo(0);
                s2Player.pause();
            }
        }
        s1Player.start();
    }

    public void repeatS2(View view) {
        if(isInCalling) {
            return;
        }
        if(s2Waiting || s2Player.isPlaying()) {
            return;
        }

        if(s1Waiting || s1Player.isPlaying()) {
            s1Waiting = false;
            if(s1Player.isPlaying()) {
                s1Player.seekTo(0);
                s1Player.pause();
            }
        }
        s2Player.start();
    }

    public void stopAlarm(View view) {
        if(isInCalling) {
            return;
        }
        preparePause();
    }

    private void preparePause() {
        if(s1Waiting || s1Player.isPlaying()) {
            readyToPauseS1 = true;
        }
        if (s2Waiting || s2Player.isPlaying()) {
            readyToPauseS2 = true;
        }
    }

    public void inCall(View view) {
        offCall(view);
        repeatS1(view);
        isInCalling = true;
    }

    public void offCall(View view) {
        totallyPause();
        isInCalling = false;
    }

    private void totallyPause() {
        preparePause();
        s1Waiting = false;
        if(s1Player.isPlaying()) {
            s1Player.seekTo(0);
            s1Player.pause();
        }
        s2Waiting = false;
        if(s2Player.isPlaying()) {
            s2Player.seekTo(0);
            s2Player.pause();
        }
    }

    private void testModelParcelable() {
        ModelParcelable model = new ModelParcelable(1, "Test Tag", true, new ModelParcelable.Book());
        Bundle bundle = new Bundle();
        bundle.putParcelable("model", model);

        ModelParcelable newModel = bundle.getParcelable("model");
        Log.i(TAG, "testModelParcelable: " + newModel);
    }

    public void testDaemonThread(View view) {
        // 事实证明，只是通过back退出app后，所谓的daemon线程仍然在运行
        // standard Java shutdown hooks are not guaranteed on this platform


        // ****以下方式启动的线程，在某些手机上，在app切换到后台后，休眠时间变长
//        startThread(false);
//        startInnerTimer();
//        startOutterTimer();
//        startExecutor();
//        startScheduleWithFixedDelay();

        // ****以下方式启动的线程，在某些手机上，在app切换到后台后，休眠时间总体上未变长，但不太稳定
        startScheduleAtFixedRate();
    }

    private static class SleepRunnable extends TimerTask {

        @Override
        public void run() {
            long lastMills = System.currentTimeMillis();
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long now = System.currentTimeMillis();
                long cost = now - lastMills;
                lastMills = now;
                Log.i(TAG, "run: " + Thread.currentThread().getName() + " cost:" + cost + " ms");
            }
        }
    }

    private static long lastMills;
    private static class SleepOnceRunnable implements Runnable {

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            long cost = now - lastMills;
            lastMills = now;
            Log.i(TAG, "run: " + Thread.currentThread().getName() + " cost:" + cost + " ms");

        }
    }

    private void startThread(boolean isDaemon) {
        String name = isDaemon ? "Test daemon" : "Test not daemon";
        Thread thead = new Thread(new SleepRunnable(), name);

        thead.setDaemon(isDaemon);
        thead.start();
    }

    private void startInnerTimer() {
        Timer timer = new Timer("Test Inner Timer");
        TimerTask task = new SleepRunnable();
        timer.schedule(task,0);
    }

    private void startOutterTimer() {
        Timer timer = new Timer("Test Outter Timer");
        lastMills = System.currentTimeMillis();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long cost = now - lastMills;
                lastMills = now;
                Log.i(TAG, "run: " + Thread.currentThread().getName() + " cost:" + cost + " ms");
            }
        };

        timer.schedule(task, 0, 100);
    }

    private void startExecutor() {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new SleepRunnable());
        exec.shutdown();
    }

    private void startScheduleAtFixedRate() {

        final ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(2);
        // 1秒钟后运行，并每隔2秒运行一次
        final ScheduledFuture beeperHandle = scheduler.scheduleAtFixedRate(
                new SleepOnceRunnable(), 0, 100, MILLISECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
                scheduler.shutdown();
            }
        }, 30, SECONDS);
    }

    private void startScheduleWithFixedDelay() {

        final ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(2);
        final ScheduledFuture beeperHandle2 = scheduler
                .scheduleWithFixedDelay(new SleepOnceRunnable(), 0, 100, MILLISECONDS);
        // 30秒后结束关闭任务，并且关闭Scheduler
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle2.cancel(true);
                scheduler.shutdown();
            }
        }, 30, SECONDS);
    }

}
