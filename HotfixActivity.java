package com.zhaofan.hotfix;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class HotfixActivity extends AppCompatActivity implements View.OnClickListener {
    Button showTitleBt, hotfixBt,removeHotfixBt,killSelfBt;
    TextView mShowTitleTv;
    File apk ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotfix);
        apk = new File(getCacheDir()+"/hotfix-debug.apk");
        initView();
        showTitleBt.setOnClickListener(this);
        hotfixBt.setOnClickListener(this);
    }

    private void initView() {
        showTitleBt = findViewById(R.id.showTitleBt);
        hotfixBt = findViewById(R.id.hotfixBt);
        mShowTitleTv = findViewById(R.id.mShowTitleTv);
        removeHotfixBt = findViewById(R.id.removeHotfixBt);
        killSelfBt = findViewById(R.id.removeHotfixBt);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showTitleBt:
                Title title = new Title();
                mShowTitleTv.setText(title.getTitle());
                break;
            case R.id.hotfixBt:
//                System.out.println("get ClassLoader name:"+getClassLoader().getClass().getName());
                try (Source source = Okio.source(getAssets().open("apk/app-debug.apk"));
                     BufferedSink sink = Okio.buffer(Okio.sink(apk))){
                    sink.writeAll(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                    PathClassLoader newClassLoader = new PathClassLoader(apk.getPath(),null);
                    Object newPathListObject = pathListField.get(newClassLoader);
                    Object newDexElementsObject = dexElementsField.get(newPathListObject);
                    int oldLenth = Array.getLength(dexElementsObject);
                    int newLenth = Array.getLength(newDexElementsObject);
                    Object concatDexElementsObject = Array.newInstance(dexElementsObject.getClass().getComponentType(),oldLenth+newLenth);
                    for (int i=0;i<newLenth;i++){
                        Array.set(concatDexElementsObject,i,Array.get(newDexElementsObject,i));
                    }
                    for (int i=0;i<oldLenth;i++){
                        Array.set(concatDexElementsObject,i+newLenth,Array.get(dexElementsObject,i));
                    }
                    dexElementsField.set(pathListObject,concatDexElementsObject);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.removeHotfixBt:
                if (apk.exists()){
                    apk.delete();
                }
                break;

            case R.id.killSelfBt:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }
}
