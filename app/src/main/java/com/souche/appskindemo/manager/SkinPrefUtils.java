package com.souche.appskindemo.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by paul on 2017/2/20.
 *
 * 保存应用换肤信息
 */

public class SkinPrefUtils {

    private static SkinPrefUtils INSTANCE;

    private final String  fPrefName = "Change_Skin_Pref";
    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private SkinPrefUtils(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(fPrefName,Context.MODE_PRIVATE);
    }

    public static SkinPrefUtils getInStance(Context context){
        INSTANCE = new SkinPrefUtils(context);
        return INSTANCE;
    }

    /**
     *  保存Apk插件包Path，插件包包名pkgName
     */
    ////////////////////////////////////////////////////
    private final String fApkPath = "SkinPrefUtils_Apk_Path";
    private final String fPkgName = "SkinPrefUtils_Pkg_Name";
    private final String fAppSuffix = "SkinPrefUtils_App_Suffix";

    public void saveApkPath(String path){
        mSharedPreferences.edit().putString(fApkPath,path).apply();
    }

    public void saveApkPackageName(String pkgName){
        mSharedPreferences.edit().putString(fPkgName,pkgName).apply();
    }

    public String getApkPath(){
        return mSharedPreferences.getString(fApkPath,"");
    }

    public String getPkgName(){
        return mSharedPreferences.getString(fPkgName,"");
    }

    public String getAppResSuffix(){
        return mSharedPreferences.getString(fAppSuffix,"");
    }

    public void saveAppResSuffix(String suffix){
        mSharedPreferences.edit().putString(fAppSuffix,suffix).apply();
    }

    public void clearInfo(){
        mSharedPreferences.edit().clear().apply();
    }



}
