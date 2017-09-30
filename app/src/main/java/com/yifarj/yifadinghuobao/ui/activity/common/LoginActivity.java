package com.yifarj.yifadinghuobao.ui.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.CompanyKeyAdapter;
import com.yifarj.yifadinghuobao.database.AppDatabase;
import com.yifarj.yifadinghuobao.database.model.ServerConfigInfoModel;
import com.yifarj.yifadinghuobao.database.model.ServerConfigInfoModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GetInfoEntity;
import com.yifarj.yifadinghuobao.model.entity.MettingCodeEntity;
import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.model.entity.PasswordLoginEntity;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.me.ForgetPasswordActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.PhoneFormatCheckUtils;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.LoadingDialog;

import java.util.List;
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
    @BindView(R.id.tvForgetPwd)
    TextView tvForgetPwd;
    @BindView(R.id.llPwd)
    LinearLayout llPwd;
    @BindView(R.id.tvLoginCutover)
    TextView tvLoginCutover;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.llPassword)
    LinearLayout llPassword;
    @BindView(R.id.etGetInfo)
    EditText etGetInfo;
    @BindView(R.id.ivGetMore)
    ImageView ivGetMore;

    private final int MAX_COUNT_TIME = 60;
    private String mettingCode;
    private Disposable mDisposable;
    private Observable<Long> mObservableCountTime;
    private Consumer<Long> mConsumerCountTime;
    private boolean isPwdLogin = false;
    private List<ServerConfigInfoModel> mServerConfigInfoModels;
    private ListPopupWindow mListPopupWindow;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    public void initViews(Bundle savedInstanceState) {
        RXSQLite.rx(SQLite.select().from(ServerConfigInfoModel.class))
                .queryList()
                .subscribe(new Consumer<List<ServerConfigInfoModel>>() {
                    @Override
                    public void accept(@NonNull List<ServerConfigInfoModel> serverConfigInfoModels) throws Exception {
                        if (serverConfigInfoModels != null && serverConfigInfoModels.size() > 0) {
                            mServerConfigInfoModels = serverConfigInfoModels;
                        }
                    }
                });

        isPwdLogin = PreferencesUtil.getBoolean("LoginType", false);
        initLoginUi();
        String name = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
        if (name != null) {
            etName.setText(name);
            etName.setSelection(0, name.length());
        }
        etGetInfo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideInputMethod();
                    getServerConfig();
                    return true;
                }
                return false;
            }
        });
        if (mServerConfigInfoModels != null && mServerConfigInfoModels.size() > 0) {
            String companyKey = PreferencesUtil.getString(ApiConstants.CPreference.COMPANY_KEY);
            if (!TextUtils.isEmpty(companyKey)) {
                etGetInfo.setText(companyKey);
                etGetInfo.setSelection(companyKey.length());
            }
            if (mServerConfigInfoModels.size() == 1) {
                if (TextUtils.isEmpty(companyKey)) {
                    etGetInfo.setText(mServerConfigInfoModels.get(0).CompanyKey);
                    etGetInfo.setSelection(mServerConfigInfoModels.get(0).CompanyKey.length());
                }
                ivGetMore.setVisibility(View.GONE);
            } else {
                if (TextUtils.isEmpty(companyKey)) {
                    etGetInfo.setText(mServerConfigInfoModels.get(mServerConfigInfoModels.size() - 1).CompanyKey);
                    etGetInfo.setSelection(mServerConfigInfoModels.get(mServerConfigInfoModels.size() - 1).CompanyKey.length());
                }
                mListPopupWindow = new ListPopupWindow(LoginActivity.this);
                mListPopupWindow.setAdapter(new CompanyKeyAdapter(LoginActivity.this, mServerConfigInfoModels));
                mListPopupWindow.setAnchorView(etGetInfo);
                mListPopupWindow.setModal(true);
                mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        etGetInfo.setText(mServerConfigInfoModels.get(i).CompanyKey);
                        etGetInfo.setSelection(mServerConfigInfoModels.get(i).CompanyKey.length());
                        mListPopupWindow.dismiss();
                        hideInputMethod();
                        getServerConfig();
                    }
                });
                clicks(ivGetMore)
                        .compose(bindToLifecycle())
                        .subscribe(new Consumer<Object>() {

                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                if (mListPopupWindow.isShowing()) {
                                    mListPopupWindow.dismiss();
                                } else {
                                    mListPopupWindow.show();
                                }
                            }
                        });
            }
        } else {
            ivGetMore.setVisibility(View.GONE);
        }


        tvLoginCutover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLoginUi();
            }
        });


        clicks(btnLogin)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (isPwdLogin) {
                            onPwdLogin();
                        } else {
                            login();
                        }
                    }
                });

        clicks(tvForgetPwd)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));

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
                        if (domain == null || port == null || ip == null || ("0").equals(accountId)) {
                            onConfigureClicked();
                        } else {
                            if (PhoneFormatCheckUtils.isPhone(phoneNumber)) {
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

    private void refreshLoginUi() {
        if (!isPwdLogin) {
            llPwd.setVisibility(View.GONE);
            llPassword.setVisibility(View.VISIBLE);
            tvLoginCutover.setText(R.string.verification_code_login);
            isPwdLogin = true;
        } else {
            llPwd.setVisibility(View.VISIBLE);
            llPassword.setVisibility(View.GONE);
            tvLoginCutover.setText(R.string.password_login);
            isPwdLogin = false;
        }
    }

    private void initLoginUi() {
        if (isPwdLogin) {
            llPwd.setVisibility(View.GONE);
            llPassword.setVisibility(View.VISIBLE);
            tvLoginCutover.setText(R.string.verification_code_login);
        } else {
            llPwd.setVisibility(View.VISIBLE);
            llPassword.setVisibility(View.GONE);
            tvLoginCutover.setText(R.string.password_login);
        }
    }


    public void hideInputMethod() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getServerConfig() {
        String key = etGetInfo.getText().toString().trim();
        if (!TextUtils.isEmpty(key)) {
            RetrofitHelper.getInfoApi()
                    .getInfo(key)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GetInfoEntity>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull GetInfoEntity entity) {
                            if (!entity.HasError && entity.Value != null) {
                                PreferencesUtil.putString(ApiConstants.CPreference.ACCOUNT_ID, entity.Value.AccsetId == null ? "" : entity.Value.AccsetId);
                                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PORT, entity.Value.Port == null ? "" : entity.Value.Port);
                                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_IP, entity.Value.Address == null ? "" : entity.Value.Address);
                                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_DOMAIN, entity.Value.Url == null ? "" : entity.Value.Url);
                                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_KEY_CODE, entity.Value.KeyCode == null ? "" : entity.Value.KeyCode);
                                PreferencesUtil.putString(ApiConstants.CPreference.COMPANY_KEY, key);

                                ToastUtils.showShortSafe("获取服务器信息成功，可以登录啦！");

                                if (!TextUtils.isEmpty(etName.getText().toString())) {
                                    if (!isPwdLogin) {
                                        etPwd.requestFocus();
                                        showKeyboard(LoginActivity.this, etPwd);
                                    } else {
                                        etPassword.requestFocus();
                                        showKeyboard(LoginActivity.this, etPassword);
                                    }
                                } else {
                                    etName.requestFocus();
                                    showKeyboard(LoginActivity.this, etName);
                                }
                                if (!TextUtils.isEmpty(entity.Value.CompanyKey) && !TextUtils.isEmpty(entity.Value.Company)) {
                                    saveServerConfigInfo(entity.Value.CompanyKey, entity.Value.Company);
                                }
                            } else {
                                ToastUtils.showShortSafe(entity.Information == null ? "" : entity.Information);
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            CzechYuanDialog mDialog = new CzechYuanDialog(LoginActivity.this, R.style.CzechYuanDialog);
                            mDialog.setContent("获取服务器信息失败，是否重新获取？");
                            mDialog.setConfirmClickListener(view1 -> getServerConfig());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }

    }

    private void showKeyboard(Activity activity, EditText et) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(et, 0);
        }
    }

    /**
     * 验证码登录
     */
    private void login() {
        DataSaver.setCurrentLoginType(false);
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
                        if (!mettingLoginEntity.HasError) {
                            LogUtils.e("mettingLoginEntity success!");
                            DataSaver.setMettingCustomerInfo(mettingLoginEntity.Value);
                            PreferencesUtil.putInt("TraderId", mettingLoginEntity.Value.TraderId);
                            PreferencesUtil.putInt("Id", mettingLoginEntity.Value.Id);
                            PreferencesUtil.putString("ContactName", mettingLoginEntity.Value.ContactName);
                            PreferencesUtil.putBoolean(ApiConstants.CPreference.IS_PWD_LOGIN, false);
                            String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
                            if (!StringUtils.isEmpty(userName)) {
                                if (!userName.equals(name)) {
                                    FlowManager.getDatabase(AppDatabase.class).reset(LoginActivity.this);
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
                                                loadingDialog.dismiss();
                                                LogUtils.e("获取Trader onNext");
                                                DataSaver.setPriceSystemId(traderEntity.Value.DefaultPriceSystemId);
                                                DataSaver.setTraderInfo(traderEntity.Value);
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
//                                                getPassword();
                                            } else {
                                                ToastUtils.showShortSafe(getString(R.string.login_failure) + traderEntity.Information.toString());
                                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                                    //停止倒计时
                                                    mDisposable.dispose();
                                                    //重新订阅
                                                    mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                    try {
                                                        loadingDialog.dismiss();
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
                                            ToastUtils.showShortSafe(getString(R.string.login_failure) + e.getMessage());
                                            if (mDisposable != null && !mDisposable.isDisposed()) {
                                                //停止倒计时
                                                mDisposable.dispose();
                                                //重新订阅
                                                mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                                try {
                                                    loadingDialog.dismiss();
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
                                            loadingDialog.dismiss();
                                        }
                                    });

                        } else {
                            ToastUtils.showShortSafe(getString(R.string.login_failure) + mettingLoginEntity.Information);
                            if (mDisposable != null && !mDisposable.isDisposed()) {
                                //停止倒计时
                                mDisposable.dispose();
                                //重新订阅
                                mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
                                try {
                                    RxTextView.text(tvCode).accept("发送验证码");
                                    loadingDialog.dismiss();
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
                        LogUtils.e("登录 onError" + e.getMessage());
                        loadingDialog.dismiss();
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
                        //                        loadingDialog.dismiss();
                    }
                });
    }


    private void onConfigureClicked() {
        ToastUtils.showShortSafe("请先获取服务器信息");
        etGetInfo.requestFocus();
        showKeyboard(LoginActivity.this, etGetInfo);
//        Intent intent = new Intent(this, ConnectServerActivity.class);
//        startActivity(intent);
    }

    /**
     * 密码登录
     */
    private void onPwdLogin() {
        DataSaver.setCurrentLoginType(true);
        String phoneNumber = etName.getText().toString().trim();
        String pwd = etPassword.getText().toString();

        String accountId = PreferencesUtil.getString(ApiConstants.CPreference.ACCOUNT_ID);
        String port = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT);
        String ip = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP);
        String domain = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
        if (domain == null || port == null || ip == null || ("0").equals(accountId)) {
            onConfigureClicked();
        } else {
            if (!PhoneFormatCheckUtils.isPhone(phoneNumber)) {
                ToastUtils.showShortSafe("请输入正确的手机号");
                return;
            }
            if (!CommonUtil.isNetworkAvailable(LoginActivity.this)) {
                ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                ToastUtils.showShortSafe("请输入密码");
                return;
            }
            if (TextUtils.isEmpty(phoneNumber)) {
                ToastUtils.showShortSafe("请输入手机号");
                return;
            }
            String domain1 = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
            AppInfoUtil.resetBaseUrl(domain1, false);

            LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this, getString(R.string.log_in));
            RetrofitHelper.getPasswordLoginApi()
                    .passwordLogin(ip, port, accountId, phoneNumber, pwd, AppInfoUtil.getDeviceId(this), AppInfoUtil.getIPAddress())
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PasswordLoginEntity>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            loadingDialog.show();
                        }

                        @Override
                        public void onNext(@NonNull PasswordLoginEntity passwordLoginEntity) {
                            if (!passwordLoginEntity.HasError && passwordLoginEntity.Value.Value != null) {
                                MettingLoginEntity.ValueEntity loginEntity = new MettingLoginEntity.ValueEntity();
                                loginEntity.Id = passwordLoginEntity.Value.Value.Id;
                                loginEntity.TraderId = passwordLoginEntity.Value.Value.TraderId;
                                loginEntity.ContactName = passwordLoginEntity.Value.Value.ContactName;
                                loginEntity.Phone = passwordLoginEntity.Value.Value.Phone;
                                loginEntity.Mobile = passwordLoginEntity.Value.Value.Mobile;
                                loginEntity.Email = passwordLoginEntity.Value.Value.Email;
                                loginEntity.Address = passwordLoginEntity.Value.Value.Address;
                                loginEntity.Fax = passwordLoginEntity.Value.Value.Fax;
                                loginEntity.ModifiedTime = passwordLoginEntity.Value.Value.ModifiedTime;
                                loginEntity.modify_date = passwordLoginEntity.Value.Value.modify_date;
                                loginEntity.oper_date = passwordLoginEntity.Value.Value.oper_date;
                                loginEntity.vip_start_date = passwordLoginEntity.Value.Value.vip_start_date;
                                loginEntity.vip_end_date = passwordLoginEntity.Value.Value.vip_end_date;
                                loginEntity.birthday = passwordLoginEntity.Value.Value.birthday;
                                loginEntity.CreatedTime = passwordLoginEntity.Value.Value.CreatedTime;
                                loginEntity.card_id = passwordLoginEntity.Value.Value.card_id;
                                loginEntity.card_flowno = passwordLoginEntity.Value.Value.card_flowno;
                                loginEntity.card_type = passwordLoginEntity.Value.Value.card_type;
                                loginEntity.vip_sex = passwordLoginEntity.Value.Value.vip_sex;
                                loginEntity.card_status = passwordLoginEntity.Value.Value.card_status;
                                loginEntity.oper_id = passwordLoginEntity.Value.Value.oper_id;
                                loginEntity.acc_num = passwordLoginEntity.Value.Value.acc_num;
                                loginEntity.dec_num = passwordLoginEntity.Value.Value.dec_num;
                                loginEntity.res_num = passwordLoginEntity.Value.Value.res_num;
                                loginEntity.memo = passwordLoginEntity.Value.Value.memo;
                                loginEntity.homeplace = passwordLoginEntity.Value.Value.homeplace;
                                loginEntity.marriage = passwordLoginEntity.Value.Value.marriage;
                                loginEntity.photo_file = passwordLoginEntity.Value.Value.photo_file;
                                loginEntity.nation = passwordLoginEntity.Value.Value.nation;
                                loginEntity.height = passwordLoginEntity.Value.Value.height;
                                loginEntity.weight = passwordLoginEntity.Value.Value.weight;
                                loginEntity.blood_type = passwordLoginEntity.Value.Value.blood_type;
                                loginEntity.favor = passwordLoginEntity.Value.Value.favor;
                                loginEntity.social_id = passwordLoginEntity.Value.Value.social_id;
                                loginEntity.use_num = passwordLoginEntity.Value.Value.use_num;
                                loginEntity.consum_amt = passwordLoginEntity.Value.Value.consum_amt;
                                loginEntity.degree = passwordLoginEntity.Value.Value.degree;
                                loginEntity.CardSerialNumbe = passwordLoginEntity.Value.Value.CardSerialNumbe;
                                loginEntity.DepartmentId = passwordLoginEntity.Value.Value.DepartmentId;
                                loginEntity.CreatedUserId = passwordLoginEntity.Value.Value.CreatedUserId;
                                loginEntity.CreatedDevice = passwordLoginEntity.Value.Value.CreatedDevice;
                                loginEntity.ModifiedUserId = passwordLoginEntity.Value.Value.ModifiedUserId;
                                loginEntity.VerifyCode = passwordLoginEntity.Value.Value.VerifyCode;
                                loginEntity.FailureTime = passwordLoginEntity.Value.Value.FailureTime;
                                loginEntity.CodeSendTimes = passwordLoginEntity.Value.Value.CodeSendTimes;
                                DataSaver.setMettingCustomerInfo(loginEntity);
                                LogUtils.e("passwordLoginEntity success!");
                                DataSaver.setPasswordCustomerInfo(passwordLoginEntity.Value.Value);
                                PreferencesUtil.putInt("TraderId", passwordLoginEntity.Value.Value.TraderId);
                                PreferencesUtil.putInt("Id", passwordLoginEntity.Value.Value.Id);
                                PreferencesUtil.putString("ContactName", passwordLoginEntity.Value.Value.ContactName);
                                AppInfoUtil.restoreToken(passwordLoginEntity.Tag);
                                String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
                                if (!StringUtils.isEmpty(userName)) {
                                    if (!userName.equals(phoneNumber)) {
                                        FlowManager.getDatabase(AppDatabase.class).reset(LoginActivity.this);
                                    }
                                }
                                PreferencesUtil.putString(ApiConstants.CPreference.USER_NAME, phoneNumber);
                                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PASSWORD, pwd);
                                PreferencesUtil.putBoolean(phoneNumber, true);
                                PreferencesUtil.putBoolean(ApiConstants.CPreference.IS_PWD_LOGIN, true);

                                LogUtils.e("登录 onComplete");
                                LogUtils.e("traderId " + passwordLoginEntity.Value.Value.TraderId);
                                RetrofitHelper.getTraderApi()
                                        .fetchTrader("Trader", "Id =" + passwordLoginEntity.Value.Value.TraderId, "", AppInfoUtil.getToken())
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
                                                    loadingDialog.dismiss();
                                                    LogUtils.e("获取Trader onNext");
                                                    DataSaver.setPriceSystemId(traderEntity.Value.DefaultPriceSystemId);
                                                    DataSaver.setTraderInfo(traderEntity.Value);

                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    ToastUtils.showShortSafe(getString(R.string.login_failure) + traderEntity.Information.toString());
                                                    loadingDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                LogUtils.e("获取Trader onError" + e.getMessage());
                                                ToastUtils.showShortSafe(getString(R.string.login_failure) + e.getMessage());
                                                loadingDialog.dismiss();
                                            }

                                            @Override
                                            public void onComplete() {
                                                loadingDialog.dismiss();
                                            }
                                        });

                            } else {
                                ToastUtils.showShortSafe(getString(R.string.login_failure) + passwordLoginEntity.Information);
                                loadingDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastUtils.showShortSafe("请检查，网络是否畅通");
                            LogUtils.e("登录 onError" + e.getMessage());
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    private void saveServerConfigInfo(String mCompanyKey, String mCompany) {
        RXSQLite.rx(SQLite.select().from(ServerConfigInfoModel.class)
                .where(ServerConfigInfoModel_Table.CompanyKey.eq(mCompanyKey)))
                .queryList()
                .subscribe(new Consumer<List<ServerConfigInfoModel>>() {
                    @Override
                    public void accept(@NonNull List<ServerConfigInfoModel> models) throws Exception {
                        if (models == null || models.size() == 0) {
                            ServerConfigInfoModel serverConfigInfoModel = new ServerConfigInfoModel();
                            serverConfigInfoModel.Company = mCompany;
                            serverConfigInfoModel.CompanyKey = mCompanyKey;
                            serverConfigInfoModel.insert().subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(@NonNull Long aLong) throws Exception {
                                    LogUtils.e("商户编号插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                                }
                            });
                        }
                    }
                });
    }

}
