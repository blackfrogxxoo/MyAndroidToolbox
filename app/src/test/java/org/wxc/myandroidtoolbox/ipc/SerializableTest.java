package org.wxc.myandroidtoolbox.ipc;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by wxc on 2016/10/3.
 */
public class SerializableTest {

    /**
     * 1 将ModelSerializable写入 --> D:\BitsMelody\MyAndroidToolbox\app\serializable_model_cache.txt
     * 2 读入ModelSerializable
     */
    @Test
    public void testSerializableModel() {
        ModelSerializable model = new ModelSerializable(1, "Test Tag");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("serializable_model_cache.txt"));
            out.writeObject(model);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModelSerializable newModel = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("serializable_model_cache.txt"));
            newModel = (ModelSerializable) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertNotNull(newModel);
        assertEquals(newModel.modelId, model.modelId);
        assertEquals(newModel.modelTag, model.modelTag);
        System.out.println(newModel);
    }
}
