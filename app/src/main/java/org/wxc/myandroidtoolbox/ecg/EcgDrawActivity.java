package org.wxc.myandroidtoolbox.ecg;

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
import org.wxc.myandroidtoolbox.ecg.cache.EcgScanView;
import org.wxc.myandroidtoolbox.ecg.draw.EcgDraw;
import org.wxc.myandroidtoolbox.ecg.draw.IEcgDrawManager;
import org.wxc.myandroidtoolbox.ecg.draw.IOnEcgDrawListener;

public class EcgDrawActivity extends AppCompatActivity {
    private static final String TAG = "EcgDrawActivity";
    private static final int MSG_NEW_MODEL_ADDED = 1;

    private android.os.Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_MODEL_ADDED:
                    Log.d(TAG, "Receive new model:" + msg.obj);
                    mEcgScanView.setDataArrayAndDraw(((EcgDraw)msg.obj).points);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private IOnEcgDrawListener onEcgReceiveListener = new IOnEcgDrawListener.Stub() {
        @Override
        public void onEcgDraw(EcgDraw ecg) throws RemoteException {
            mHandler.obtainMessage(MSG_NEW_MODEL_ADDED, ecg).sendToTarget();
        }
    };

    private IEcgDrawManager mModelManager;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mModelManager = IEcgDrawManager.Stub.asInterface(iBinder);
            try {
                mModelManager.registerListener(onEcgReceiveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private EcgScanView mEcgScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        mEcgScanView = (EcgScanView) findViewById(R.id.ecgScanView);
        Intent intent = new Intent(this, EcgDrawManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mModelManager != null && mModelManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener:" + onEcgReceiveListener);
            try {
                mModelManager.unregisterListener(onEcgReceiveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
