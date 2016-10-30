package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wxc on 2016/10/30.
 */

public class CachedThreadPool {
    public static void main(String[] args) {
        // 基本的线程池
//        cachedThreadPool();

        // 指定线程数量
//        fixedThreadPool();

        // 指定线程数量为1
        singleThreadPool();
    }

    private static void cachedThreadPool() {
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i=0; i<5; i++) {
            exec.execute(new LiftOff());
        }
        exec.shutdown();
    }

    private static void fixedThreadPool(){
        ExecutorService exec = Executors.newFixedThreadPool(1);
        for(int i=0; i<5; i++) {
            exec.execute(new LiftOff());
        }
        exec.shutdown();
    }

    private static void singleThreadPool(){
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for(int i=0; i<5; i++) {
            exec.execute(new LiftOff());
        }
        exec.shutdown();
    }
}
