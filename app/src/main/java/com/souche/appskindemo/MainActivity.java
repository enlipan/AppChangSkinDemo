package com.souche.appskindemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.souche.appskindemo.manager.SkinResourceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setInflaterFactory();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void setInflaterFactory() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // android 版本兼容
        /**
         * 如何找到页面中，需要换肤的控件，找到对应该控件以及该控件需要更换资源的指定属性
         *
         * Activity 中需要替换的元素List<View>以及属性集合的封装
         * 找到单独的一串List<View>无法简单操控
         *              封装View以及属性 -List<SkinView>
         *                                  - SkinView(View,List<ViewAttr>)
         *                                      - SkinAttr(resName,SkinAttrType) - 不同type有不同操作，Drawable、Color...
         */
        LayoutInflaterCompat.setFactory(inflater, new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                Log.d("TAG","Factory " + name);
                for (int i = 0; i < attrs.getAttributeCount(); i++) {
                    Log.d("TAG",attrs.getAttributeName(i) + "  " + attrs.getAttributeValue(i));
                }
                if (TextUtils.equals(name,"TextView")){
                    return new EditText(context,attrs);
                }
                return null;
            }
        });

    }

    /**
     * 皮肤插件apk文件路径
     */
    private String mSkinPluginPath;

    /**
     * 皮肤插件apk的包名
     */
    private String mSkinPluginPackageName;

    private Resources mAppResource;
    private void initData() {
        mSkinPluginPath = "";
        mSkinPluginPackageName = "com";
        mAppResource = getResources();
    }

    private void initView() {
        TextView tv1 = (TextView) findViewById(R.id.tv_1);
        TextView tv2 = (TextView) findViewById(R.id.tv_2);
        TextView tv3 = (TextView) findViewById(R.id.tv_3);
        TextView tv4 = (TextView) findViewById(R.id.tv_4);
        TextView tv5 = (TextView) findViewById(R.id.tv_5);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSkinToNight();
            }
        });
    }




    /**
     * 夜间模式
     */
    private void changeSkinToNight() {
        loadPluginFromPath(mSkinPluginPath,mSkinPluginPackageName);
    }

     /**
     * 皮肤
     *
     * 资源的获取 - 从 apk 插件包中获取
     *  - apk 路径获取
     *  -
     */
    private void loadPluginFromPath(String apkPath,String pkgName) {
        final AssetManager assetManager;
        try {
            assetManager =  AssetManager.class.newInstance();
            if (assetManager != null){
                Method addPathMethod = assetManager.getClass().getMethod("addAssetPath",String.class);
                addPathMethod.invoke(assetManager,mSkinPluginPath);

                //获取对应apk的Resource
                Resources apkSkinResource = new Resources(assetManager,mAppResource.getDisplayMetrics(),mAppResource.getConfiguration());
                SkinResourceManager resourceManager = SkinResourceManager.getInstance(apkSkinResource,mSkinPluginPackageName);

            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }


    /**
     * 缩放View
     * @param contentView
     */
    private void scaleView(View contentView){}


}
