package org.wxc.myandroidtoolbox.ecg;

/**
 * Created by black on 2016/6/24.
 */
public class PacketFactory {
    public static final byte[] BYTE_ECG2 = {
            0x4a,
            -56, 0,
            -12, 1,
            -24, 3,
            -96, 15,
            -72, 11,
            -48, 7,
            -24, 3,
            0, 0,
            113, -2,
            0x1f};
    /**
     * type:心电
     */
    public static final byte TYPE_ECG = 0x04;

    /**
     * 可能的Packet：<br>
     *  握手：key2<br>
     *  采集命令响应：即心电数据，初定为一直发<br>
     *  设备信息命令响应：即状态数据包，电量一直发，其他一次<br>
     *  通知/报警：
     *
     * @param ori
     * @return
     */
    public static EcgPacket fromByteArray(byte[] ori) {
//        if(ori == null || ori.length == 0) {
//            return null;
//        }
//        byte totalLength = (byte) (TypeUtil.subByte(ori[0], 0, 4) * 2);
//        final byte packetType = TypeUtil.subByte(ori[0], 4, 8);
//        if(ori.length < 3 || ori.length > 20 || (ori.length != 20 && totalLength != ori.length)) {
//            return null;
//        }
//        if(packetType>0x05 || packetType<0x01) {
//            return null;
//        }
//
//        switch (packetType){
//            case TYPE_ECG:
//                return EcgPacket.fromBytes(ori);
//            default:
//                return null;
//        }
        return EcgPacket.fromBytes(ori);
    }
}
