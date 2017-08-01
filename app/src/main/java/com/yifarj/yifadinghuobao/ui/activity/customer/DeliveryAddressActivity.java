package com.yifarj.yifadinghuobao.ui.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.AddressListAdapter;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * DeliveryAddressActivity
 *
 * @auther Czech.Yuan
 * @date 2017/7/31 15:16
 */
public class DeliveryAddressActivity extends BaseActivity {

    @BindView(R.id.btnAdd)
    Button btnAdd;

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView emptyView;

    private AddressListAdapter mAddressListAdapter;
    private List<TraderEntity.ValueEntity.TraderDeliveryAddressListEntity> mItemData = new ArrayList<>();
    private TraderEntity.ValueEntity traderInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_delivery_adress;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        init();
        loadData();
        initRecyclerView();
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAddressListAdapter = new AddressListAdapter(mRecyclerView, mItemData);
        mRecyclerView.setAdapter(mAddressListAdapter);
    }

    private void init() {
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RxView.clicks(btnAdd)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(DeliveryAddressActivity.this, EditAddressActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void loadData() {
        traderInfo = DataSaver.getTraderInfo();
        mItemData = traderInfo.TraderDeliveryAddressList;
    }

    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setEmptyImage(R.drawable.ic_data_empty);
        emptyView.setEmptyText("暂无数据");
    }

    public void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }
}
