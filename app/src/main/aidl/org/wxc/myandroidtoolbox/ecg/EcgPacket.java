package org.wxc.myandroidtoolbox.ecg;


import android.os.Parcel;
import android.os.Parcelable;

import org.wxc.myandroidtoolbox.utils.TypeUtil;

/**
 * Created by black on 2016/6/24.
 */
public class EcgPacket{
    private static EcgPacket sEcgPacket;
    /**
     * 4 bits，数据包类型
     */
    protected byte mPacketType;

    /**
     * 4 bits,包的总长度，有效值为1-10（*2),
     */
    protected byte mTotalLength;


    /**
     * 原始数据
     */
    protected byte[] mBytes = new byte[20];

    public EcgPacket(byte[] ori) {
        init(ori);
    }

    private void init(byte[] ori) {
        this.mBytes = ori;
        this.mTotalLength = (byte) ori.length;
        this.mPacketType = TypeUtil.subByte(ori[0], 4, 8);
    }

    @Override
    public String toString() {
        return super.toString() +",sequenceId:"+ getSequenceID();
    }

    public short[] getECGmeasurements() {
        int length = mTotalLength/2 - 1;
        short[] measurements = new short[length];
        for(int i=0;i<length;i++) {
            measurements[i] = TypeUtil.LEBytes2Short(new byte[]{mBytes[i*2 + 1], mBytes[i*2 +2]});
        }
//        Log.i("test", Arrays.toString(measurements));
        return measurements;
    }

    public int getSequenceID() {
        return mBytes[mTotalLength-1];
    }

    public static EcgPacket fromBytes(byte[] ori) {
        if(sEcgPacket == null) {
            sEcgPacket = new EcgPacket(ori);
        } else {
            sEcgPacket.init(ori);
        }

        return sEcgPacket;

    }
}
