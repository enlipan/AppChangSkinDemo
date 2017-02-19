package com.souche.appskindemo.skin;

import android.view.View;

import java.util.List;

/**
 * Created by paul on 2017/2/19.
 */

public class SkinView {

    private View mView;
    private List<SkinAttr> mSkinAttrList;

    public View getView() {
        return mView;
    }

    public List<SkinAttr> getSkinAttrList() {
        return mSkinAttrList;
    }

    public SkinView(View view, List<SkinAttr> skinAttrs){
        mView = view;
        mSkinAttrList = skinAttrs;
    }


    public void applySkin(){
        for (SkinAttr skinAttr : mSkinAttrList) {
            skinAttr.apply(mView);
        }
    }

}
