package com.example.share_resource;

/**
 * Created by wxc on 2016/11/5.
 */

public abstract class IntGenerator {
    private volatile boolean canceled = false;
    public abstract int next();
    // Allow this to be canceled:
    public void cancel() {
        canceled = true;
    }
    public boolean isCanceled () {
        return canceled;
    }
}
