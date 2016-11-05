package org.wxc.myandroidtoolbox.ecg;

import org.junit.Test;
import org.wxc.myandroidtoolbox.utils.TypeUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by wxc on 2016/10/3.
 */
public class TestEcgPacket {
    @Test
    public void testEcg(){

        EcgPacket packet = PacketFactory.fromByteArray(PacketFactory.BYTE_ECG2);
        assertNotNull(packet);
        System.out.println(packet.toString());

        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 200)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 500)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 1000)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 4000)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 3000)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 2000)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 1000)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 0)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) -399)));
        System.out.println(Arrays.toString(TypeUtil.short2LEBytes((short) 0)));

    }



}
