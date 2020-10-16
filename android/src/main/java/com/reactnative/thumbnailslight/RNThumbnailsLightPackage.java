
package com.reactnative.thumbnailslight;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RNThumbnailsLightPackage implements ReactPackage {
//     @Override
//     public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
//        return Collections.emptyList();
//     }

//     @Override
//     public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
//         return Arrays.<ViewManager>asList(new RNThumbnailsLightManager());
//     }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new RNThumbnailsLightManager(reactContext));

        return modules;
    }
}
