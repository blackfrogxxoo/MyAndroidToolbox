package com.example;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.text.DecimalFormat;

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
    private CacheListener listener;
    private double[] lastData = new double[4];

    public DeinterleaverCache(CacheListener listener) {
        this.listener = listener;
        sequenceIDArray = new boolean[32];
        processData = new short[288];
    }

    public void setMeasurementsAndSequenceID(short[] measurements, int sequenceID) {

        if(sequenceID == 0 || lastSequenceID > sequenceID){
            process();
        } else {
            listener.onNotProcess(sequenceID);
        }

        for(int i=0;i<9;i++) {
            processData[i*32 + sequenceID] = measurements[i];
        }

        sequenceIDArray[sequenceID] = true;

        lastSequenceID = sequenceID;
    }

    private void process() {
//        LogUtil.i("test", Arrays.toString(sequenceIDArray));
//        LogUtil.i(TAG, Arrays.toString(processData));
//        processData = interpolation(sequenceIDArray, processData);
        boolean success = interpolation();
        if(!success) {
            return;
        }

        //if(dataHolder!=null)
        //    dataHolder.offerData(processData);

//        LogUtil.i(TAG, Arrays.toString(processData));

        // TODO 统计掉包个数
        packageCount ++;
        for(int i=0;i<sequenceIDArray.length;i++) {
            if(!sequenceIDArray[i]){
                lostCount ++;
            }
        }

        if(packageCount == 6) {
            listener.onPacketLossGetten(lostCount*100f/packageCount/32);
//            Log.i(TAG, "掉包率：" + lostCount*100f/packageCount/32);
//            lostView.setText("掉包率：" + format.format(lostCount*100f/packageCount/32) + "%");
            packageCount = 0;
            lostCount = 0;
        }


        listener.onProcess(processData);

        initProcessData();
    }

    private void initProcessData() {
//        processData = new short[288];
//        sequenceIDArray = new boolean[32];
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

//        System.out.println("inputIndexs:" + Arrays.toString(inputIndexs));
//        System.out.println("inputData:" + Arrays.toString(inputData));


        //interpolation example
        PolynomialSplineFunction innerFun = (new SplineInterpolator()).interpolate(inputIndexs,inputData);

        //extrapolation example
        PolynomialFunctionLagrangeForm outerFun = new PolynomialFunctionLagrangeForm(inputIndexs,inputData);

        // 插值
        double maxReceivedIndex = inputIndexs[inputIndexs.length - 1];
        for(int i=0;i<288;i++) {
            if(!isReceived(i)) {
                boolean hasNext = i < maxReceivedIndex;
                if(hasNext) {
                    processData[i] = (short) innerFun.value(i);
                } else {
                    processData[i] = (short) outerFun.value(i);
                }
            }
        }
        System.out.println("time:" + (System.currentTimeMillis() - time));
        return true;
    }

    private boolean isReceived(int positon) {
        return sequenceIDArray[positon%32];
    }

    private short[] interpolation(boolean[] lost, short[] data) {
        for(int i=0;i<32;i++) {
            if(!lost[i]) { // 丢包
                for(int j=0;j<9;j++) {
                    data[j*32 + i] = interpolation(data, lost, j*32+i);
                }

            }
        }
        return data;
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

    public interface CacheListener{
        void onProcess(short[] processData);

        void onNotProcess(int sequenceId);

        void onPacketLossGetten(float rate);
    }
}
