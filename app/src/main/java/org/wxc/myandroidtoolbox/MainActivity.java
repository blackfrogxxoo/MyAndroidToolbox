package org.wxc.myandroidtoolbox;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.wxc.myandroidtoolbox.aidl.IModelManager;
import org.wxc.myandroidtoolbox.aidl.IOnNewModelListener;
import org.wxc.myandroidtoolbox.aidl.Model;
import org.wxc.myandroidtoolbox.ecg.cache.EcgScanView;
import org.wxc.myandroidtoolbox.ecg.draw.EcgDraw;
import org.wxc.myandroidtoolbox.ecg.draw.IEcgDrawManager;
import org.wxc.myandroidtoolbox.ecg.draw.IOnEcgDrawListener;
import org.wxc.myandroidtoolbox.model.ModelParcelable;
import org.wxc.myandroidtoolbox.pool.BinderPool;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IModelManager modelManager;
    private IEcgDrawManager ecgManager;

    private EcgScanView ecgScanView;
    private int i;

    IOnNewModelListener.Stub onNewModelListener = new IOnNewModelListener.Stub() {
        @Override
        public void onNewModelAdded(Model newModel) throws RemoteException {
            Log.i(TAG, "onNewModelAdded: newModel = " + newModel.toString());
        }
    };
    private IOnEcgDrawListener.Stub onEcgDrawListener = new IOnEcgDrawListener.Stub() {
        @Override
        public void onEcgDraw(EcgDraw ecg) throws RemoteException {
//            Log.i(TAG, "onEcgDraw: ecg = " + ecg.toString());
            Observable.just(ecg.points)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<int[]>() {
                @Override
                public void call(int[] ints) {
                    ecgScanView.setDataArrayAndDraw(ints);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ecgScanView = (EcgScanView) findViewById(R.id.ecgScanView);
//        testModelParcelable();
        new Thread(){
            @Override
            public void run() {
                BinderPool binderPool = BinderPool.getInstance(getApplicationContext());
                IBinder modelBinder = binderPool.queryBinder(BinderPool.BINDER_MODEL);

                modelManager = IModelManager.Stub.asInterface(modelBinder);
                IBinder ecgBinder = binderPool.queryBinder(BinderPool.BINDER_ECG_DRAW);

                ecgManager = IEcgDrawManager.Stub.asInterface(ecgBinder);
                try {
                    modelManager.registerListener(onNewModelListener);

                    ecgManager.registerListener(onEcgDrawListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void testBinderPool(View view) {
        try {
            ecgManager.setRunning(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void testBinderPool() {
       try {
            modelManager.addModel(new Model(i++, "Test " + i));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void testModelParcelable() {
        ModelParcelable model = new ModelParcelable(1, "Test Tag", true, new ModelParcelable.Book());
        Bundle bundle = new Bundle();
        bundle.putParcelable("model", model);

        ModelParcelable newModel = bundle.getParcelable("model");
        Log.i(TAG, "testModelParcelable: " + newModel);
    }

    @Override
    protected void onDestroy() {
        if (modelManager != null && modelManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener:" + onNewModelListener);
            try {
                modelManager.unregisterListener(onNewModelListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (ecgManager != null && ecgManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener:" + onEcgDrawListener);
            try {
                ecgManager.unregisterListener(onEcgDrawListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        try {
            ecgManager.setRunning(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
