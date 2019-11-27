package com.zhaofan.hotfix;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;
public class HotfixApplication extends Application {
    File apk;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        apk = new File(getCacheDir()+"/hotfix-debug.apk");
        if (apk.exists()){
            try {
                ClassLoader classLoader = getClassLoader();
                Class loaderClass = BaseDexClassLoader.class;
                Field pathListField = loaderClass.getDeclaredField("pathList");
                pathListField.setAccessible(true);
                Object pathListObject = pathListField.get(classLoader);//getClassLoader().pathList
                Class pathListClass = pathListObject.getClass();
                Field dexElementsField = pathListClass.getDeclaredField("dexElements");
                dexElementsField.setAccessible(true);
                Object dexElementsObject = dexElementsField.get(pathListClass);//getClassLoader().pathList.dexElements

                PathClassLoader newClassLoader = new PathClassLoader(apk.getPath(), null);
                Object newPathListObject = pathListField.get(newClassLoader);
                Object newDexElementsObject = dexElementsField.get(newPathListObject);
                int oldLenth = Array.getLength(dexElementsObject);
                int newLenth = Array.getLength(newDexElementsObject);
                Object concatDexElementsObject = Array.newInstance(dexElementsObject.getClass().getComponentType()
                        , oldLenth + newLenth);
                for (int i = 0; i < newLenth; i++) {
                    Array.set(concatDexElementsObject, i, Array.get(newDexElementsObject, i));
                }
                for (int i = 0; i < oldLenth; i++) {
                    Array.set(concatDexElementsObject, i + newLenth, Array.get(dexElementsObject, i));
                }
                dexElementsField.set(pathListObject, concatDexElementsObject);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }
}
