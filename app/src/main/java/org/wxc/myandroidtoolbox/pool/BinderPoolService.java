package org.wxc.myandroidtoolbox.pool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Process;
import android.os.IBinder;
import android.util.Log;

public class BinderPoolService extends Service {
    private static final String TAG = "BinderPoolService";

    private Binder mBinderPool = new BinderPool.BinderPoolImpl();
    private int pid = Process.myPid();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "当前进程ID为："+pid+"----"+"客户端与服务端连接成功，服务端返回BinderPool.BinderPoolImpl 对象");
        return mBinderPool;
    }
}
