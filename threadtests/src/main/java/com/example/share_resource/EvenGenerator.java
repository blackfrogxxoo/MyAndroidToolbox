package com.example.share_resource;

/**
 * Created by wxc on 2016/11/5.
 */

public class EvenGenerator extends IntGenerator {
    private int currentEvenValue = 0;
    @Override
    public int next() {
        for(int i=0;i<100;i++) {
            ++currentEvenValue; // Danger point here!
        }
        return currentEvenValue;
    }

    public static void main(String[] args) {
        EvenChecker.test(new EvenGenerator());
    }
}
