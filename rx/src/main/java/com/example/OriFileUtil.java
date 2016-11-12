package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wxc on 2016/11/12.
 */

public class OriFileUtil {
    private String mPath;
    private BufferedWriter mBufferedWriter;
    private static OriFileUtil sInstance;

    private OriFileUtil(){}

    public static OriFileUtil getInstance(String path) throws IOException {
        if (sInstance == null) {
            synchronized (OriFileUtil.class) {
                if (sInstance == null) {
                    sInstance = new OriFileUtil();
                }
            }
        }
        sInstance.setPath(path);
        return sInstance;
    }

    public void store(String content) throws IOException {
        mBufferedWriter.write(content);
    }

    public synchronized static void close() throws IOException {
        if(sInstance != null) {
            sInstance.mPath = null;
            if (sInstance.mBufferedWriter != null) {
                sInstance.mBufferedWriter.close();
                sInstance.mBufferedWriter = null;
            }
            sInstance = null;
        }
    }

    private void setPath(String path) throws IOException {
        if(path.equals(mPath)) {
            if(mBufferedWriter == null) {
                mBufferedWriter = new BufferedWriter(new FileWriter(mPath, true));
            }
        } else {
            mPath = path;
            if(mBufferedWriter != null) {
                mBufferedWriter.close();
            }
            File file = new File(mPath);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            mBufferedWriter = new BufferedWriter(new FileWriter(file, true));
        }
    }
}
