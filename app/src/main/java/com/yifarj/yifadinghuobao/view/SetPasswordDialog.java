package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.ApiConstants;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.common.MainActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.CommonUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017-09-16.
 */

public class SetPasswordDialog extends BaseDialog {

    private String type;
    public MainActivity mActivity;

    public static SetPasswordDialog newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        SetPasswordDialog dialog = new SetPasswordDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        type = bundle.getString("type");
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_set_password;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        holder.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ToastUtils.showShort("您也可以去“我的”设置密码哦");
            }
        });
        holder.setOnClickListener(R.id.tv_ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_pwd = holder.getView(R.id.et_pwd);
                EditText et_pwd2 = holder.getView(R.id.et_pwd2);
                String pwd = et_pwd.getText().toString().trim();
                String pwd2 = et_pwd2.getText().toString().trim();
                if (pwd.isEmpty() || pwd2.isEmpty()) {
                    ToastUtils.showShort("密码不能为空");
                    return;
                }
                if (!pwd.equals(pwd2)) {
                    ToastUtils.showShort("两次密码不一致");
                    return;
                }
                setPassword(pwd2);
                dialog.dismiss();
                PreferencesUtil.putBoolean(ApiConstants.CPreference.SET_PASSWORD, true);
                PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_PASSWORD, pwd2);
                Toast.makeText(getContext(), "设置密码成功，下次可以使用密码登陆哦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideInputMethod() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager service = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            service.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setPassword(String password) {
        if (!CommonUtil.isNetworkAvailable(getContext())) {
            ToastUtils.showShort("没有网络，请检查网络设置");
            return;
        }
        String userName = PreferencesUtil.getString(ApiConstants.CPreference.USER_NAME, "");
        mActivity = new MainActivity();
        for (int i = 0; i < DataSaver.getTraderInfo().TraderContactList.size(); i++) {
            if (userName.equals(DataSaver.getTraderInfo().TraderContactList.get(i).Mobile)) {
                TraderEntity.ValueEntity.TraderContactListEntity entity = DataSaver.getTraderInfo().TraderContactList.get(i);
                entity.card_password = password;
                RetrofitHelper.saveTraderApi().saveTrader("Trader", ZipUtil.gzip(JsonUtils.serialize(DataSaver.getTraderInfo())), "", AppInfoUtil.getToken())
                        .compose(mActivity.bindToLifecycle())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TraderEntity>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull TraderEntity traderEntity) {
                                if (!traderEntity.HasError) {
                                    DataSaver.setTraderInfo(traderEntity.Value);
                                    hideInputMethod();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                ToastUtils.showShort("密码设置失败");
                                Log.i("SetPasswrodDialog", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        }
    }
}
