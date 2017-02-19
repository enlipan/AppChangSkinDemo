package com.souche.appskindemo.skin;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.souche.appskindemo.manager.SkinManager;
import com.souche.appskindemo.manager.SkinResourceManager;

/**
 * Created by paul on 2017/2/19.
 */

public enum  SkinAttrType {

    BACKGROUND(""){
        @Override
        public void apply(View view, String resName) {
            Drawable drawable = getResourceManager().getDrawableByName(resName);
            view.setBackgroundDrawable(drawable);
        }
    },
    SRC("") {
        @Override
        public void apply(View view, String resName) {
            if (view instanceof ImageView){
                ((ImageView) view).setImageDrawable(getResourceManager().getDrawableByName(resName));
            }
        }
    },
    TEXT_COLOR("") {
        @Override
        public void apply(View view, String resName) {
            if (view instanceof TextView){
                ((TextView) view).setTextColor(getResourceManager().getColorByName(resName));
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

    public SkinResourceManager getResourceManager(){
        return SkinManager.getInstance().getResourceManager();
    }
}
