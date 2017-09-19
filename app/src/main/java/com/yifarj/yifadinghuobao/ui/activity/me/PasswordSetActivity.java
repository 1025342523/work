package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.TitleView;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zydx-pc on 2017/8/28.
 */

public class PasswordSetActivity extends BaseActivity {
    @BindView(R.id.titleView)
    TitleView titleView;
    @BindView(R.id.rl_setPassword)
    RelativeLayout rl_setPassword;
    @BindView(R.id.rl_modifyPassword)
    RelativeLayout rl_modifyPassword;

    private boolean isOldPwd = false;
    private int traderId = 0;
    public static boolean isSetPwd = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_password_set;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else if (DataSaver.getPasswordCustomerInfo() != null) {
            traderId = DataSaver.getPasswordCustomerInfo().TraderId;
        } else {
            traderId = PreferencesUtil.getInt("TraderId", 0);
        }
        //是否是密码登录
        if(PreferencesUtil.getBoolean(ApiConstants.CPreference.IS_PWD_LOGIN,false)){
            rl_setPassword.setVisibility(View.GONE);
        }else{
            if(isSetPwd){
               rl_setPassword.setVisibility(View.GONE);
            }else if(PreferencesUtil.getBoolean(PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME),false)){
                rl_setPassword.setVisibility(View.GONE);
            }
        }

        getOldPassword();
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RxView.clicks(rl_setPassword)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(PasswordSetActivity.this, SetPasswordActivity.class);
                        intent.putExtra("isOldPwd", isOldPwd);
                        startActivity(intent);
                        finish();
                    }
                });
        RxView.clicks(rl_modifyPassword)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        startActivity(new Intent(PasswordSetActivity.this, ModifyPasswordActivity.class));
                    }
                });
    }

    /**
     * 获取登录密码
     */
    private void getOldPassword() {
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
                                    if (traderContactEntity.card_password != null) {
                                        isOldPwd = true;
                                        LogUtils.e("旧密码获取成功" + traderContactEntity.card_password);
                                    } else {
                                        LogUtils.e("旧密码为空");
                                    }
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
     * 传递服务器是否设置密码
     */
    public static class IsSetPwd{
        public IsSetPwd(boolean isSetPwd){
            PasswordSetActivity.isSetPwd = isSetPwd;
        }
    }

}
