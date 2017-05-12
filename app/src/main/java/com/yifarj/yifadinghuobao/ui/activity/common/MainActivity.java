package com.yifarj.yifadinghuobao.ui.activity.common;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.fragment.goods.TabGoodsFragment;
import com.yifarj.yifadinghuobao.ui.fragment.main.TabMainFragment;
import com.yifarj.yifadinghuobao.ui.fragment.order.TabOrderFragment;
import com.yifarj.yifadinghuobao.ui.fragment.shopping.TabShoppingFragment;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tabHost)
    FragmentTabHost mFragmentTabHost;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        final FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        assert tabHost != null;
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.contentContainer);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f1").setIndicator(getIndicatorView(0)), TabMainFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f2").setIndicator(getIndicatorView(1)), TabGoodsFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f3").setIndicator(getIndicatorView(2)), TabOrderFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("f4").setIndicator(getIndicatorView(3)), TabShoppingFragment.class, null);
        TabWidget mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
        assert mTabWidget != null;
        mTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        tabHost.setCurrentTab(0);
    }

    private static final int[] TAB_BUTTON_ICON_RES = {
            R.drawable.selector_bottom_bar_main,
            R.drawable.selector_bottom_bar_goods,
            R.drawable.selector_bottom_bar_order,
            R.drawable.selector_bottom_bar_shopping,
    };
    private static final int[] TAB_BUTTON_NAME_RES = {
            R.string.tab_main,
            R.string.tab_goods,
            R.string.tab_order,
            R.string.tab_shopping,
    };

    private View getIndicatorView(int position) {
        View view = View.inflate(this, R.layout.tab_indicator, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon_tab);
        TextView tvName = (TextView) view.findViewById(R.id.txt_indicator);
        icon.setImageResource(TAB_BUTTON_ICON_RES[position]);
        tvName.setText(TAB_BUTTON_NAME_RES[position]);
        return view;
    }
}
