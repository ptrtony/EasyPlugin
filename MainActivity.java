package com.zhaofan.hotfix;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            Class utilsClazz = Class.forName("com.zhaofan.hotfix_plugin.utils.Utils");
//            Constructor constructorClass = utilsClazz.getConstructor();
//            constructorClass.setAccessible(true);
//            Object utils = constructorClass.newInstance();
//            Method method = utilsClazz.getDeclaredMethod("shot");
//            method.setAccessible(true);
//            method.invoke(utils);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        File file = new File(getCacheDir()+"/plugin_app-debug.apk");
        try (Source source = Okio.source(getAssets().open("apk/plugin_apk-debug.apk"));
             BufferedSink sink = Okio.buffer(Okio.sink(file))){
            sink.writeAll(source);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DexClassLoader dexClassLoader = new DexClassLoader(file.getPath(),getCacheDir().getPath(),null,null);
        Class debugClazz = null;
        try {
            debugClazz = dexClassLoader.loadClass("com.zhaofan.plugin_apk.utils.Utils");
            Constructor constructor = debugClazz.getDeclaredConstructors()[0];
            Object utils = constructor.newInstance();
            Method method = debugClazz.getDeclaredMethod("shout");
            method.invoke(utils);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
