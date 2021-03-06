package org.wxc.myandroidtoolbox.ecg.cache;

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
        processData = interpolation(sequenceIDArray, processData);

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
