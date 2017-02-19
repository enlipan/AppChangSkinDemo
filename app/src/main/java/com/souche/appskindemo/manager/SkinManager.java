package com.souche.appskindemo.manager;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.souche.appskindemo.skin.SkinView;

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

    private static SkinManager INSTANCE = new SkinManager();

    private SkinManager(){}

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    private void inital(Context context){
        mContext = context.getApplicationContext();
    }

    private void loadResourceManger(){

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


    public interface ISkinViewChangedListener {
        void onSkinChanged();
    }



}
