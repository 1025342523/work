package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.AppDatabase;
import com.yifarj.yifadinghuobao.model.entity.MettingCodeEntity;
import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.common.ConnectServerActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PhoneFormatCheckUtils;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.jakewharton.rxbinding2.view.RxView.clicks;

/**
 * Created by zydx-pc on 2017/8/30.
 */

public class ForgetPasswordActivity extends BaseActivity {

    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.etNewPassword)
    EditText etNewPassword;
    @BindView(R.id.ivShowPwd)
    ImageView ivShowPwd;
    @BindView(R.id.btnOk)
    Button btnOk;

    private final int MAX_COUNT_TIME = 60;
    private String mettingCode;
    private Disposable mDisposable;
    private Observable<Long> mObservableCountTime;
    private Consumer<Long> mConsumerCountTime;

    private boolean isShowPwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_forget_password;
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

        mConsumerCountTime = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                //显示剩余时长。当倒计时为 0 时，还原 btn 按钮.
                if (aLong == 0) {
                    RxView.enabled(tvCode).accept(true);
                    RxTextView.text(tvCode).accept("发送验证码");
                } else {
                    RxTextView.text(tvCode).accept("剩余 " + aLong + " 秒");
                }
            }
        };

        mObservableCountTime = RxView.clicks(tvCode)
                .throttleFirst(MAX_COUNT_TIME, TimeUnit.SECONDS)
                .flatMap(new Function<Object, ObservableSource<Long>>() {

                    @Override
                    public ObservableSource<Long> apply(@NonNull Object o) throws Exception {
                        String phoneNumber = etName.getText().toString().trim();
                        String accountId = PreferencesUtil.getString(ApiConstants.CPreference.ACCOUNT_ID);
                        String port = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT);
                        String ip = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP);
                        String domain = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
                        if (domain == null || port == null || ip == null || ("0").equals(accountId)) {
                            onConfigureClicked();
                        } else {
                            if (PhoneFormatCheckUtils.isPhoneLegal(phoneNumber)) {
                                RetrofitHelper.getMettingCodeApi().getMettingCode(ip, port, accountId, phoneNumber, AppInfoUtil.getDeviceId(ForgetPasswordActivity.this), AppInfoUtil.getIPAddress())
                                        .compose(bindToLifecycle())
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<MettingCodeEntity>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(@NonNull MettingCodeEntity mettingCodeEntityEntity) {

                                                if (!mettingCodeEntityEntity.HasError && mettingCodeEntityEntity.Tag != null) {
                                                    ToastUtils.showShortSafe("验证码已发送");
                                                    mettingCode = mettingCodeEntityEntity.Tag.substring(7, mettingCodeEntityEntity.Tag.length());
                                                    LogUtils.e("mettingCode：" + mettingCode);

                                                    AppInfoUtil.restoreToken(mettingCodeEntityEntity.Value);

                                                    try {
                                                        RxView.enabled(tvCode).accept(false);
                                                        RxTextView.text(tvCode).accept("剩余" + MAX_COUNT_TIME + "秒");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    ToastUtils.showShortSafe(mettingCodeEntityEntity.Information);
                                                    if (mDisposable != null && !mDisposable.isDisposed()) {
                                                        //停止倒计时
                                                        mDisposable.dispose();
                                                        //重新订阅
                                                        mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                        try {
                                                            RxTextView.text(tvCode).accept("发送验证码");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                ToastUtils.showShortSafe("验证码发送失败");
                                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                                    //停止倒计时
                                                    mDisposable.dispose();
                                                    //重新订阅
                                                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                    try {
                                                        RxTextView.text(tvCode).accept("发送验证码");
                                                    } catch (Exception e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            } else {
                                ToastUtils.showShortSafe("请输入正确的手机号");
                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                    //停止倒计时
                                    mDisposable.dispose();
                                    //重新订阅
                                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                }
                            }
                        }


                        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()).take(MAX_COUNT_TIME);
                    }
                })
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return MAX_COUNT_TIME - (aLong + 1);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());


        //重置验证码按钮。
        RxView.clicks(tvCode).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (mDisposable != null && !mDisposable.isDisposed()) {
                    //停止倒计时
                    mDisposable.dispose();
                    //重新订阅
                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                    //按钮可点击
                    RxView.enabled(tvCode).accept(true);
                    RxTextView.text(tvCode).accept("发送验证码");
                }
            }
        });

        mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);


    }

    /**
     * 重置登录密码
     */
    private void setPassword() {
        final String name = etName.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();
        final String newPwd = etNewPassword.getText().toString().trim();

        if (!CommonUtil.isNetworkAvailable(ForgetPasswordActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortSafe("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShortSafe("请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtils.showShortSafe("请输入密码");
            return;
        }

        String domain = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
        AppInfoUtil.resetBaseUrl(domain, false);

        RetrofitHelper.getMettingLoginApi()
                .mettingLogin(AppInfoUtil.getToken(), name, pwd)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MettingLoginEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull MettingLoginEntity mettingLoginEntity) {
                        if (!mettingLoginEntity.HasError) {
                            LogUtils.e("mettingLoginEntity success!");
                            DataSaver.setMettingCustomerInfo(mettingLoginEntity.Value);
                            PreferencesUtil.putInt("TraderId", mettingLoginEntity.Value.TraderId);
                            PreferencesUtil.putInt("Id", mettingLoginEntity.Value.Id);
                            PreferencesUtil.putString("ContactName", mettingLoginEntity.Value.ContactName);
                            String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
                            if (!StringUtils.isEmpty(userName)) {
                                if (!userName.equals(name)) {
                                    FlowManager.getDatabase(AppDatabase.class).reset(ForgetPasswordActivity.this);
                                }
                            }
                            PreferencesUtil.putString(ApiConstants.CPreference.USER_NAME, name);

                            LogUtils.e("登录 onComplete");
                            LogUtils.e("traderId " + mettingLoginEntity.Value.TraderId);
                            RetrofitHelper.getTraderApi()
                                    .fetchTrader("Trader", "Id =" + mettingLoginEntity.Value.TraderId, "", AppInfoUtil.getToken())
                                    .compose(bindToLifecycle())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<TraderEntity>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                            LogUtils.e("获取Trader onSubscribe");
                                        }

                                        @Override
                                        public void onNext(@NonNull TraderEntity traderEntity) {
                                            if (!traderEntity.HasError && traderEntity.Value != null) {
                                                LogUtils.e("获取Trader onNext");
                                                DataSaver.setPriceSystemId(traderEntity.Value.DefaultPriceSystemId);
                                                DataSaver.setTraderInfo(traderEntity.Value);

                                                setNewPassword(newPwd);
                                            } else {
                                                ToastUtils.showShortSafe("密码设置失败" + traderEntity.Information.toString());
                                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                                    //停止倒计时
                                                    mDisposable.dispose();
                                                    //重新订阅
                                                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                    try {
                                                        RxTextView.text(tvCode).accept("发送验证码");
                                                        //按钮可点击
                                                        RxView.enabled(tvCode).accept(true);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            LogUtils.e("获取Trader onError" + e.getMessage());
                                            ToastUtils.showShortSafe("密码设置失败" + e.getMessage());
                                            if (mDisposable != null && !mDisposable.isDisposed()) {
                                                //停止倒计时
                                                mDisposable.dispose();
                                                //重新订阅
                                                mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                try {
                                                    RxTextView.text(tvCode).accept("发送验证码");
                                                    //按钮可点击
                                                    RxView.enabled(tvCode).accept(true);
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });

                        } else {
                            ToastUtils.showShortSafe("获取验证码失败，" + mettingLoginEntity.Information);
                            if (mDisposable != null && !mDisposable.isDisposed()) {
                                //停止倒计时
                                mDisposable.dispose();
                                //重新订阅
                                mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                try {
                                    RxTextView.text(tvCode).accept("发送验证码");
                                    //按钮可点击
                                    RxView.enabled(tvCode).accept(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("设置 onError" + e.getMessage());
                        ToastUtils.showShortSafe("密码设置失败");
                        if (mDisposable != null && !mDisposable.isDisposed()) {
                            //停止倒计时
                            mDisposable.dispose();
                            //重新订阅
                            mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                            try {
                                RxTextView.text(tvCode).accept("发送验证码");
                                //按钮可点击
                                RxView.enabled(tvCode).accept(true);
                            } catch (Exception x) {
                                x.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 设置登录密码
     */
    private void setNewPassword(String newPassword) {
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

    private void onConfigureClicked() {
        ToastUtils.showShortSafe("请先获取服务器信息");
        Intent intent = new Intent(this, ConnectServerActivity.class);
        startActivity(intent);
    }

    public void hideInputMethod() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
