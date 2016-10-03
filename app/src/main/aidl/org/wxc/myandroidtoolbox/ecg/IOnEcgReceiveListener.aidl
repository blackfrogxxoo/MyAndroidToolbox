// IOnEcgReceiveListener.aidl
package org.wxc.myandroidtoolbox.ecg;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.ecg.EcgPacket;

interface IOnEcgReceiveListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onEcgReceived(in EcgPacket ecg);
}
