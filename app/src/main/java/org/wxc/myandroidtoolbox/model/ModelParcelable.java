package org.wxc.myandroidtoolbox.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 实现Parcelable接口的model模板
 *
 * Created by wxc on 2016/10/3.
 */
public class ModelParcelable implements Parcelable {
    private static final String TAG = "ModelParcelable";

    public int modelId;
    public String modelTag;
    public boolean modelBool;

    public Book modelBook;

    public ModelParcelable(int id, String tag, boolean bool, Book book) {
        modelId = id;
        modelTag = tag;
        modelBool = bool;
        modelBook = book;
    }

    protected ModelParcelable(Parcel in) {
        modelId = in.readInt();
        modelTag = in.readString();
        modelBool = in.readInt() == 1;
        modelBook = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<ModelParcelable> CREATOR = new Creator<ModelParcelable>() {
        @Override
        public ModelParcelable createFromParcel(Parcel in) {
            return new ModelParcelable(in);
        }

        @Override
        public ModelParcelable[] newArray(int size) {
            return new ModelParcelable[size];
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
        parcel.writeInt(modelBool ? 1 : 0);
        parcel.writeParcelable(modelBook, 0);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG + '@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append(" # ");
        sb.append("modelId:" + modelId);
        sb.append(", modelTag:" + modelTag);
        sb.append(", modelBool:" + modelBool);
        sb.append(", modelBook:" + modelBook);
        return sb.toString();
    }

    public static class Book implements Parcelable {

        public Book(){}

        protected Book(Parcel in) {
        }

        public static final Creator<Book> CREATOR = new Creator<Book>() {
            @Override
            public Book createFromParcel(Parcel in) {
                return new Book(in);
            }

            @Override
            public Book[] newArray(int size) {
                return new Book[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }
}
