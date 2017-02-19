package com.souche.appskindemo.manager;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by paul on 2017/2/19.
 */

public class SkinResourceManager {

    final Resources mSkinResource;
    final String mApkPkgName;

    private SkinResourceManager(Resources skinResource, String apkPkgName) {
        mSkinResource = skinResource;
        mApkPkgName = apkPkgName;
    }

    public static SkinResourceManager getInstance(Resources apkSkinResource,String apkPkgName) {
        return new SkinResourceManager(apkSkinResource, apkPkgName);
    }

    public Drawable getDrawableByName(String name){
        try {
            return mSkinResource.getDrawable(mSkinResource.getIdentifier(name,"drawable",mApkPkgName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ColorStateList getColorByName(String name){
        try {
            return mSkinResource.getColorStateList(mSkinResource.getIdentifier(name,"color",mApkPkgName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
