package com.example.share_resource;

/**
 * Created by wxc on 2016/11/5.
 */

public class SynchronizedEvenGenerator extends IntGenerator {
    private int currentEvenValue = 0;
    @Override
    public synchronized int next() { // Now safe
        for(int i=0;i<100;i++) {
            ++currentEvenValue;
        }
        return currentEvenValue;
    }

    public static void main(String[] args) {
        EvenChecker.test(new SynchronizedEvenGenerator());
    }
}
