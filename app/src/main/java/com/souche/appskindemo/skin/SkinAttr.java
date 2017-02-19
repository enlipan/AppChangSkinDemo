package com.souche.appskindemo.skin;

import android.view.View;

/**
 * Created by paul on 2017/2/19.
 */

public class SkinAttr {

    private String mResName;
    private SkinAttrType mSkinType;


    public String getResName() {
        return mResName;
    }

    public SkinAttrType getSkinType() {
        return mSkinType;
    }

    public SkinAttr(String resName, SkinAttrType skinType) {
        mResName = resName;
        mSkinType = skinType;
    }

    public void apply(View view) {
        mSkinType.apply(view,mResName);

    }
}
