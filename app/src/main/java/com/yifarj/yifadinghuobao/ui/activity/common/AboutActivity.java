package com.yifarj.yifadinghuobao.ui.activity.common;

import android.os.Bundle;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import butterknife.BindView;


/**
 * 关于页面
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @BindView(R.id.titleView)
    TitleView titleView;


    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titleView.setLeftIconClickListener(view -> finish());
        tvVersion.setText("V" + AppInfoUtil.getVersionName(this) + " Build " + AppInfoUtil.getVersionCode(this));
    }


}
