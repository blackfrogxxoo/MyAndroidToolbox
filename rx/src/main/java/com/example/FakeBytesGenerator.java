package com.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by black on 2016/11/11.
 */

public class FakeBytesGenerator {
    private static boolean isRunning;

    static interface Listener {
        void onBytes(byte[] bytes);
    }

    public static void start(final Listener listener) {
        System.out.print("Running");
        isRunning = true;
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
        // 模拟Binder线程池
        exec.scheduleAtFixedRate(() -> {
            if(!isRunning) {
                exec.shutdown();
            }
            listener.onBytes(new byte[]{0x01});
        }, 0, 20, TimeUnit.MILLISECONDS);
    }

    public static void stop() {
        System.out.println("Finish.");
        isRunning = false;
    }

    public static void main(String[] args) {
        start(new Listener() {
            int i=0;
            @Override
            public void onBytes(byte[] bytes) {
                i++;
                if(i % 100 == 0) {
                    System.out.print('.');
                }
            }
        });

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop();
    }
}
