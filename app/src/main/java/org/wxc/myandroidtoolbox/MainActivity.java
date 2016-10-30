package org.wxc.myandroidtoolbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.wxc.myandroidtoolbox.ble.BleActivity;
import org.wxc.myandroidtoolbox.ipc.ModelParcelable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                startActivity(new Intent(getApplicationContext(), BleActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        testModelParcelable();
    }

    private void testModelParcelable() {
        ModelParcelable model = new ModelParcelable(1, "Test Tag", true, new ModelParcelable.Book());
        Bundle bundle = new Bundle();
        bundle.putParcelable("model", model);

        ModelParcelable newModel = bundle.getParcelable("model");
        Log.i(TAG, "testModelParcelable: " + newModel);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
