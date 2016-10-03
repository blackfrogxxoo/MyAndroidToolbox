// IEcgPacketManager.aidl
package org.wxc.myandroidtoolbox.ecg.draw;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.ecg.draw.EcgDraw;
import org.wxc.myandroidtoolbox.ecg.draw.IOnEcgDrawListener;

interface IEcgDrawManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void receiveEcgDraw(in EcgDraw ecg);
    void registerListener(IOnEcgDrawListener listener);
    void unregisterListener(IOnEcgDrawListener listener);
}
