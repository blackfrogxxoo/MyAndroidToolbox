package org.wxc.myandroidtoolbox.ecg.cache;

import android.os.Process;

import java.text.DecimalFormat;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by wxc on 2015/10/21.
 */
public class QueueDataHolder {
    private static final int MAX_LENGTH = 5000;
    private static final int MIN_LENGTH = 100;
    private static final String TAG = "QueueDataHolder";


    private Queue<Integer> dataQueue;

    private static final int SKIP_COUNT = 0;

    private int lastData;
    private boolean isNotifying;
    private static final int INTERVAL = 20;
    private boolean beginDraw;
    private long lastOfferTime;
    private long lastPollTime;

    private DecimalFormat format = new DecimalFormat("###.##");

    public QueueDataHolder(final Action1<int[]> drawAction) {
        isNotifying = true;
        dataQueue = new LinkedBlockingDeque<>();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Process.setThreadPriority(-20);
                while (isNotifying && dataQueue != null) {
                    synchronized (this) {
                        if (dataQueue != null && dataQueue.size() > 10 && beginDraw) {
                            try {
                                int[] dataArray = new int[EcgScanView.ONCE_DRAW_COUNT +
                                        1];
                                for (int i = 0; i <EcgScanView.ONCE_DRAW_COUNT; i++) {
                                    if (SKIP_COUNT > 0)
                                        for (int j = 0; j < SKIP_COUNT; j++) {
                                            dataQueue.poll();
                                        }
                                    Integer integer = dataQueue.poll();
                                    if (integer != null) {
                                        dataArray[i + 1] = integer.shortValue();
                                    }
                                }
                                dataArray[0] = lastData;
                                lastData = dataArray[EcgScanView.ONCE_DRAW_COUNT];
                                long time = System.currentTimeMillis();
//                                Log.i(TAG, "Send to view interval:" + (time - lastPollTime) + ", count=" + dataQueue.size());
                                lastPollTime = time;

                                Observable.just(dataArray)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(drawAction);
                                if (dataQueue != null) {
                                    this.wait(calculateInterval());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
//        thread.setPriority(-20);
        thread.start();
    }

    public String getDelayString() {
        if (dataQueue == null) {
            return "";
        }

        return format.format(((float) dataQueue.size()) / 512f);
    }

    private int calculateInterval() {
        int k = 8;
        int j = 10;
        int y = 0;
        if (dataQueue != null) {
            y = (int) (INTERVAL + k * (0.5f - 1 / (1 + Math.exp(
                    (MIN_LENGTH - dataQueue.size()) / j))));
        }
//        Log.i(TAG, "y=" + y);
        if(y < 1) {
            y = 1;
        }
        return y;
    }

    public void offerData(byte[] items) {
        if (dataQueue != null) {
//            LogUtil.i(TAG, "offerData ");
            for (short item : items) {
                if (dataQueue.size() > MAX_LENGTH) {
                    dataQueue.poll();
                }
                dataQueue.offer((int) item);
            }

        }
    }

    public void offerData(short[] items) {
        if (dataQueue != null) {
            long time = System.currentTimeMillis();
//            LogUtil.i(TAG, "offerData 用时：" + (time - lastOfferTime));
            lastOfferTime = time;
            for (short item : items) {
                if (dataQueue.size() > MAX_LENGTH) {
                    dataQueue.poll();
                }
                dataQueue.offer((int) item);
            }

            /**
             * 可以画
             */
            if (dataQueue.size() > MIN_LENGTH) {
                beginDraw = true;
            }

        }
    }

    public synchronized void close() {
//        LogUtil.e(TAG, "close()");
        isNotifying = false;
        if (dataQueue != null) {
            dataQueue.clear();
            dataQueue = null;
        }
    }

    public synchronized void refresh() {
        if (dataQueue != null) {
            dataQueue.clear();
        }
    }

    public void offerData(int data) {
        if (dataQueue != null) {
            long time = System.currentTimeMillis();
//            LogUtil.i(TAG, "offerData 用时：" + (time - lastOfferTime));
            lastOfferTime = time;
            if (dataQueue.size() > MAX_LENGTH) {
                dataQueue.poll();
            }
            dataQueue.offer(data);

            /**
             * 可以画
             */
            if (dataQueue.size() > MIN_LENGTH) {
                beginDraw = true;
            }

        }
    }
}
