package com.yifarj.yifadinghuobao.ui.activity.common;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.LoginEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.fragment.goods.TabGoodsFragment;
import com.yifarj.yifadinghuobao.ui.fragment.main.TabMainFragment;
import com.yifarj.yifadinghuobao.ui.fragment.mine.TabMineFragment;
import com.yifarj.yifadinghuobao.ui.fragment.order.TabOrderFragment;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.LoadingDialog;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yifarj.yifadinghuobao.R.id.tabHost;

public class MainActivity extends BaseActivity {

    @BindView(tabHost)
    FragmentTabHost mFragmentTabHost;
    private long exitTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        assert mFragmentTabHost != null;
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.contentContainer);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f1").setIndicator(getIndicatorView(0)), TabMainFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f2").setIndicator(getIndicatorView(1)), TabGoodsFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f3").setIndicator(getIndicatorView(2)), TabOrderFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f4").setIndicator(getIndicatorView(3)), TabMineFragment.class, null);
        TabWidget mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
        assert mTabWidget != null;
        mTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mFragmentTabHost.setCurrentTab(1);
    }

    private static final int[] TAB_BUTTON_ICON_RES = {
            R.drawable.selector_bottom_bar_main,
            R.drawable.selector_bottom_bar_goods,
            R.drawable.selector_bottom_bar_order,
            R.drawable.selector_bottom_bar_mine,
    };
    private static final int[] TAB_BUTTON_NAME_RES = {
            R.string.tab_main,
            R.string.tab_goods,
            R.string.tab_order,
            R.string.tab_mine,
    };

    private View getIndicatorView(int position) {
        View view = View.inflate(this, R.layout.tab_indicator, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon_tab);
        TextView tvName = (TextView) view.findViewById(R.id.txt_indicator);
        icon.setImageResource(TAB_BUTTON_ICON_RES[position]);
        tvName.setText(TAB_BUTTON_NAME_RES[position]);
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出App
     */
    private void exitApp() {

        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showShortSafe("再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void logout() {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this, getString(R.string.logout));
        RetrofitHelper.getLogoutApi()
                .logout("", "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loadingDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull LoginEntity loginEntity) {
                        loadingDialog.dismiss();
                        if (!loginEntity.HasError) {
                            ToastUtils.showShortSafe("注销成功");
                        } else {
                            ToastUtils.showShortSafe("注销失败" + loginEntity.Information);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("网络请求失败，请检查网络是否畅通");
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        finish();
                    }
                });
    }

}
