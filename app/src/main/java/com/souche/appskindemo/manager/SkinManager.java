package com.souche.appskindemo.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.souche.appskindemo.skin.SkinView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by paul on 2017/2/20.
 */

public class SkinManager {

    /**
     * ResourceManager 管理
     */
    private SkinResourceManager mResourceManager;
    public SkinResourceManager getResourceManager() {
        return mResourceManager;
    }

    private Context mContext;
    private Resources mAppResource;

    private static SkinManager INSTANCE = new SkinManager();

    private SkinManager(){}

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    /**
     * Application 初始化
     * @param context
     */
    public void initial(Context context){
        mContext = context.getApplicationContext();
        mAppResource = context.getResources();

        initFromSaveSkinInfo(context);
    }

    private void initFromSaveSkinInfo(Context context) {
        SkinPrefUtils skinPref = SkinPrefUtils.getInStance(context);
        String path = skinPref.getApkPath();
        String pkgName = skinPref.getPkgName();

        String suffix = skinPref.getAppResSuffix();
        if (!TextUtils.isEmpty(suffix)){ // 应用内换肤 信息
            loadAppResourceManager(suffix);
        } else {
            try {
                File file = new File(path);
                if (!file.exists()){
                    skinPref.clearInfo();
                }else {
                    loadResourceManger(path,pkgName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前ResourceManager加载时，对应的path信息
     */
    private String mCurrentPluginPath;
    private String mCurrentPluginPkgName;

    /**
     * 应用内换肤-资源对应-所添加后缀
     */
    private String mAppSkinResourceSuffix;
    private void setCurrentSuffix(String suffix){
        mAppSkinResourceSuffix = suffix;
    }
    public String getAppSkinResourceSuffix() {
        return mAppSkinResourceSuffix;
    }

    private void loadResourceManger(String apkPath, String pkgName){
        /**
         * 传入的plugin信息，已经加载，则不需要再次加载
         */
        if (TextUtils.equals(apkPath,mCurrentPluginPath) && TextUtils.equals(pkgName,mCurrentPluginPkgName)) return;

        mResourceManager = initManagerByLoadPlugin(apkPath,pkgName);
    }

    /**
     * 应用内换肤- 更换 ResourceManager为应用内 Manager
     * @param suffix
     */
    private void loadAppResourceManager(String suffix){
        mResourceManager =  SkinResourceManager.getInstance(mAppResource,mContext.getPackageName());
        setCurrentSuffix(suffix);
    }

    private void setCurrentPluginInfo(String path,String pkgName){
        mCurrentPluginPath = path;
        mCurrentPluginPkgName = pkgName;
    }

    private SkinResourceManager initManagerByLoadPlugin(String apkPath, String pkgName) {
        final AssetManager assetManager;
        try {
            assetManager =  AssetManager.class.newInstance();
            if (assetManager != null){
                Method addPathMethod = assetManager.getClass().getMethod("addAssetPath",String.class);
                addPathMethod.invoke(assetManager,apkPath);

                //获取对应apk的Resource
                Resources apkSkinResource = new Resources(assetManager,mAppResource.getDisplayMetrics(),mAppResource.getConfiguration());
                SkinResourceManager resourceManager = SkinResourceManager.getInstance(apkSkinResource,pkgName);

                setCurrentPluginInfo(apkPath,pkgName);

                return resourceManager;
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
        return null;
    }

    /**
     *  应用中每个Activity均可能存在需要换肤的SkinView集合 - 构造键值对
     */
    private final Map<ISkinViewChangedListener,List<SkinView>> mSkinViewMaps = new ArrayMap<>();

    public void addToSkinViewMap(ISkinViewChangedListener viewChangeCallBack, List<SkinView> skinViews){
        mSkinViewMaps.put(viewChangeCallBack,skinViews);
    }

    public Map<ISkinViewChangedListener, List<SkinView>> getSkinViewMaps() {
        return mSkinViewMaps;
    }


    private final List<ISkinViewChangedListener>  mISkinViewChangesListener = new ArrayList<>();

    public void registerSkinChangeListener(ISkinViewChangedListener callBack){
        mISkinViewChangesListener.add(callBack);
    }

    public void unRegisterSkinChangeListener(ISkinViewChangedListener callBack){
        mSkinViewMaps.remove(callBack);
        mISkinViewChangesListener.remove(callBack);
    }

    /**
     * 根究指定的 apkPath以及PkgName换肤
     * @param apkPath
     * @param pkgName
     * @param callBack
     */
    public void changeActivityViewSkin(String apkPath, String pkgName, ISkinViewChangingCallBack callBack) {
        clearCurrentInfo();

        loadResourceManger(apkPath,pkgName);
        doChangeSkinWork(callBack);
    }

    /**
     * 已经换肤过-保存path路径-自动根据保存路径加载皮肤
     * @param callBack
     */
    public void changeActivityViewSkinFromSavedPath(ISkinViewChangingCallBack callBack){
        clearCurrentInfo();

        if (mResourceManager != null){
            doChangeSkinWork(callBack);
        }
    }

    /**
     * 应用内换肤
     *
     * @param appResourceSuffix 应用内换肤对应的特定文件名后缀
     */
    public void changeActivityViewSkinInApp(String appResourceSuffix,ISkinViewChangingCallBack callBack){
        clearCurrentInfo();

        loadAppResourceManager(appResourceSuffix);
        doChangeSkinWork(callBack);
    }

    private void clearCurrentInfo(){
        mAppSkinResourceSuffix = null;

        mCurrentPluginPath  = null;
        mCurrentPluginPkgName = null;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 实际的换肤逻辑
     * @param callBack
     */
    private void doChangeSkinWork(final ISkinViewChangingCallBack callBack) {
        // doSkinChange -- BackGround
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                callBack.onStart();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    for (ISkinViewChangedListener iSkinViewChangedListener : mISkinViewChangesListener) {
                        doSkinResourceChange(iSkinViewChangedListener);
                        iSkinViewChangedListener.onSkinChanged();
                    }
                }catch (final Exception e){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(e);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updateSavePrefInfo();
                callBack.onComplete();
            }
        }.execute();
    }

    private void updateSavePrefInfo() {
        SkinPrefUtils skinPrefUtils = SkinPrefUtils.getInStance(mContext);
        skinPrefUtils.clearInfo();
        if (TextUtils.isEmpty(mAppSkinResourceSuffix)){
            updateSavedPluginInfoInPref(skinPrefUtils,mAppSkinResourceSuffix);
        }else {
            updateSavedPluginInfoInPref(skinPrefUtils,mCurrentPluginPath, mCurrentPluginPkgName);
        }
    }


    private void updateSavedPluginInfoInPref(SkinPrefUtils skinPrefUtils, String appSkinResourceSuffix) {
        skinPrefUtils.saveAppResSuffix(appSkinResourceSuffix);
    }

    /**
     * 遍历所有存货SKinView - 更换皮肤操作
     * @param iSkinViewChangedListener
     */
    private void doSkinResourceChange(ISkinViewChangedListener iSkinViewChangedListener) {
        List<SkinView> skinViews = mSkinViewMaps.get(iSkinViewChangedListener);
        for (SkinView skinView : skinViews) {
            skinView.applySkin();
        }
    }

    /**
     * 将当前加载的皮肤信息更新保存到Pref中
     * @param skinPrefUtils
     * @param path
     * @param pkgName
     */
    private void updateSavedPluginInfoInPref(SkinPrefUtils skinPrefUtils, String path, String pkgName){
        skinPrefUtils.saveApkPath(path);
        skinPrefUtils.saveApkPackageName(pkgName);
    }


    public boolean isNeedChangeSkin(){
        return !TextUtils.isEmpty(mCurrentPluginPath);
    }

    public interface ISkinViewChangedListener {
        void onSkinChanged();
    }
}
