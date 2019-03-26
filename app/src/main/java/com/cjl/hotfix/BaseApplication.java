package com.cjl.hotfix;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cjl.hotfixlib.FixDexUtil;

/**
 * @author chenjieliang
 */
public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //加载热修复的dex文件
        FixDexUtil.loadFixedDex(this);
    }
}
