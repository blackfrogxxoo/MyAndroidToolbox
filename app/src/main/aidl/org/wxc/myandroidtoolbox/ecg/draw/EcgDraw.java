package org.wxc.myandroidtoolbox.ecg.draw;

import android.os.Parcel;
import android.os.Parcelable;

import org.wxc.myandroidtoolbox.ecg.cache.EcgScanView;

/**
 * Created by wxc on 2016/10/3.
 */
public class EcgDraw implements Parcelable {
    public int[] points = new int[EcgScanView.ONCE_DRAW_COUNT + 1];

    public EcgDraw(int[] points) {
        this.points = points;
    }

    protected EcgDraw(Parcel in) {
        in.readIntArray(points);
    }

    public static final Creator<EcgDraw> CREATOR = new Creator<EcgDraw>() {
        @Override
        public EcgDraw createFromParcel(Parcel in) {
            return new EcgDraw(in);
        }

        @Override
        public EcgDraw[] newArray(int size) {
            return new EcgDraw[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(points);
    }
}
