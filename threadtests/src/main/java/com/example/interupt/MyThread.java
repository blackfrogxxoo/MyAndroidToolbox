package com.example.interupt;

/**
 * Created by wxc on 2016/11/5.
 */

public class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        for (int i=0; i<5000; i++) {
//            System.out.println("i=" + (i + 1));
            // do sth
        }
    }

    public static void main(String[] args) {
//        testInterupt(true);
        // output:
//        thread.interrupted() = true
//        thread.interrupted() = false
//        end!

//        testInterupt(false);
        // output:
//        thread.interrupted() = false
//        thread.interrupted() = false
//        end!

//        testIsInterupted(false);
        // output:
//        thread.isInterrupted() = true
//        thread.isInterrupted() = true
//        end!

        testIsInterupted(true);
        // output:
//        thread.isInterrupted() = false
//        thread.isInterrupted() = false
//        end!
    }

    private static void testInterupt(boolean isMainInterupted) {
        MyThread thread = new MyThread();
        thread.start();
//        try {
//            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(isMainInterupted) {
            Thread.currentThread().interrupt();
            System.out.println("Thread.interrupted() = " + Thread.interrupted());
            System.out.println("Thread.interrupted() = " + Thread.interrupted());
        } else {
            thread.interrupt();
            System.out.println("thread.isAlive() = " + thread.isAlive());
            System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
            System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
        }
        System.out.println("end!");
    }

    private static void testIsInterupted(boolean wait) {
        try {
            MyThread thread = new MyThread();
            thread.start();
            if(wait) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            thread.interrupt();
            System.out.println("thread.isAlive() = " + thread.isAlive());
            System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
            System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end!");
    }
}
