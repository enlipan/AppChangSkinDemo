package com.souche.appskindemo.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import com.souche.appskindemo.skin.SkinView;

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

    public void initial(Context context){
        mContext = context.getApplicationContext();
        mAppResource = context.getResources();
    }

    public void loadResourceManger(String apkPath,String pkgName){
        mResourceManager = loadPluginFromPath(apkPath,pkgName);
    }

    private SkinResourceManager loadPluginFromPath(String apkPath, String pkgName) {
        final AssetManager assetManager;
        try {
            assetManager =  AssetManager.class.newInstance();
            if (assetManager != null){
                Method addPathMethod = assetManager.getClass().getMethod("addAssetPath",String.class);
                addPathMethod.invoke(assetManager,apkPath);

                //获取对应apk的Resource
                Resources apkSkinResource = new Resources(assetManager,mAppResource.getDisplayMetrics(),mAppResource.getConfiguration());
                SkinResourceManager resourceManager = SkinResourceManager.getInstance(apkSkinResource,pkgName);
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

    public void changeActivityViewSkin(String apkPath, String pkgName, ISkinViewChangingCallBack callBack) {
        loadResourceManger(apkPath,pkgName);

        doChangeSkinWork(callBack);
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
                callBack.onComplete();
            }
        }.execute();
    }


    public interface ISkinViewChangedListener {
        void onSkinChanged();
    }
}
