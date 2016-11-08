package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wxc on 2016/11/8.
 */

public class InterpolationTest {
    public static void main(String[] args) {
        final DeinterleaverCache cache = new DeinterleaverCache(new DeinterleaverCache.CacheListener() {
            @Override
            public void onProcess(short[] processData) {
                System.out.println("processed: " + Arrays.toString(processData));
            }

            @Override
            public void onNotProcess(int sequenceId) {

            }

            @Override
            public void onPacketLossGetten(float rate) {

            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                int id = 0;
                int times = 0;
                List<Integer> lostSequenceIds = new ArrayList<>();
                while (true){
                    if(id == 32) {
                        id = 0;
//                        System.out.println(Arrays.toString(lostSequenceIds.toArray()));
                        lostSequenceIds.clear();
                    }
                    if(Math.random() < 0.9) {
                        short[] data = DataFactory.getPacket(id);
                        cache.setMeasurementsAndSequenceID(data, id);
                    } else {
                        lostSequenceIds.add(id);
                    }
                    id++;
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    times++;
                }
            }
        };
        thread.start();

    }
}
