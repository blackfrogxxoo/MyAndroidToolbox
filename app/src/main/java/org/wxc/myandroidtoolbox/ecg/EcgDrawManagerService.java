package org.wxc.myandroidtoolbox.ecg;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.wxc.myandroidtoolbox.ecg.cache.DeinterleaverCache;
import org.wxc.myandroidtoolbox.ecg.cache.QueueDataHolder;
import org.wxc.myandroidtoolbox.ecg.draw.EcgDraw;
import org.wxc.myandroidtoolbox.ecg.draw.IEcgDrawManager;
import org.wxc.myandroidtoolbox.ecg.draw.IOnEcgDrawListener;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.functions.Action1;

/**
 * Created by wxc on 2016/10/3.
 */
public class EcgDrawManagerService extends Service{
    private static final String TAG = "EcgDrawManagerService";

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private RemoteCallbackList<IOnEcgDrawListener> mEcgListenerlList = new RemoteCallbackList<>();
    private Binder mBinder = new IEcgDrawManager.Stub() {

        @Override
        public void setRunning(boolean start) throws RemoteException {
            if(start) {
                new Thread(new ServiceWorker()).start();
            } else {
                mIsServiceDestroyed.set(true);
            }
        }

        @Override
        public void registerListener(IOnEcgDrawListener listener) throws RemoteException {
            mEcgListenerlList.register(listener);
        }

        @Override
        public void unregisterListener(IOnEcgDrawListener listener) throws RemoteException {
            mEcgListenerlList.unregister(listener);
        }
    };
    private DeinterleaverCache mDeinterleaverCache;
    private QueueDataHolder mQueueDataHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        mDeinterleaverCache = new DeinterleaverCache(new DeinterleaverCache.CacheListener() {
            @Override
            public void onProcess(short[] processData) {
                mQueueDataHolder.offerData(processData);
            }

            @Override
            public void onNotProcess(int sequenceId) {

            }

            @Override
            public void onPacketLossGetten(float rate) {

            }
        });
        mQueueDataHolder = new QueueDataHolder(new Action1<int[]>() {
            @Override
            public void call(int[] ints) {
                EcgDraw ecgDraw = new EcgDraw(ints);
                final int N = mEcgListenerlList.beginBroadcast();
                for(int i=0; i<N; i++) {
                    IOnEcgDrawListener l = mEcgListenerlList.getBroadcastItem(i);
                    if(l != null) {
                        try {
                            l.onEcgDraw(ecgDraw);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mEcgListenerlList.finishBroadcast();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mQueueDataHolder.close();
        mDeinterleaverCache.close();
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private void onNewEcgReceived(EcgPacket model) throws RemoteException {
        mDeinterleaverCache.setMeasurementsAndSequenceID(model.getECGmeasurements(), model.getSequenceID());

    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            byte i = 0;
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] ori = PacketFactory.BYTE_ECG2;
                ori[ori.length-1] = i;
                i++;
                if(i==32) {
                    i = 0;
                }
                if(i == 0) {
                    Log.i(TAG, "run: thread name:" + Thread.currentThread().getName());
                    Log.i(TAG, "delay: " + mQueueDataHolder.getDelayString());
                }
                EcgPacket newModel = PacketFactory.fromByteArray(ori);
                try {
                    onNewEcgReceived(newModel);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
