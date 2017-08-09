package com.yifarj.yifadinghuobao.ui.fragment.mine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.ui.activity.common.AboutActivity;
import com.yifarj.yifadinghuobao.ui.activity.customer.DeliveryAddressActivity;
import com.yifarj.yifadinghuobao.ui.activity.me.ReturnFormActivity;
import com.yifarj.yifadinghuobao.ui.activity.me.ReturnProductActivity;
import com.yifarj.yifadinghuobao.ui.fragment.base.BaseFragment;
import com.yifarj.yifadinghuobao.view.CustomItem;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * TabMineFragment
 *
 * @auther Czech.Yuan
 * @date 2017/5/12 15:07
 */
public class TabMineFragment extends BaseFragment {

    @BindView(R.id.iv)
    ImageView iv;

    @BindView(R.id.tvUserPhone)
    TextView tvUserPhone;

    @BindView(R.id.ciAddress)
    CustomItem ciAddress;

    @BindView(R.id.ciReturn)
    CustomItem ciReturn;

    @BindView(R.id.ciReturnOrder)
    CustomItem ciReturnOrder;

    @BindView(R.id.rl_about)
    RelativeLayout about;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        if (DataSaver.getMettingCustomerInfo() != null) {
            tvUserPhone.setText(DataSaver.getMettingCustomerInfo().ContactName);
            RxView.clicks(about)
                    .compose(bindToLifecycle())
                    .subscribe(new Consumer<Object>() {

                        @Override
                        public void accept(@NonNull Object o) throws Exception {
                            startActivity(new Intent(getActivity(), AboutActivity.class));
                        }
                    });
        }
        RxView.clicks(ciAddress)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        startActivity(new Intent(getActivity(), DeliveryAddressActivity.class));
                    }
                });
        RxView.clicks(ciReturn)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        startActivity(new Intent(getActivity(), ReturnProductActivity.class));
                    }
                });
        RxView.clicks(ciReturnOrder)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        startActivity(new Intent(getActivity(), ReturnFormActivity.class));
                    }
                });
    }
}
