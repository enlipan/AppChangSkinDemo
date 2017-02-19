package com.souche.appskindemo.manager;

/**
 * Created by paul on 2017/2/20.
 */
public interface ISkinViewChangingCallBack {
    /**
     * View 换肤开始
     */
    void onStart();

    /**
     * View 换肤结束
     */
    void onComplete();

    /**
     * 换肤异常
     */
    void onError(Exception e);
}
