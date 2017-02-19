package com.souche.appskindemo;

import android.app.Application;

import com.souche.appskindemo.manager.SkinManager;

/**
 * Created by paul on 2017/2/20.
 */

public class SkinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().initial(this);
    }
}
