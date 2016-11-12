package com.example;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by wxc on 2015/11/20.
 */
public class DeinterleaverCache {
    private static final String TAG = "DeinterleaverCache";
    private boolean[] sequenceIDArray;
    private short[] processData;
    private int lastSequenceID = -1;
    private int packageCount;
    private int lostCount;
    private DecimalFormat format = new DecimalFormat("###.##");
    private double[] lastData = new double[4];

    public DeinterleaverCache() {
        sequenceIDArray = new boolean[32];
        processData = new short[288];
    }

    public short[] setMeasurementsAndSequenceID(short[] measurements, int sequenceID) {

        short[] result = null;
        if(sequenceID == 0 || lastSequenceID > sequenceID){
            result = process();
        }

        for(int i=0;i<9;i++) {
            processData[i*32 + sequenceID] = measurements[i];
        }

        sequenceIDArray[sequenceID] = true;

        lastSequenceID = sequenceID;

        return result;
    }

    private short[] process() {
        boolean success = interpolation();
        if(!success) {
            return null;
        }

        short[] result = new short[processData.length];
        System.arraycopy(processData, 0, result, 0, 288);

        initProcessData();

        System.out.println(Arrays.toString(result));
        System.out.println(Thread.currentThread().getName());
        return result;
    }

    private void initProcessData() {
        for(int i=0;i<288;i++)  {
            processData[i] = 0;
        }
        for(int i=0;i<32;i++)  {
            sequenceIDArray[i] = false;
        }
    }

    private boolean interpolation() {
        long time = System.currentTimeMillis();
        int receivedPacketCount = 0;


        for(boolean b : sequenceIDArray) {
            if(b) {
                receivedPacketCount ++;
            }
        }

        if(receivedPacketCount == 0) {  // 第一个数据包
            return false;
        }

        System.out.println("receivedPacketCount:" + receivedPacketCount);

        double[] inputIndexs = new double[receivedPacketCount*9 + 4];
        double[] inputData = new double[receivedPacketCount*9 + 4];
        for(int i=0;i<4;i++) {
            inputIndexs[i] = -4 + i;
            inputData[i] = lastData[i];
        }

        // 赋值
        int index = 0;
        for(int i=0;i<288;i++) {
            if(isReceived(i)) {
                inputData[index + 4] = processData[i];
                inputIndexs[index + 4] = i;
                index ++;
            }
        }

        //interpolation example
        PolynomialSplineFunction innerFun = (new SplineInterpolator()).interpolate(inputIndexs,inputData);

        // 插值
        double maxReceivedIndex = inputIndexs[inputIndexs.length - 1];
        for(int i=0;i<288;i++) {
            if(!isReceived(i)) {
                boolean hasNext = i < maxReceivedIndex;
                if(hasNext) {
                    processData[i] = (short) innerFun.value(i);
                } else {
                    processData[i] = processData[i-1];
                }
            }
        }
        return true;
    }

    private boolean isReceived(int positon) {
        return sequenceIDArray[positon%32];
    }

    private boolean interpolation1() {

        int receivedPacketCount = 0;


        for(boolean b : sequenceIDArray) {
            if(b) {
                receivedPacketCount ++;
            }
        }

        if(receivedPacketCount == 0) {  // 第一个数据包
            return false;
        }
        for(int i=0;i<32;i++) {
            if(!sequenceIDArray[i]) { // 丢包
                for(int j=0;j<9;j++) {
                    processData[j*32 + i] = interpolation(processData, sequenceIDArray, j*32+i);
                }

            }
        }
        return true;
    }

    private short interpolation(short data[],boolean[] lost, int position) {
        short pre = 0, next = 0;
        boolean noPre = false, noNext = false;
        int preIndex, nextIndex;
        for(preIndex = position-1;preIndex>=0;preIndex--) {
            if(lost[preIndex%32]) {
                pre = data[preIndex];

                break;
            }
        }
        if(preIndex == -1) {
            noPre = true;
        }

        for(nextIndex = position+1;nextIndex<data.length;nextIndex++) {
            if(lost[nextIndex%32]) {
                next = data[nextIndex];

                break;
            }
        }
        if(nextIndex == 288) {
            noNext = true;
        }

        if(noPre && noNext) {
            return 0;
        } else if(!noPre && noNext) {
            return pre;
        } else if(!noNext && noPre) {
            return next;
        } else {
            return (short)((pre+next) / 2);
        }
    }

    public void close() {
        initProcessData();
    }
}
