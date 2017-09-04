package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.TraderContactEntity;
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

public class ModifyPasswordActivity extends BaseActivity {
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.etOldPassword)
    EditText etOldPassword;
    @BindView(R.id.etNewPassword)
    EditText etNewPassword;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.tvOldPwdError)
    TextView tvOldPwdError;
    @BindView(R.id.tvNewPwdError)
    TextView tvNewPwdError;

    private String password;
    private TraderContactEntity.ValueBean traderContact;
    private int traderId = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputMethod();
                finish();
            }
        });

        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                tvOldPwdError.setVisibility(View.GONE);
                LogUtils.e("tvOldPwdError onTextChanged：" + s.toString() + "，start：" + start + "，count：" + count + "，after：" + after);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else if (DataSaver.getPasswordCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            traderId = PreferencesUtil.getInt("TraderId", 0);
        }
        getOldPassword(traderId);

        clicks(btnOk)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        setNewPassword();
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
    private void setNewPassword() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();

        if (!CommonUtil.isNetworkAvailable(ModifyPasswordActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (oldPassword.isEmpty()) {
            tvOldPwdError.setVisibility(View.VISIBLE);
            tvOldPwdError.setText(R.string.password_empty);
            return;
        }
        if (newPassword.isEmpty()) {
            tvNewPwdError.setVisibility(View.VISIBLE);
            tvNewPwdError.setText(R.string.password_empty);
            return;
        }
        if (!oldPassword.equals(password)) {
            tvOldPwdError.setVisibility(View.VISIBLE);
            tvOldPwdError.setText(R.string.old_password_error);
            return;
        }
        String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
        for (int i = 0; i < DataSaver.getTraderInfo().TraderContactList.size(); i++) {
            if (userName.equals(DataSaver.getTraderInfo().TraderContactList.get(i).Mobile)) {
                TraderEntity.ValueEntity.TraderContactListEntity traderContactEntity1 = DataSaver.getTraderInfo().TraderContactList.get(i);
                traderContactEntity1.card_password = newPassword;
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
                                    ToastUtils.showShortSafe("密码设置成功");
                                    LogUtils.e("密码设置成功:" + newPassword);
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

    /**
     * 获取登录密码
     */
    private void getOldPassword(int traderId) {
        RetrofitHelper.getTraderApi()
                .fetchTrader("Trader", "Id =" + traderId, "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TraderEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TraderEntity traderEntity) {
                        if (!traderEntity.HasError && traderEntity.Value != null) {
                            String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
                            for (TraderEntity.ValueEntity.TraderContactListEntity traderContactEntity : traderEntity.Value.TraderContactList) {
                                if (userName.equals(traderContactEntity.Mobile)) {
                                    password = traderContactEntity.card_password;
                                    LogUtils.e("旧密码获取成功" + password);
                                }
                            }
                        } else {
                            LogUtils.e("密码获取失败");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取登录密码
     *//*
    private void getOldPassword(int traderId) {
        RetrofitHelper.fetchTraderContactApi()
                .fetchTraderContact("TraderContact", "Id =" + traderId, "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TraderContactEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TraderContactEntity traderContactEntity) {
                        if (!traderContactEntity.HasError && traderContactEntity.Value != null) {
                            password = traderContactEntity.Value.card_password;
                            LogUtils.e("旧密码获取成功" + password);
                        } else {
                            LogUtils.e("旧密码获取失败");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("旧密码获取失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    *//**
     * 设置新密码
     *//*
    private void setNewPassword(String pwd) {
        traderContact = new TraderContactEntity.ValueBean();
        traderContact.card_password = pwd;
        RetrofitHelper.SaveTraderContactApi()
                .SaveTraderContact("TraderContact", "", ZipUtil.gzip(JsonUtils.serialize(traderContact)), AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TraderContactEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TraderContactEntity traderContactEntity) {
                        if (!traderContactEntity.HasError && traderContactEntity.Value != null && traderContactEntity.Value.card_password != null) {
                            ToastUtils.showShortSafe("密码修改成功");
                            finish();
                        } else {
                            ToastUtils.showShortSafe("密码修改失败");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("密码修改失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/

}
