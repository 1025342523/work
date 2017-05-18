package com.yifarj.yifadinghuobao.ui.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.LoginEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.LoadingDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.btnLogin)
    Button btnLogin;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        String name = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME);
        etName.setText(name);
        etName.setSelection(0, name.length());
        RxView.clicks(btnLogin)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> login());
        RxView.clicks(ivConfigure)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> startActivity(new Intent(LoginActivity.this, LoginConfigActivity.class)));
    }


    /**
     * 登录
     */
    private void login() {
        if (!CommonUtil.isNetworkAvailable(LoginActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        String accountId = String.valueOf(PreferencesUtil.getInt(ApiConstants.CPreference.ACCOUNT_ID));
        String domain = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
        String port = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT);
        String ip = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP);
        if (domain == null || port == null || ip == null || accountId.equals("0")) {
            onConfigureClicked();
        } else {
            AppInfoUtil.resetBaseUrl(domain, false);
            final String name = etName.getText().toString().trim();
            final String pwd = etPwd.getText().toString();

            LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this, getString(R.string.log_in));
            RetrofitHelper.getLoginApi()
                    .login(name, pwd, ip, port, accountId, AppInfoUtil.getDeviceId(LoginActivity.this), AppInfoUtil.getIPAddress(), "0")
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
                                AppInfoUtil.restoreToken(loginEntity.Value);
                                PreferencesUtil.putString(ApiConstants.CPreference.USER_NAME, name);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                ToastUtils.showShortSafe(getString(R.string.login_failure) + loginEntity.Information);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onComplete() {
                            loadingDialog.dismiss();
                        }
                    });
        }
    }

    private void onConfigureClicked() {
        Intent intent = new Intent(this, LoginConfigActivity.class);
        startActivity(intent);
    }
}
