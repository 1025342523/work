package com.yifarj.yifadinghuobao.ui.activity.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yifarj.yifadinghuobao.R;

import butterknife.ButterKnife;

/**
 * Activity基类
 *
 * @auther Czech.Yuan
 * @date 2017/5/6 17:07
 */
public abstract class BaseActivity extends RxAppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_blue));
        }
        //设置布局
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        ButterKnife.bind(this);
        //初始化控件
        initViews(savedInstanceState);

    }

    public abstract int getLayoutId();

    public abstract void initViews(Bundle savedInstanceState);


    public void loadData() {
    }


    public void showProgressBar() {
    }


    public void hideProgressBar() {
    }


    public void initRecyclerView() {
    }


    public void initRefreshLayout() {
    }


    public void finishTask() {
    }

    public void setRightIcon(int visibility, int title){}
}
