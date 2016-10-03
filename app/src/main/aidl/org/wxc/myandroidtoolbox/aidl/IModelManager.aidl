// IModelManager.aidl
package org.wxc.myandroidtoolbox.aidl;

// Declare any non-default types here with import statements
import org.wxc.myandroidtoolbox.aidl.Model;
import org.wxc.myandroidtoolbox.aidl.IOnNewModelListener;

interface IModelManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void addModel(in Model model);
    List<Model> getModelList();
    void registerListener(IOnNewModelListener listener);
    void unregisterListener(IOnNewModelListener listener);
}
