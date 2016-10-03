// IOnNewModelListener.aidl
package org.wxc.myandroidtoolbox.aidl;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.aidl.Model;

interface IOnNewModelListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewModelAdded(in Model newModel);
}
