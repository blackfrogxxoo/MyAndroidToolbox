package com.example.singleton;


/**
 * Created by wxc on 2016/11/6.
 */

public class TestInstance {
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println(DifferentSingleton.SynchronizedLanHan.getInstace().toString());
            System.out.println(DifferentSingleton.FixedSynchronizedLanHan.getInstace().toString());
        }
    }

    // output:
//    com.example.singleton.DifferentSingleton$SynchronizedLanHan@20279922
//    com.example.singleton.DifferentSingleton$SynchronizedLanHan@21415cb9
//    com.example.singleton.DifferentSingleton$SynchronizedLanHan@23fcb7ef
//    com.example.singleton.DifferentSingleton$FixedSynchronizedLanHan@2ee06356
//    com.example.singleton.DifferentSingleton$FixedSynchronizedLanHan@2ee06356
//    com.example.singleton.DifferentSingleton$FixedSynchronizedLanHan@2ee06356
//
//    Process finished with exit code 0
    public static void main(String[] args) {
        MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();
        MyThread thread3 = new MyThread();
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
