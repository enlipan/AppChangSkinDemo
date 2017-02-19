package com.souche.appskindemo.skin;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 2017/2/19.
 */

public class SkinAttrSupport {

    /**
     *  AttributeSet to SkinAttr
     */
    public static List<SkinAttr> getSKinAttrs(AttributeSet attributeSet, Context context){

        List<SkinAttr> skinAttrList = new ArrayList<>();
        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {
            String  attrName = attributeSet.getAttributeName(i); // textColor ....
            String  attrValue = attributeSet.getAttributeValue(i); // @color/resId
            /**
             * Check value 值类型 - 是否是资源引用类型 value =  @resourceId
             */
            if (attrValue.startsWith("@")){
                int resId = -1;
                try {
                    /**
                     * 某些系统Style属性值无法转换  Style/Appcompat....
                     */
                    resId = Integer.parseInt(attrValue.substring(1)); //去@ 后对于ResourceId进行值获取
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
                if (resId == -1) continue;

                final String resName = context.getResources().getResourceEntryName(resId);
                if (attrName.startsWith(SkinConst.SKIN_PREFIX)){ // 对应需要更换资源控件
                    SkinAttrType attrType = getSupportType(attrName); // check是否支持更换

                    if (attrType == null) continue;

                    SkinAttr skinAttr = new SkinAttr(resName,attrType);
                    skinAttrList.add(skinAttr);
                }
            }
        }
        return  skinAttrList;
    }

    private static SkinAttrType getSupportType(String attrName) {
        for (SkinAttrType attrType : SkinAttrType.values()) {
            if (TextUtils.equals(attrName,attrType.getResType())){
                return attrType;
            }
        }
        return null;
    }

}
