package com.souche.appskindemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.souche.appskindemo.manager.DefaultViewCreate;
import com.souche.appskindemo.manager.SkinManager;
import com.souche.appskindemo.skin.SkinAttr;
import com.souche.appskindemo.skin.SkinAttrSupport;
import com.souche.appskindemo.skin.SkinView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 2017/2/19.
 */

public class BaseSkinActivity extends AppCompatActivity implements SkinManager.ISkinViewChangedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setViewFactory();
        super.onCreate(savedInstanceState);
    }

    private Method mCreateViewMethod;
    private final Class [] mClassSignatureArgs = new Class[]{View.class,String.class,Context.class,AttributeSet.class};
    private final Object[] mCreateViewInvokeArgs = new Object[4];
    private void setViewFactory() {
        SkinManager.getInstance().registerSkinChangeListener(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LayoutInflaterCompat.setFactory(inflater, new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                /**
                 * 系统默认的setFactory函数，自定义构造的Factory需要完成其默认工作
                 *
                 * AppCompatActivity 完成的View构造工作
                 */
                AppCompatDelegate delegate = getDelegate();
                /**
                 *
                 * From {@link android.support.v4.view.LayoutInflaterFactory}
                 *
                *@Override
                public final View onCreateView(View parent, String name,
                        Context context, AttributeSet attrs) {
                    // First let the Activity's Factory try and inflate the view
                    final View view = callActivityOnCreateView(parent, name, context, attrs);
                    if (view != null) {
                        return view;
                    }

                    // If the Factory didn't handle it, let our createView() method try
                    return createView(parent, name, context, attrs);
                }
                 */
                View view = null;
                try {
                    if (mCreateViewMethod == null){
                        mCreateViewMethod = delegate.getClass().getMethod("createView", mClassSignatureArgs);
                    }
                    mCreateViewInvokeArgs[0] = parent;
                    mCreateViewInvokeArgs[1] = name;
                    mCreateViewInvokeArgs[2] = context;
                    mCreateViewInvokeArgs[3] = attrs;
                    view = (View) mCreateViewMethod.invoke(delegate, mCreateViewInvokeArgs);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (view == null){
                    view = DefaultViewCreate.getInstance().createViewFromTag(context, name,attrs);
                }
                if (view != null){
                    constructSkinViews(view,attrs,context);
                }
                return view;
            }
        });

    }

    private void constructSkinViews(View view, AttributeSet attrsParam, Context context) {
        List<SkinView> skinViewList = SkinManager.getInstance().getSkinViewMaps().get(this);

        if (skinViewList == null){
            skinViewList = new ArrayList<>();
            SkinManager.getInstance().addToSkinViewMap(this,skinViewList);
        }
        List<SkinAttr> sKinAttrs = SkinAttrSupport.getSKinAttrs(attrsParam,context);
        skinViewList.add(new SkinView(view,sKinAttrs));

        checkAutoSkinState();
    }

    /**
     * 检测换肤状态，是否已经点击过换肤--当前页面进入需要自动换肤
     */
    private void checkAutoSkinState() {

    }


    @Override
    protected void onDestroy() {
        SkinManager.getInstance().unRegisterSkinChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSkinChanged() {

    }
}
