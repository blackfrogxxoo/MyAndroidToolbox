package org.wxc.myandroidtoolbox.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.wxc.myandroidtoolbox.R;
import org.wxc.myandroidtoolbox.aidl.IModelManager;
import org.wxc.myandroidtoolbox.aidl.IOnNewModelListener;
import org.wxc.myandroidtoolbox.aidl.Model;

import java.util.List;

public class ModelManagerActivity extends AppCompatActivity {
    private static final String TAG = "ModelManagerActivity";
    private static final int MSG_NEW_MODEL_ADDED = 1;

    private android.os.Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_MODEL_ADDED:
                    Log.d(TAG, "Receive new model:" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private IOnNewModelListener mOnNewModelListener = new IOnNewModelListener.Stub() {
        @Override
        public void onNewModelAdded(Model newModel) throws RemoteException {
            mHandler.obtainMessage(MSG_NEW_MODEL_ADDED, newModel).sendToTarget();
        }

    };

    private IModelManager mModelManager;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mModelManager = IModelManager.Stub.asInterface(iBinder);
            try {
                List<Model> list = mModelManager.getModelList();
                Log.i(TAG, "query model list, list type:" + list.getClass().getCanonicalName());
                Log.i(TAG, "query model list:" + list.toString());

                Model model = new Model(3, "Item 3");
                mModelManager.addModel(model);
                List<Model> newList = mModelManager.getModelList();
                Log.i(TAG, "query model list:" + newList.toString());

                mModelManager.registerListener(mOnNewModelListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_manager);

        Intent intent = new Intent(this, ModelManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mModelManager != null && mModelManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener:" + mOnNewModelListener);
            try {
                mModelManager.unregisterListener(mOnNewModelListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
