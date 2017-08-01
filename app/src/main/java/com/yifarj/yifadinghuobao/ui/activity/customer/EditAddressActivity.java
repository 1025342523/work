package com.yifarj.yifadinghuobao.ui.activity.customer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * EditAddressActivity
 *
 * @auther Czech.Yuan
 * @date 2017/7/31 17:31
 */
public class EditAddressActivity extends BaseActivity {

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.etAddress)
    EditText etAddress;


    @BindView(R.id.tvSetDefault)
    TextView tvSetDefault;

    private String address;

    private boolean isSelected = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_delivery_adress;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        init();
        loadData();
    }


    private void init() {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RxView.clicks(btnSave)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {

                    }
                });
        RxView.clicks(tvSetDefault)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Drawable drawable;
                        if (isSelected) {
                            drawable = getResources().getDrawable(R.drawable.ic_check_default_address_selected);
                        } else {
                            drawable = getResources().getDrawable(R.drawable.ic_check_default_address);
                        }
                        tvSetDefault.setCompoundDrawables(drawable, null, null, null);
                    }
                });
    }

    @Override
    public void loadData() {
        address = getIntent().getStringExtra("TraderDeliveryAddress");
        if (TextUtils.isEmpty(address)) {
            etAddress.setText(address);
        }

    }


}
