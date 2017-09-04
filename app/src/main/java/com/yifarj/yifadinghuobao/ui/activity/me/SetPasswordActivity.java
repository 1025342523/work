package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.jakewharton.rxbinding2.view.RxView.clicks;

/**
 * Created by zydx-pc on 2017/8/22.
 */

public class SetPasswordActivity extends BaseActivity {
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.etNewPassword)
    EditText etNewPassword;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.tvNewPwdError)
    TextView tvNewPwdError;
    @BindView(R.id.ivShowPwd)
    ImageView ivShowPwd;

    private boolean isShowPwd = false, isOldPwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_password;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        if (!CommonUtil.isNetworkAvailable(SetPasswordActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            etNewPassword.setText("******");
            etNewPassword.setEnabled(false);
            ivShowPwd.setEnabled(false);
            btnOk.setVisibility(View.GONE);
        }
        isOldPwd = getIntent().getBooleanExtra("isOldPwd", false);
        String oldPwd = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PASSWORD);
        if (isOldPwd || (oldPwd != null)) {
            etNewPassword.setText("******");
            etNewPassword.setEnabled(false);
            ivShowPwd.setEnabled(false);
            btnOk.setVisibility(View.GONE);
        }

        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputMethod();
                finish();
            }
        });

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                tvNewPwdError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivShowPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowPwd || etNewPassword.getText().toString().isEmpty()) {
                    isShowPwd = true;
                    ivShowPwd.setImageResource(R.drawable.ic_show_pwd);
                    etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etNewPassword.setSelection(etNewPassword.getText().toString().length());
                } else {
                    isShowPwd = false;
                    ivShowPwd.setImageResource(R.drawable.ic_hide_pwd);
                    etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etNewPassword.setSelection(etNewPassword.getText().toString().length());
                }
            }
        });

        clicks(btnOk)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        setPassword();
                    }
                });
    }

    public void hideInputMethod() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 设置登录密码
     */
    private void setPassword() {
        String pwd = etNewPassword.getText().toString();
        if (!CommonUtil.isNetworkAvailable(SetPasswordActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (pwd.isEmpty()) {
            tvNewPwdError.setVisibility(View.VISIBLE);
            tvNewPwdError.setText("密码不能为空");
            return;
        }
        String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
        for (int i = 0; i < DataSaver.getTraderInfo().TraderContactList.size(); i++) {
            if (userName.equals(DataSaver.getTraderInfo().TraderContactList.get(i).Mobile)) {
                TraderEntity.ValueEntity.TraderContactListEntity traderContactEntity1 = DataSaver.getTraderInfo().TraderContactList.get(i);
                traderContactEntity1.card_password = pwd;
                RetrofitHelper.saveTraderApi()
                        .saveTrader("Trader", ZipUtil.gzip(JsonUtils.serialize(DataSaver.getTraderInfo())), "", AppInfoUtil.getToken())
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TraderEntity>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull TraderEntity traderEntity) {
                                if (!traderEntity.HasError) {
                                    PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PASSWORD, pwd);
                                    ToastUtils.showShortSafe("密码设置成功");
                                    LogUtils.e("密码设置成功:" + pwd);
                                    DataSaver.setTraderInfo(traderEntity.Value);
                                    hideInputMethod();
                                    finish();
                                } else {
                                    LogUtils.e("密码设置失败");
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                ToastUtils.showShortSafe("密码设置失败");
                                LogUtils.e("密码设置失败");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        }

    }

}
