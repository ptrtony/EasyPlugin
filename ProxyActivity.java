package com.zhaofan.hotfix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 解决插件化调用资源文件失败的情况
 */
public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
//
    }

    @Override
    public AssetManager getAssets() {
        Class assetManagerClass = AssetManager.class;
        AssetManager assetManager = null;
        try {
            assetManager = (AssetManager) assetManagerClass.newInstance();
            Method assetMethod = assetManagerClass.getMethod("adAssetPath",String.class);
            assetMethod.invoke(assetManager,"apkPath");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        return new Resources(getAssets(),getResources().getDisplayMetrics(),getResources().getConfiguration());
    }
}
