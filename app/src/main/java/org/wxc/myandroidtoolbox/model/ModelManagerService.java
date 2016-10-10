package org.wxc.myandroidtoolbox.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.wxc.myandroidtoolbox.aidl.IModelManager;
import org.wxc.myandroidtoolbox.aidl.IOnNewModelListener;
import org.wxc.myandroidtoolbox.aidl.Model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wxc on 2016/10/3.
 */
public class ModelManagerService extends Service {
    private static final String TAG = "ModelManagerService";

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Model> mModelList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewModelListener> mModeListenerlList = new RemoteCallbackList<>();
    private Binder mBinder = new IModelManager.Stub() {

        @Override
        public void addModel(Model model) throws RemoteException {
            mModelList.add(model);
        }

        @Override
        public List<Model> getModelList() throws RemoteException {
            return mModelList;
        }

        @Override
        public void registerListener(IOnNewModelListener listener) throws RemoteException {
            mModeListenerlList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewModelListener listener) throws RemoteException {
            mModeListenerlList.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mModelList.add(new Model(1, "Item 1"));
        mModelList.add(new Model(2, "Item 2"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onNewModelAdded(Model model) throws RemoteException {
        mModelList.add(model);
        final int N = mModeListenerlList.beginBroadcast();
        for(int i=0; i<N; i++) {
            IOnNewModelListener l = mModeListenerlList.getBroadcastItem(i);
            if(l != null) {
                l.onNewModelAdded(model);
            }
        }
        mModeListenerlList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int modelId = mModelList.size() + 1;
                Model newModel = new Model(modelId, "Item "+ modelId);
                try {
                    onNewModelAdded(newModel);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
