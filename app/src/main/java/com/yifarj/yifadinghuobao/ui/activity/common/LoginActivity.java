package com.yifarj.yifadinghuobao.ui.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.MettingCodeEntity;
import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PhoneFormatCheckUtils;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.LoadingDialog;

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
    @BindView(R.id.tvCode)
    TextView tvCode;

    private final int MAX_COUNT_TIME = 60;
    private String mettingCode;
    private Disposable mDisposable;
    private Observable<Long> mObservableCountTime;
    private Consumer<Long> mConsumerCountTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        String name = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME);
        etName.setText(name);
        etName.setSelection(0, name.length());
        clicks(btnLogin)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (mDisposable != null && !mDisposable.isDisposed()) {
                            //停止倒计时
                            mDisposable.dispose();
                        }
                        login();
                    }
                });
        clicks(ivConfigure)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> startActivity(new Intent(LoginActivity.this, ConnectServerActivity.class)));


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
                        if (domain == null || port == null || ip == null || accountId.equals("0")) {
                            onConfigureClicked();
                        } else {
                            if (PhoneFormatCheckUtils.isPhoneLegal(phoneNumber)) {
                                RetrofitHelper.getMettingCodeApi().getMettingCode(ip, port, accountId, phoneNumber, AppInfoUtil.getDeviceId(LoginActivity.this), AppInfoUtil.getIPAddress())
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
                                                    ToastUtils.showShortSafe("验证码发送失败");
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
     * 登录
     */
    private void login() {
        final String name = etName.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();

        if (!CommonUtil.isNetworkAvailable(LoginActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShortSafe("请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortSafe("请输入手机号");
            return;
        }

        String domain = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
        AppInfoUtil.resetBaseUrl(domain, false);

        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this, getString(R.string.log_in));
        RetrofitHelper.getMettingLoginApi()
                .mettingLogin(AppInfoUtil.getToken(), name, pwd)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MettingLoginEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loadingDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull MettingLoginEntity mettingLoginEntity) {
                        loadingDialog.dismiss();
                        if (!mettingLoginEntity.HasError) {
                            DataSaver.setMettingCustomerInfo(mettingLoginEntity.Value);
                            PreferencesUtil.putString(ApiConstants.CPreference.USER_NAME, name);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            ToastUtils.showShortSafe(getString(R.string.login_failure) + mettingLoginEntity.Information);
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


    private void onConfigureClicked() {
        ToastUtils.showShortSafe("请先获取服务器信息");
        Intent intent = new Intent(this, ConnectServerActivity.class);
        startActivity(intent);
    }
}
