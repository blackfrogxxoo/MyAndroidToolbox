package org.wxc.myandroidtoolbox.pool;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import org.wxc.myandroidtoolbox.aidl.IModelManager;
import org.wxc.myandroidtoolbox.aidl.IOnNewModelListener;
import org.wxc.myandroidtoolbox.aidl.Model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by black on 2016/10/10.
 */

public class ModelManager extends IModelManager.Stub {

    private CopyOnWriteArrayList<Model> mModelList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewModelListener> mModeListenerlList = new RemoteCallbackList<>();

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
}
