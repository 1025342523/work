package com.yifarj.yifadinghuobao.ui.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.AppDatabase;
import com.yifarj.yifadinghuobao.model.entity.AccountListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.yifarj.yifadinghuobao.utils.PreferencesUtil.putString;

public class ConnectServerActivity extends BaseActivity {

    private static final int REQUEST_BARCODE = 10;

    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.llGetAccountIds)
    LinearLayout llGetAccountIds;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.llTest)
    LinearLayout llTest;
    @BindView(R.id.tvTestName)
    TextView tvTestName;

    private String mAccountId;
    private String mIp, mPort, mUrl, mKeyCode;


    @Override
    public int getLayoutId() {
        return R.layout.activity_connect_server;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titleView.setLeftIconClickListener(view -> finish());
        RxView.clicks(llGetAccountIds)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(ConnectServerActivity.this, ScanQrcodeActivity.class);
                        startActivityForResult(intent, REQUEST_BARCODE);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.fake_fade_out);
                    }
                });
        RxView.clicks(llTest)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        getAccountIds(mPort, mIp, "");
                    }
                });
    }

    private void getAccountIds(String port, String ip, String keycode) {
        if (!CommonUtil.isNetworkAvailable(ConnectServerActivity.this)) {
            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
            return;
        }
        if (ip == null || port == null || mUrl == null) {
            ToastUtils.showShortSafe("请先获取服务器信息");
            return;
        }
        RetrofitHelper.getAccountAPI()
                .getAccountList(ip, port, keycode)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccountListEntity>() {


                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtils.e("onSubscribe", "订阅");
                    }

                    @Override
                    public void onNext(AccountListEntity valueEntity) {
                        LogUtils.e("onNext", valueEntity.toString());
                        if (!valueEntity.HasError) {
//                            ToastUtils.showShortSafe("测试连接成功");
                            CzechYuanDialog mDialog = new CzechYuanDialog(ConnectServerActivity.this, R.style.CzechYuanDialog);
                            mDialog.setContent("恭喜您，测试连接成功，去登录吧");
                            mDialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String oldAccountID = PreferencesUtil.getString(ApiConstants.CPreference.ACCOUNT_ID);
                                    String oldUrl = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN);
                                    if (!StringUtils.isEmpty(oldAccountID)) {
                                        if (!oldAccountID.equals(mAccountId)) {
                                            FlowManager.getDatabase(AppDatabase.class).reset(ConnectServerActivity.this);
                                        }
                                    }
                                    if (!StringUtils.isEmpty(oldUrl)) {
                                        if (!oldUrl.equals(mUrl)) {
                                            FlowManager.getDatabase(AppDatabase.class).reset(ConnectServerActivity.this);
                                        }
                                    }
                                    onSave();
                                    finish();
                                }
                            });
                        } else {
                            ToastUtils.showShortSafe("测试连接失败");
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtils.showShortSafe("测试连接失败");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("onComplete", "完成");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_BARCODE) {

                if (data != null) {
                    mUrl = "http://120.77.216.91:8888/351/yifa.asmx";
                    mIp = "127.0.0.1";
                    mPort = "1218";
                    mAccountId = "75";
                    mKeyCode = "yidinghuo";
//                    try {
//                        String serverInfo = data.getStringExtra("barcode");
//                        String tempServerInfo = serverInfo.substring(7, serverInfo.length());
//                        LogUtils.e(tempServerInfo);
//                        String[] connectInfoArray = tempServerInfo.split("\\?");
//                        String server1 = connectInfoArray[0];
//                        String server2 = server1.substring(0, server1.length() - 13);
//                        mUrl = "http://" + server2 + "yifa.asmx";
//                        LogUtils.e(mUrl);
//                        String connectInfo2 = connectInfoArray[1];
//                        String[] connectInfo3 = connectInfo2.split("&");
//                        mIp = connectInfo3[0].substring(5, connectInfo3[0].length());
//                        LogUtils.e(mIp);
//                        mPort = connectInfo3[1].substring(5, connectInfo3[1].length());
//                        LogUtils.e(mPort);
//                        mAccountId = connectInfo3[2].substring(9, connectInfo3[2].length());
//                        LogUtils.e(mAccountId);
//                        if (connectInfo3.length > 3) {
//                            mKeyCode = connectInfo3[3].substring(8, connectInfo3[3].length());
//                        }
//                        LogUtils.e(mKeyCode);
                    putString(ApiConstants.CPreference.LOGIN_DOMAIN, mUrl);
//
                    CzechYuanDialog mDialog = new CzechYuanDialog(ConnectServerActivity.this, R.style.CzechYuanDialog);
                    mDialog.setContent("获取服务器信息成功，是否测试连接？");
                    mDialog.setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getAccountIds(mPort, mIp, mKeyCode);
                        }
                    });
//                    } catch (Exception e) {
//                        ToastUtils.showShortSafe("获取服务器信息失败，请重新获取");
//                    }
                } else {
                    ToastUtils.showShortSafe("未获取到服务器信息，请重新获取");
                }
            }
        }
    }

    public void onSave() {
        putString(ApiConstants.CPreference.ACCOUNT_ID, mAccountId);
        putString(ApiConstants.CPreference.LOGIN_IP, mIp);
        putString(ApiConstants.CPreference.LOGIN_PORT, mPort);
        putString(ApiConstants.CPreference.LOGIN_DOMAIN, mUrl);
        putString(ApiConstants.CPreference.LOGIN_KEY_CODE, mKeyCode);
    }
}
