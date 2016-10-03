// IEcgPacketManager.aidl
package org.wxc.myandroidtoolbox.ecg;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.ecg.EcgPacket;
import org.wxc.myandroidtoolbox.ecg.IOnEcgReceiveListener;

interface IEcgPacketManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void receiveEcg(in EcgPacket ecg);
    void registerListener(IOnEcgReceiveListener listener);
    void unregisterListener(IOnEcgReceiveListener listener);
}
