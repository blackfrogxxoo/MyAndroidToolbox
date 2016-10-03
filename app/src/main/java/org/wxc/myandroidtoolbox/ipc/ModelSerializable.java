package org.wxc.myandroidtoolbox.ipc;

import java.io.Serializable;

/**
 * 实现Serializable接口的类模板
 *
 * Created by wxc on 2016/10/3.
 */
public class ModelSerializable implements Serializable {
    private static final String TAG = "ModelSerializable";

    private static final long serialVersionUID = 8711368828010083044L;

    public int modelId;
    public String modelTag;

    public ModelSerializable(int id, String tag) {
        modelId = id;
        modelTag = tag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG + '@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append(" # ");
        sb.append("modelId:" + modelId);
        sb.append(", modelTag:" + modelTag);
        return sb.toString();
    }
}
