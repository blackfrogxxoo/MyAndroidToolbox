// IBinderPool.aidl
package org.wxc.myandroidtoolbox.aidl;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
