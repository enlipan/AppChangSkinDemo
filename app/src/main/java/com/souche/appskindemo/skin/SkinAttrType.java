package com.souche.appskindemo.skin;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.souche.appskindemo.manager.SkinManager;
import com.souche.appskindemo.manager.SkinResourceManager;

/**
 * Created by paul on 2017/2/19.
 *
 *
 * 利用apk 插件包中的同名资源文件 替换应用中的资源文件
 * apk插件包的资源文件需要利用对应patch路径加载生成的 ResourceManager加载插件包资源文件
 */

public enum  SkinAttrType {

    BACKGROUND("background"){
        @Override
        public void apply(View view, String resName) {
            Drawable drawable = getResourceManager().getDrawableByName(constructorResName(resName));
            view.setBackgroundDrawable(drawable);
        }
    },
    SRC("src") {
        @Override
        public void apply(View view, String resName) {
            if (view instanceof ImageView){
                ((ImageView) view).setImageDrawable(getResourceManager().getDrawableByName(constructorResName(resName)));
            }
        }
    },
    TEXT_COLOR("textColor") {
        @Override
        public void apply(View view, String resName) {
            if (view instanceof TextView){
                ((TextView) view).setTextColor(getResourceManager().getColorByName(constructorResName(resName)));
            }
        }
    };


    private String mResType;
    SkinAttrType(String name) {
        mResType = name;
    }

    public String getResType() {
        return mResType;
    }

    public abstract void apply(View view, String resName);

    /**
     * 支持应用内换肤
     * @param resName
     * @return
     */
    String constructorResName(String resName){
        if (TextUtils.isEmpty(getSuffixStr())){
            return  resName;
        }else {
            return  resName + getSuffixStr();
        }
    }

    String getSuffixStr(){
        return SkinManager.getInstance().getAppSkinResourceSuffix();
    }

    SkinResourceManager getResourceManager(){
        return SkinManager.getInstance().getResourceManager();
    }
}
