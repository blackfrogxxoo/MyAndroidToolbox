package org.wxc.myandroidtoolbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by black on 2016/12/6.
 */

public class SerializeUtil {

    public static boolean store(Serializable obj, String path) {
        try {
            File file = new File(path);
            File dir = file.getParentFile();
            if(!dir.exists()) {
                dir.mkdirs();
            }
            if(!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream(path));
            os.writeObject(obj);
            os.close();
//            Log.i(TAG, "storeSerializable: success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static <T extends Serializable> T read(String path) {
        File file = new File(path);
        T obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            obj = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
