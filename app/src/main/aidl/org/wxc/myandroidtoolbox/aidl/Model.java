package org.wxc.myandroidtoolbox.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wxc on 2016/10/3.
 */
public class Model implements Parcelable {
    private static final String TAG = "Model";

    public int modelId;
    public String modelTag;

    public Model(int id, String tag) {
        modelId = id;
        modelTag = tag;
    }

    protected Model(Parcel in) {
        modelId = in.readInt();
        modelTag = in.readString();
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

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(modelId);
        parcel.writeString(modelTag);
    }
}
