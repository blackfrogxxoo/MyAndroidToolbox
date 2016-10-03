// IOnEcgReceiveListener.aidl
package org.wxc.myandroidtoolbox.ecg.draw;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.ecg.draw.EcgDraw;

interface IOnEcgDrawListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onEcgDraw(in EcgDraw ecg);
}
