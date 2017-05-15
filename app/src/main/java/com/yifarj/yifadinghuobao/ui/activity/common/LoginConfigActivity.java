package com.yifarj.yifadinghuobao.ui.activity.common;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.request.RequestListener;
import com.jakewharton.rxbinding2.view.RxView;
import com.wx.wheelview.widget.WheelView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.AccountListEntity;
import com.yifarj.yifadinghuobao.network.ApiApiConstants;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CustomEditItem;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.WheelViewBottomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 登录配置页面
 */
public class LoginConfigActivity extends BaseActivity {

    //    private static final int MSG_GET_ACCOUNT_ID = 10;

    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.ciMainServer)
    CustomEditItem ciMainServer;
    @BindView(R.id.ciIp)
    CustomEditItem ciIp;
    @BindView(R.id.ciPort)
    CustomEditItem ciPort;
    @BindView(R.id.ciKeyCode)
    CustomEditItem ciKeyCode;
    @BindView(R.id.ciAccountNum)
    CustomEditItem ciAccountNum;
    @BindView(R.id.llGetAccountIds)
    LinearLayout llGetAccountIds;
    private List<AccountListEntity.ValueEntity> mAccountList;
    private int mAccountId;
    private int mPosition;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login_config;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        RxView.clicks(llGetAccountIds)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (!CommonUtil.isNetworkAvailable(LoginConfigActivity.this)) {
                            ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                            return;
                        }
                        getAccountIdsClick();
                    }
                });
        init();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            onSave();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    private void init() {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });

        ciMainServer.getEditText().setMaxLines(1);
        ciMainServer.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        ciMainServer.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);

        if (PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN) == null) {
                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_DOMAIN, "http://192.168.1.10:8888/yifa.asmx");
        }

        ciMainServer.getEditText().setText(PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN, ""));

        if (PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP) == null) {
                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_IP, "127.0.0.1");
        }

        String ip = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_IP, "");
        ciIp.getEditText().setMaxLines(1);
        ciIp.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        ciIp.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        ciIp.getEditText().setText(ip);

        ciPort.getEditText().setMaxLines(1);
        ciPort.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        ciPort.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);

        if (PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT) == null) {
                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PORT, "5218");
        }

        String port = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_PORT, "");
        ciPort.getEditText().setText(port);

        ciKeyCode.getEditText().setMaxLines(1);
        ciKeyCode.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ciKeyCode.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);

        String keyCode = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_KEY_CODE, "");
        ciKeyCode.getEditText().setText(keyCode);

        mAccountId = PreferencesUtil.getInt(ApiConstants.CPreference.ACCOUNT_ID, 0);
        if (mAccountId != 0) {
            getAccountIds(port, ip, keyCode, false);
        }


        ciAccountNum.getEditText().setMaxLines(1);
        ciAccountNum.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        ciAccountNum.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        ciAccountNum.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountList != null && mAccountList.size() > 0) {
                    showAccountNumDialog();
                } else {
                    getAccountIdsClick(true);
                }
            }
        });
    }

    private void showAccountNumDialog() {
        if (mAccountList != null && mAccountList.size() > 0) {
            WheelViewBottomDialog dialog = new WheelViewBottomDialog(LoginConfigActivity.this);
            List<String> wheelData = new ArrayList<>();
            for (AccountListEntity.ValueEntity item : mAccountList) {
                wheelData.add(item.Name);
            }
            dialog.setWheelData(wheelData);
            dialog.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    mPosition = position;
                }
            });
            dialog.setOkBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountListEntity.ValueEntity item = mAccountList.get(mPosition);
                    ciAccountNum.getEditText().setText(item.Name);
                    mAccountId = item.Id;
                }
            });
            dialog.setTitle(getString(R.string.select_account));
            dialog.show();
        } else {
            ToastUtils.showShortSafe(getString(R.string.account_nothing));
        }
    }

    private void getAccountIdsClick() {
        getAccountIdsClick(false);
    }

    public void getAccountIdsClick(boolean showDialog) {
        getAccountIdIfValid(
                ciIp.getEditText().getText().toString(),
                ciPort.getEditText().getText().toString(),
                ciKeyCode.getEditText().getText().toString(), showDialog);
    }

    private void getAccountIdIfValid(String ip, String port, String keycode, boolean showDialog) {
        port = port.trim();
        ip = ip.trim();
        keycode = keycode.trim();
        if (port.length() > 0 && ip.length() > 0) {
            mAccountId = 0;
            getAccountIds(port, ip, keycode, showDialog);
        } else {
            ToastUtils.showShortSafe(getString(R.string.parm_none));
        }
    }

    private void getAccountIds(String port, String ip, String keycode, final boolean showDialog) {
        try {
            if (!CommonUtil.isNetworkAvailable(LoginConfigActivity.this)) {
                ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                return;
            }
            AppInfoUtil.resetBaseUrl(ciMainServer.getEditText().getText().toString().trim(), true);
            RequestParams params = new RequestParams();
            params.put("Host", ip);
            params.put("Port", port);
            params.put("KeyCode", keycode);
            Requester.post(ApiConstants.CUrl.BASE_URL + ApiConstants.CUrl.GET_ACCOUNT_LIST, params, AccountListEntity.class, new RequestListener<AccountListEntity>() {
                @Override
                public void onStart() {
                    super.onStart();
                    llGetAccountIds.setEnabled(false);
                }

                @Override
                public void onSuccess(AccountListEntity entity) {
                    super.onSuccess(entity);
                    if (!entity.HasError) {
                        mAccountList = new ArrayList<>();
                        if (entity.Value != null && entity.Value.size() > 0) {
                            for (AccountListEntity.ValueEntity entityItem :
                                    entity.Value) {
                                if (entityItem.Visible) {
                                    mAccountList.add(entityItem);
                                }
                            }
                            if (mAccountList.size() > 0) {
                                if (mAccountId == 0) {
                                    ciAccountNum.getEditText().setText(mAccountList.get(0).Name);
                                    mAccountId = mAccountList.get(0).Id;
                                } else {
                                    for (AccountListEntity.ValueEntity accountItem :
                                            mAccountList) {
                                        if (accountItem.Id == mAccountId) {
                                            ciAccountNum.getEditText().setText(accountItem.Name);
                                        }
                                    }
                                }
                            }
                            if (showDialog) {
                                showAccountNumDialog();
                            }
                        } else {
                            ToastUtil.showToastShort(getString(R.string.get_account_none));
                        }
                    } else {
                        ToastUtil.showToastShort(getString(R.string.get_account_none) + ":" + entity.Information);
                    }
                }

                @Override
                public void onFailure(int errorCode, String errorMsg, Throwable throwable) {
                    super.onFailure(errorCode, errorMsg, throwable);
                    ToastUtil.showToastShort(getString(R.string.get_account_none));
                    llGetAccountIds.setEnabled(true);
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    super.onError(errorCode, errorMsg);
                    ToastUtil.showToastShort(getString(R.string.get_account_none));
                    llGetAccountIds.setEnabled(true);
                }

                @Override
                public void onFinish(boolean success) {
                    super.onFinish(success);
                    llGetAccountIds.setEnabled(true);
                }
            });
        } catch (Exception e) {
            ToastUtil.showToastShort(getString(R.string.url_is_error));
        }
    }

    public void onSave() {
        PreferencesUtil.putInt(ApiConstants.CPreference.ACCOUNT_ID, mAccountId);
        PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_IP, ciIp.getEditText().getText().toString().trim());
        PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PORT, ciPort.getEditText().getText().toString().trim());
        PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_DOMAIN, ciMainServer.getEditText().getText().toString().trim());
        PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_KEY_CODE, ciKeyCode.getEditText().getText().toString().trim());
        finish();
    }


}
