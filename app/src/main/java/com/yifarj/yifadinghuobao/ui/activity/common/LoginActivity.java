package com.yifarj.yifadinghuobao.ui.activity.common;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.llInputArea)
    LinearLayout llInputArea;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.ivExperience)
    ImageView ivExperience;
    @BindView(R.id.ivConfigure)
    ImageView ivConfigure;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btnLogin)
    void onBtnLoginClick() {
        //TODO implement
    }

    @OnLongClick(R.id.btnLogin)
    boolean onBtnLoginLongClick() {
        //TODO implement
        return true;
    }
}
