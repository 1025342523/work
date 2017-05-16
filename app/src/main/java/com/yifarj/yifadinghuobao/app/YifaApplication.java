package com.yifarj.yifadinghuobao.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;

/**
 * YifaApplication
 *
 * @auther Czech.Yuan
 * @date 2017/5/11 15:14
 */
public class YifaApplication extends Application {
    public static YifaApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        init();
    }

    public static YifaApplication getInstance() {
        return application;
    }

    private void init() {
        //初始化Utils
        Utils.init(this);
        //初始化Stetho调试工具
        Stetho.initializeWithDefaults(this);
        //初始化Leak内存泄露检测工具
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        //初始化SharedPreferences
        PreferencesUtil.init(this);
        //初始化DBFlow
        FlowManager.init(this);
    }
}
